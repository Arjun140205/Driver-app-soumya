import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';

import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let http: HttpTestingController;
  let router: Router;

  const text = () => (fixture.nativeElement as HTMLElement).textContent || '';

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule, HttpClientTestingModule],
      declarations: [LoginComponent]
    });
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    http = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  afterEach(() => localStorage.clear());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('shows the DriveU heading and a sign up link', () => {
    expect(text()).toContain('DriveU');
    expect(text()).toContain("Don't have an account?");
  });

  it('shows required messages when submitted empty', () => {
    component.onSubmit();
    fixture.detectChanges();
    expect(text()).toContain('Email address is required');
    expect(text()).toContain('Password is required');
  });

  it('validates the email format and password length', () => {
    component.loginForm.setValue({ email: 'qq', password: 'a' });
    component.onSubmit();
    fixture.detectChanges();
    expect(text()).toContain('Please enter a valid email address');
    expect(text()).toContain('Password must be at least 6 characters long');
  });

  it('shows an error for invalid credentials', () => {
    component.loginForm.setValue({ email: 'admin@gmail.com', password: 'wrongpass' });
    component.onSubmit();
    http.expectOne('http://localhost:8080/api/login').flush('Invalid credentials', { status: 401, statusText: 'Unauthorized' });
    fixture.detectChanges();
    expect(text()).toContain('Invalid email address or password');
  });

  it('stores the token and goes to the home page on success', () => {
    const navigate = spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    component.loginForm.setValue({ email: 'admin@gmail.com', password: 'secret123' });
    component.onSubmit();
    http.expectOne('http://localhost:8080/api/login').flush({ token: 'jwt', username: 'admin', userRole: 'Admin', userId: 1 });
    expect(localStorage.getItem('token')).toBe('jwt');
    expect(navigate).toHaveBeenCalledWith(['/home']);
  });
});
