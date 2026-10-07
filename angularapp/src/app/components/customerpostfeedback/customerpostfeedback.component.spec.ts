import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { CustomerpostfeedbackComponent } from './customerpostfeedback.component';

describe('CustomerpostfeedbackComponent', () => {
  let component: CustomerpostfeedbackComponent;
  let fixture: ComponentFixture<CustomerpostfeedbackComponent>;
  let http: HttpTestingController;

  const text = () => (fixture.nativeElement as HTMLElement).textContent || '';

  beforeEach(() => {
    localStorage.setItem('userId', '2');
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule, HttpClientTestingModule],
      declarations: [CustomerpostfeedbackComponent],
      providers: [{ provide: ActivatedRoute, useValue: { snapshot: { queryParamMap: convertToParamMap({ driverId: '3' }) } } }]
    });
    fixture = TestBed.createComponent(CustomerpostfeedbackComponent);
    component = fixture.componentInstance;
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => localStorage.clear());

  it('should create and show the Submit Feedback heading', () => {
    expect(component).toBeTruthy();
    expect(text()).toContain('Submit Feedback');
  });

  it('shows validation messages on an empty submit', () => {
    component.onSubmit();
    fixture.detectChanges();
    expect(text()).toContain('*Category is required');
    expect(text()).toContain('*Rating is required');
    expect(text()).toContain('*Feedback is required');
    expect(text()).toContain('All fields are required');
  });

  it('posts the feedback for the driver and shows the thank-you popup', () => {
    component.setRating(4);
    component.feedbackForm.patchValue({ category: 'Punctuality', feedbackText: 'On time and polite' });
    component.onSubmit();
    const req = http.expectOne('http://localhost:8080/api/feedback');
    expect(req.request.method).toBe('POST');
    expect(req.request.body.user).toEqual({ userId: 2 });
    expect(req.request.body.driver).toEqual({ driverId: 3 });
    expect(req.request.body.rating).toBe(4);
    req.flush({}, { status: 201, statusText: 'Created' });
    fixture.detectChanges();
    expect(text()).toContain('Thank you for your feedback!');
  });
});
