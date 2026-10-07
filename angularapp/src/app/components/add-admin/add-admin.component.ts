import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { EMAIL_REGEX, MOBILE_REGEX, PASSWORD_REGEX, PASSWORD_RULES } from '../../utils/constants';
import { passwordsMatch } from '../../utils/validators';
import { serverMessage } from '../../utils/http-errors';

/**
 * Admin page "Add Admin": a logged-in admin creates another admin account.
 * (Sign-up can only create customers.) The admin is created by the server with the Admin role;
 * no OTP is needed because the person adding the admin is already a trusted, logged-in admin.
 */
@Component({
  selector: 'app-add-admin',
  templateUrl: './add-admin.component.html',
  styleUrls: ['./add-admin.component.css']
})
export class AddAdminComponent {
  adminForm: FormGroup;
  submitted: boolean = false;
  serverError: string = '';
  showSuccessPopup: boolean = false;
  passwordRules = PASSWORD_RULES;

  constructor(private fb: FormBuilder, private authService: AuthService) {
    this.adminForm = this.fb.group(
      {
        username: ['', [Validators.required, Validators.pattern(/\S/)]],
        email: ['', [Validators.required, Validators.pattern(EMAIL_REGEX)]],
        mobileNumber: ['', [Validators.required, Validators.pattern(MOBILE_REGEX)]],
        password: ['', [Validators.required, Validators.pattern(PASSWORD_REGEX)]],
        confirmPassword: ['', [Validators.required]]
      },
      { validators: passwordsMatch }
    );
  }

  get f() {
    return this.adminForm.controls;
  }

  showError(field: string): boolean {
    const control = this.adminForm.get(field)!;
    return control.invalid && (control.touched || this.submitted);
  }

  get passwordMismatch(): boolean {
    return !!this.adminForm.errors?.['mismatch'] && !this.f['confirmPassword'].errors?.['required'];
  }

  get passwordTyped(): boolean {
    return !!this.f['password'].value || this.f['password'].touched || this.submitted;
  }

  ruleMet(rule: { test: (value: string) => boolean }): boolean {
    return rule.test(this.f['password'].value || '');
  }

  onSubmit(): void {
    this.submitted = true;
    this.serverError = '';
    if (this.adminForm.invalid) {
      return;
    }
    const value = this.adminForm.value;
    const admin: User = {
      username: String(value.username).trim(),
      email: String(value.email).trim(),
      mobileNumber: String(value.mobileNumber).trim(),
      password: value.password
    };
    this.authService.registerAdmin(admin).subscribe({
      next: () => (this.showSuccessPopup = true),
      error: (err) => {
        if (err && err.status === 409) {
          this.serverError = 'A user with this email already exists';
        } else if (err && err.status >= 400 && err.status < 500) {
          this.serverError = serverMessage(err, 'Unable to add the admin. Please check the details and try again.');
        }
      }
    });
  }

  closeSuccessPopup(): void {
    this.showSuccessPopup = false;
    this.adminForm.reset({ username: '', email: '', mobileNumber: '', password: '', confirmPassword: '' });
    this.submitted = false;
    this.serverError = '';
  }
}
