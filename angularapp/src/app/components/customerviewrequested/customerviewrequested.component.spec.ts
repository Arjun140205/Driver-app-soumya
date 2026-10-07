import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';

import { CustomerviewrequestedComponent } from './customerviewrequested.component';

describe('CustomerviewrequestedComponent', () => {
  let component: CustomerviewrequestedComponent;
  let fixture: ComponentFixture<CustomerviewrequestedComponent>;
  let http: HttpTestingController;

  const url = 'http://localhost:8080/api/driverRequest';
  const el = () => fixture.nativeElement as HTMLElement;
  const driver = { driverId: 3, driverName: 'demo name three', vehicleType: 'Truck', hourlyRate: 300, contactNumber: '93' };
  const requests = () => [
    { driverRequestId: 1, status: 'Pending', pickupLocation: 'a', dropLocation: 'b', tripDate: '2025-01-25', timeSlot: '10:00:00', estimatedDuration: '2 hours', driver },
    { driverRequestId: 2, status: 'Approved', pickupLocation: 'c', dropLocation: 'd', tripDate: '2025-01-26', timeSlot: '10:00:00', estimatedDuration: '2 hours', driver },
    { driverRequestId: 3, status: 'Trip End', pickupLocation: 'e', dropLocation: 'f', tripDate: '2025-01-27', timeSlot: '10:00:00', estimatedDuration: '2 hours', paymentAmount: 600, actualDuration: '2 hours 0 minutes', driver }
  ];

  const init = (body: any, status = 200) => {
    localStorage.setItem('userId', '2');
    TestBed.configureTestingModule({
      imports: [FormsModule, RouterTestingModule, HttpClientTestingModule],
      declarations: [CustomerviewrequestedComponent]
    });
    fixture = TestBed.createComponent(CustomerviewrequestedComponent);
    component = fixture.componentInstance;
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
    const req = http.expectOne(`${url}/user/2`);
    status === 200 ? req.flush(body) : req.flush('', { status, statusText: 'err' });
    fixture.detectChanges();
  };

  afterEach(() => localStorage.clear());

  it('should create and list the requests as cards', () => {
    init(requests());
    expect(component).toBeTruthy();
    expect(el().querySelectorAll('.request-card').length).toBe(3);
  });

  it('shows "No Data Found" when the customer has no requests (404)', () => {
    init(null, 404);
    expect(el().textContent).toContain('No Data Found');
  });

  it('enables Edit/Delete only for Pending and Trip End only for Approved', () => {
    init(requests());
    const disabled = (id: string) => (el().querySelector(id) as HTMLButtonElement).disabled;
    expect(disabled('#edit-1')).toBeFalse();
    expect(disabled('#delete-1')).toBeFalse();
    expect(disabled('#edit-2')).toBeTrue();
    expect(disabled('#tripEnd-1')).toBeTrue();
    expect(disabled('#tripEnd-2')).toBeFalse();
    expect(disabled('#fetchPay-3')).toBeFalse();
    expect(disabled('#fetchPay-2')).toBeTrue();
  });

  it('deletes a pending request after confirmation', () => {
    init(requests());
    component.askDelete(component.requests[0]);
    fixture.detectChanges();
    expect(el().textContent).toContain('Yes, Delete');
    component.confirmDelete();
    http.expectOne(`${url}/1`).flush({});
    expect(component.requests.length).toBe(2);
  });

  it('ends a trip and sends the actual drop details and payment', () => {
    init(requests());
    component.endTrip(component.requests[1]);
    const req = http.expectOne(`${url}/2`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body.status).toBe('Trip End');
    expect(req.request.body.paymentAmount).toBeGreaterThan(0);
    expect(req.request.body.actualDuration).toContain('hours');
    req.flush({});
    expect(component.requests[1].status).toBe('Trip End');
  });

  it('shows the payment modal after a short loading phase and navigates to the review page', fakeAsync(() => {
    init(requests());
    const navigate = spyOn(TestBed.inject(Router), 'navigate').and.returnValue(Promise.resolve(true));
    component.fetchPayAmount(component.requests[2]);
    fixture.detectChanges();
    expect(el().querySelector('.spinner')).not.toBeNull();
    tick(1600);
    fixture.detectChanges();
    expect(el().textContent).toContain('Travel Charge');
    component.writeReview();
    expect(navigate).toHaveBeenCalledWith(['/customerpostfeedback'], { queryParams: { driverId: 3 } });
  }));

  it('filters by driver name', () => {
    init(requests());
    component.searchText = 'nobody';
    expect(component.filteredRequests.length).toBe(0);
  });
});
