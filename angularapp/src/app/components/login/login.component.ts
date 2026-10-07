import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Login } from '../../models/login.model';
import { EMAIL_REGEX } from '../../utils/constants';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  submitted: boolean = false;
  showPassword: boolean = false;
  errorMessage: string = '';

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.pattern(EMAIL_REGEX)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  get email() {
    return this.loginForm.get('email')!;
  }

  get password() {
    return this.loginForm.get('password')!;
  }

  isInvalid(field: 'email' | 'password'): boolean {
    const control = this.loginForm.get(field)!;
    return control.invalid && (control.touched || this.submitted);
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';
    if (this.loginForm.invalid) {
      return;
    }
    const credentials: Login = { email: this.email.value.trim(), password: this.password.value };
    this.authService.login(credentials).subscribe({
      next: () => this.router.navigate(['/home']),
      error: (err) => {
        // 401 = wrong credentials. Server failures are routed to the error page by the interceptor.
        if (err && (err.status === 400 || err.status === 401 || err.status === 403)) {
          this.errorMessage = 'Invalid email address or password';
        }
      }
    });
  }
}
