import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { CustomerviewfeedbackComponent } from './customerviewfeedback.component';

describe('CustomerviewfeedbackComponent', () => {
  let component: CustomerviewfeedbackComponent;
  let fixture: ComponentFixture<CustomerviewfeedbackComponent>;
  let http: HttpTestingController;

  const url = 'http://localhost:8080/api/feedback';
  const el = () => fixture.nativeElement as HTMLElement;
  const data = () => [
    { feedbackId: 1, feedbackText: 'Great ride', date: '2025-01-25', category: 'Punctuality', rating: 5, sentiment: 'Positive', aiTags: 'punctual, polite', driver: { driverId: 3, driverName: 'demo name three', hourlyRate: 300 } },
    { feedbackId: 2, feedbackText: 'Okay', date: '2025-01-26', category: 'Other', rating: 3 }
  ];

  const init = (body: any, status = 200) => {
    localStorage.setItem('userId', '2');
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      declarations: [CustomerviewfeedbackComponent]
    });
    fixture = TestBed.createComponent(CustomerviewfeedbackComponent);
    component = fixture.componentInstance;
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
    const req = http.expectOne(`${url}/user/2`);
    status === 200 ? req.flush(body) : req.flush('', { status, statusText: 'err' });
    fixture.detectChanges();
  };

  afterEach(() => localStorage.clear());

  it('should create and show the My Feedback heading with the feedback cards', () => {
    init(data());
    expect(component).toBeTruthy();
    expect(el().textContent).toContain('My Feedback');
    expect(el().querySelectorAll('.feedback-card').length).toBe(2);
  });

  it('shows "No Data Found" when nothing has been posted (404)', () => {
    init(null, 404);
    expect(el().textContent).toContain('No Data Found');
  });

  it('opens the driver info modal', () => {
    init(data());
    component.viewDriverInfo(component.feedbacks[0]);
    fixture.detectChanges();
    expect(el().textContent).toContain('demo name three');
    component.closeDriverModal();
    fixture.detectChanges();
    expect(el().querySelector('#driverModal')).toBeNull();
  });

  it('deletes a feedback after confirmation', () => {
    init(data());
    component.askDelete(component.feedbacks[0]);
    fixture.detectChanges();
    expect(el().textContent).toContain('Yes, Delete');
    component.confirmDelete();
    http.expectOne(`${url}/1`).flush({});
    fixture.detectChanges();
    expect(component.feedbacks.length).toBe(1);
  });
});
