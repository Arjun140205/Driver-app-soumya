import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { SignupComponent } from './signup.component';

describe('SignupComponent', () => {
  let component: SignupComponent;
  let fixture: ComponentFixture<SignupComponent>;
  let http: HttpTestingController;

  const text = () => (fixture.nativeElement as HTMLElement).textContent || '';
  const validValues = {
    username: 'customer2name',
    email: 'cus2@gmail.com',
    mobileNumber: '7888499919',
    password: 'secret12',
    confirmPassword: 'secret12',
    userRole: 'Customer'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule, HttpClientTestingModule],
      declarations: [SignupComponent]
    });
    fixture = TestBed.createComponent(SignupComponent);
    component = fixture.componentInstance;
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('shows required messages on an empty submit', () => {
    component.onSubmit();
    fixture.detectChanges();
    ['Username is required', 'Email is required', 'Mobile number is required', 'Password is required',
      'Confirm password is required', 'User role is required'].forEach((m) => expect(text()).toContain(m));
  });

  it('validates email, mobile, password length and matching passwords', () => {
    component.signupForm.setValue({ username: 'a', email: 'aa', mobileNumber: '9', password: 'a', confirmPassword: 'b', userRole: '' });
    component.onSubmit();
    fixture.detectChanges();
    ['Please enter a valid email address', 'Mobile number must be 10 digits',
      'Password must be at least 6 characters long', 'Passwords do not match'].forEach((m) => expect(text()).toContain(m));
  });

  it('shows the duplicate email message on a 409', () => {
    component.signupForm.setValue(validValues);
    component.onSubmit();
    http.expectOne('http://localhost:8080/api/register').flush('A user with this email already exists', { status: 409, statusText: 'Conflict' });
    fixture.detectChanges();
    expect(text()).toContain('A user with this email already exists');
  });

  it('shows the success popup after registering', () => {
    component.signupForm.setValue(validValues);
    component.onSubmit();
    const req = http.expectOne('http://localhost:8080/api/register');
    expect(req.request.body.userRole).toBe('Customer');
    expect(req.request.body.confirmPassword).toBeUndefined();
    req.flush({ userId: 5 }, { status: 201, statusText: 'Created' });
    fixture.detectChanges();
    expect(text()).toContain('Registration Successful!');
  });
});
