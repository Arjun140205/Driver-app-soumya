import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { AdminviewfeedbackComponent } from './adminviewfeedback.component';

describe('AdminviewfeedbackComponent', () => {
  let component: AdminviewfeedbackComponent;
  let fixture: ComponentFixture<AdminviewfeedbackComponent>;
  let http: HttpTestingController;

  const url = 'http://localhost:8080/api/feedback';
  const text = () => (fixture.nativeElement as HTMLElement).textContent || '';
  const feedbacks = () => [
    { feedbackId: 1, feedbackText: 'excellent in punctuality', date: '2025-01-25', category: 'Punctuality', rating: 5, sentiment: 'Positive', aiTags: 'punctuality,politeness',
      user: { email: 'cus@gmail.com', username: 'demo', mobileNumber: '9123456789' },
      driver: { driverId: 2, driverName: 'demo name two', licenseNumber: '1234567890987655', experienceYears: 2, vehicleType: 'Sedan', hourlyRate: 230 } },
    { feedbackId: 2, feedbackText: 'late again', date: '2025-01-26', category: 'Driver Performance', rating: 1, sentiment: 'Negative', aiTags: '', user: { username: 'demo' }, driver: null }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, HttpClientTestingModule],
      declarations: [AdminviewfeedbackComponent]
    });
    fixture = TestBed.createComponent(AdminviewfeedbackComponent);
    component = fixture.componentInstance;
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  it('should create', () => {
    http.expectOne(url).flush(null);
    expect(component).toBeTruthy();
  });

  it('shows "No Data Found" when there is no feedback', () => {
    http.expectOne(url).flush(null, { status: 204, statusText: 'No Content' });
    fixture.detectChanges();
    expect(text()).toContain('Feedback Received');
    expect(text()).toContain('No Data Found');
  });

  it('lists feedback with dd/MM/yyyy dates, sentiment badge and tags', () => {
    http.expectOne(url).flush(feedbacks());
    fixture.detectChanges();
    expect(text()).toContain('25/01/2025');
    expect(text()).toContain('Positive');
    expect(text()).toContain('punctuality');
  });

  it('filters by category and sentiment', () => {
    http.expectOne(url).flush(feedbacks());
    component.categoryFilter = 'Punctuality';
    component.applyFilters();
    expect(component.filteredFeedbacks.length).toBe(1);
    component.categoryFilter = 'All';
    component.sentimentFilter = 'Negative';
    component.applyFilters();
    expect(component.filteredFeedbacks[0].feedbackId).toBe(2);
  });

  it('shows the user profile modal', () => {
    http.expectOne(url).flush(feedbacks());
    component.showProfile(component.feedbacks[0]);
    fixture.detectChanges();
    expect(text()).toContain('User Details:');
    expect(text()).toContain('cus@gmail.com');
  });

  it('shows driver details with the AI review summary', () => {
    http.expectOne(url).flush(feedbacks());
    component.viewDriverInfo(component.feedbacks[0]);
    http.expectOne('http://localhost:8080/api/ai/driver/2/feedback-summary').flush({
      summary: 'Consistently punctual.', strengths: ['Good punctuality'], improvements: [], aiScore: 80, averageRating: 4, feedbackCount: 1
    });
    fixture.detectChanges();
    expect(text()).toContain('Driver Details');
    expect(text()).toContain('demo name two');
    expect(text()).toContain('AI Review Summary');
    expect(text()).toContain('Good punctuality');
  });
});
