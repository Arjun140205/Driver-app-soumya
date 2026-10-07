import { Component, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService, OtpSendResponse } from '../../services/auth.service';
import { User } from '../../models/user.model';
import {
  EMAIL_REGEX,
  MOBILE_REGEX,
  OTP_REGEX,
  OTP_RESEND_SECONDS,
  PASSWORD_REGEX,
  PASSWORD_RULES
} from '../../utils/constants';
import { passwordsMatch } from '../../utils/validators';
import { serverMessage } from '../../utils/http-errors';

type Channel = 'email' | 'mobile';

/** Everything the page needs to know about the OTP of one channel (the e-mail or the mobile number). */
interface OtpState {
  sent: boolean;       // an OTP was sent, so the "enter OTP" box is shown
  verified: boolean;   // the OTP was entered correctly: "Verified" is shown
  sending: boolean;
  verifying: boolean;
  code: string;        // what the user typed into the OTP box
  target: string;      // the e-mail / number the OTP was sent to
  message: string;     // information shown under the field
  error: string;       // OTP errors shown under the field
  cooldown: number;    // seconds left before "Resend OTP" works again
}

function emptyOtp(): OtpState {
  return { sent: false, verified: false, sending: false, verifying: false, code: '', target: '', message: '', error: '', cooldown: 0 };
}

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnDestroy {
  signupForm: FormGroup;
  submitted: boolean = false;
  errorMessage: string = '';
  showSuccessPopup: boolean = false;

  passwordRules = PASSWORD_RULES;
  otp: { email: OtpState; mobile: OtpState } = { email: emptyOtp(), mobile: emptyOtp() };

  private timers: { [channel: string]: any } = {};
  private subscriptions = new Subscription();

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    // There is no role field: everybody who signs up is a customer (admins are created by an admin).
    this.signupForm = this.fb.group(
      {
        username: ['', [Validators.required, Validators.pattern(/\S/)]],
        email: ['', [Validators.required, Validators.pattern(EMAIL_REGEX)]],
        mobileNumber: ['', [Validators.required, Validators.pattern(MOBILE_REGEX)]],
        password: ['', [Validators.required, Validators.pattern(PASSWORD_REGEX)]],
        confirmPassword: ['', [Validators.required]]
      },
      { validators: passwordsMatch }
    );

    // Changing the e-mail / number after an OTP was sent throws that OTP away.
    this.subscriptions.add(this.f['email'].valueChanges.subscribe((value) => this.onTargetChanged('email', value)));
    this.subscriptions.add(this.f['mobileNumber'].valueChanges.subscribe((value) => this.onTargetChanged('mobile', value)));
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    Object.values(this.timers).forEach((timer) => clearInterval(timer));
  }

  get f() {
    return this.signupForm.controls;
  }

  isInvalid(field: string): boolean {
    const control = this.signupForm.get(field)!;
    return control.invalid && (control.touched || this.submitted);
  }

  get passwordMismatch(): boolean {
    return !!this.signupForm.errors?.['mismatch'] && !this.f['confirmPassword'].errors?.['required'];
  }

  get passwordTyped(): boolean {
    return !!this.f['password'].value || this.f['password'].touched || this.submitted;
  }

  ruleMet(rule: { test: (value: string) => boolean }): boolean {
    return rule.test(this.f['password'].value || '');
  }

  // ------------------------------------------------------------------ OTP

  private controlOf(channel: Channel) {
    return channel === 'email' ? this.f['email'] : this.f['mobileNumber'];
  }

  private valueOf(channel: Channel): string {
    return String(this.controlOf(channel).value || '').trim();
  }

  /** "Send OTP" is available once the e-mail / number is valid, and not while a send is running or cooling down. */
  canSend(channel: Channel): boolean {
    const state = this.otp[channel];
    return this.controlOf(channel).valid && !state.sending && !state.verified && state.cooldown === 0;
  }

  sendLabel(channel: Channel): string {
    const state = this.otp[channel];
    if (state.sending) {
      return 'Sending...';
    }
    if (state.cooldown > 0) {
      return `Resend in ${state.cooldown}s`;
    }
    return state.sent ? 'Resend OTP' : 'Send OTP';
  }

  otpFormatOk(channel: Channel): boolean {
    return OTP_REGEX.test(this.otp[channel].code.trim());
  }

  sendOtp(channel: Channel): void {
    if (!this.canSend(channel)) {
      return;
    }
    const state = this.otp[channel];
    const target = this.valueOf(channel);
    state.sending = true;
    state.error = '';
    state.message = '';
    const request = channel === 'email' ? this.authService.sendEmailOtp(target) : this.authService.sendMobileOtp(target);
    request.subscribe({
      next: (response) => {
        state.sending = false;
        state.sent = true;
        state.target = target;
        state.code = '';
        state.message = this.describeDelivery(channel, response, target);
        this.startCooldown(channel);
      },
      error: (err) => {
        state.sending = false;
        state.error = serverMessage(err, 'We could not send the OTP. Please try again.');
        if (err && err.status === 409) {
          this.errorMessage = '';
        }
      }
    });
  }

  verifyOtp(channel: Channel): void {
    const state = this.otp[channel];
    if (state.verifying || state.verified) {
      return;
    }
    if (!this.otpFormatOk(channel)) {
      state.error = 'Please enter the 6 digit OTP';
      return;
    }
    state.verifying = true;
    state.error = '';
    const code = state.code.trim();
    const request =
      channel === 'email'
        ? this.authService.verifyEmailOtp(state.target, code)
        : this.authService.verifyMobileOtp(state.target, code);
    request.subscribe({
      next: () => {
        state.verifying = false;
        state.verified = true;
        state.message = '';
        state.error = '';
        this.stopCooldown(channel);
      },
      error: (err) => {
        state.verifying = false;
        state.error = serverMessage(err, 'We could not verify the OTP. Please try again.');
      }
    });
  }

  /** "Change" next to a verified value: unlock the field again (the verification is dropped). */
  changeTarget(channel: Channel): void {
    this.resetOtp(channel);
  }

  private describeDelivery(channel: Channel, response: OtpSendResponse, target: string): string {
    if (response && response.delivery === 'console') {
      return 'OTP generated. This server has no ' + (channel === 'email' ? 'e-mail (SMTP)' : 'SMS') +
        ' provider set up yet (development mode), so read the OTP in the server console.';
    }
    return channel === 'email' ? `OTP sent to ${target}. Check your inbox.` : `OTP sent by SMS to ${target}.`;
  }

  private onTargetChanged(channel: Channel, value: string): void {
    const state = this.otp[channel];
    if (state.target && String(value || '').trim() !== state.target) {
      this.resetOtp(channel);
    }
  }

  private resetOtp(channel: Channel): void {
    this.stopCooldown(channel);
    this.otp[channel] = emptyOtp();
  }

  private startCooldown(channel: Channel): void {
    this.stopCooldown(channel);
    const state = this.otp[channel];
    state.cooldown = OTP_RESEND_SECONDS;
    this.timers[channel] = setInterval(() => {
      state.cooldown = Math.max(0, state.cooldown - 1);
      if (state.cooldown === 0) {
        this.stopCooldown(channel);
      }
    }, 1000);
  }

  private stopCooldown(channel: Channel): void {
    if (this.timers[channel]) {
      clearInterval(this.timers[channel]);
      this.timers[channel] = null;
    }
    this.otp[channel].cooldown = 0;
  }

  // ------------------------------------------------------------------ submit

  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';
    if (this.signupForm.invalid) {
      return;
    }
    if (!this.otp.email.verified || !this.otp.mobile.verified) {
      this.errorMessage = 'Please verify your email and mobile number with the OTP before signing up.';
      return;
    }
    const value = this.signupForm.value;
    const user: User = {
      username: value.username.trim(),
      email: value.email.trim(),
      mobileNumber: value.mobileNumber.trim(),
      password: value.password
    };
    this.authService.register(user).subscribe({
      next: () => (this.showSuccessPopup = true),
      error: (err) => {
        if (err && err.status === 409) {
          this.errorMessage = 'A user with this email already exists';
        } else if (err && err.status >= 400 && err.status < 500) {
          this.errorMessage = serverMessage(err, 'Registration failed. Please try again.');
        }
      }
    });
  }

  goToLogin(): void {
    this.showSuccessPopup = false;
    this.router.navigate(['/login']);
  }
}
