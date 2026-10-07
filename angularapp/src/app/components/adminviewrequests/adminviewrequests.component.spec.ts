import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { AdminviewrequestsComponent } from './adminviewrequests.component';

describe('AdminviewrequestsComponent', () => {
  let component: AdminviewrequestsComponent;
  let fixture: ComponentFixture<AdminviewrequestsComponent>;
  let http: HttpTestingController;

  const url = 'http://localhost:8080/api/driverRequest';
  const text = () => (fixture.nativeElement as HTMLElement).textContent || '';
  const driver = { driverId: 3, driverName: 'demo name three', licenseNumber: '22', experienceYears: 7, contactNumber: '91', vehicleType: 'Truck', hourlyRate: 330, address: 'demo address' };
  const requests = () => [
    { driverRequestId: 1, user: { username: 'demo' }, driver, status: 'Pending', pickupLocation: 'demo pick 3', dropLocation: 'demo drop 3', tripDate: '2025-01-26' },
    { driverRequestId: 2, user: { username: 'demo' }, driver, status: 'Trip End', pickupLocation: 'demo pick 2', dropLocation: 'demo drop 2', tripDate: [2025, 1, 25] }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, HttpClientTestingModule],
      declarations: [AdminviewrequestsComponent]
    });
    fixture = TestBed.createComponent(AdminviewrequestsComponent);
    component = fixture.componentInstance;
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  it('should create', () => {
    http.expectOne(url).flush(null);
    expect(component).toBeTruthy();
  });

  it('shows "Oops! No records found" when there is no data', () => {
    http.expectOne(url).flush(null, { status: 204, statusText: 'No Content' });
    fixture.detectChanges();
    expect(text()).toContain('Driver Requests for Approval');
    expect(text()).toContain('Oops! No records found');
  });

  it('lists the requests with formatted trip dates', () => {
    http.expectOne(url).flush(requests());
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('tbody tr').length).toBe(2);
    expect(text()).toContain('2025-01-25');
  });

  it('offers Approve/Reject depending on the status', () => {
    http.expectOne(url).flush(requests());
    const [pending, tripEnd] = component.requests;
    expect(component.canApprove(pending) && component.canReject(pending)).toBeTrue();
    expect(component.canApprove(tripEnd) || component.canReject(tripEnd)).toBeFalse();
  });

  it('approves a request', () => {
    http.expectOne(url).flush(requests());
    component.approve(component.requests[0]);
    const req = http.expectOne(`${url}/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ status: 'Approved' });
    req.flush({});
    expect(component.requests[0].status).toBe('Approved');
  });

  it('filters by driver name / pickup location and by status', () => {
    http.expectOne(url).flush(requests());
    component.searchText = 'pick 2';
    component.applyFilters();
    expect(component.filteredRequests.length).toBe(1);
    component.searchText = '';
    component.statusFilter = 'Pending';
    component.applyFilters();
    expect(component.filteredRequests[0].driverRequestId).toBe(1);
  });

  it('shows the progression and closes a request at "Trip End"', () => {
    http.expectOne(url).flush(requests());
    const tripEnd = component.requests[1];
    component.openStages(tripEnd);
    fixture.detectChanges();
    expect(text()).toContain('Request Progression');
    expect(component.stageClass('Trip End')).toBe('done');
    expect(component.stageClass('Closed')).toBe('idle');
    expect(fixture.nativeElement.querySelector('#closeRequestButton')).toBeTruthy();
    component.closeRequest();
    http.expectOne(`${url}/2`).flush({});
    expect(tripEnd.status).toBe('Closed');
    expect(component.showStageModal).toBeFalse();
  });

  it('shows Pending and Rejected stages for a rejected request', () => {
    http.expectOne(url).flush(requests());
    component.requests[0].status = 'Rejected';
    component.openStages(component.requests[0]);
    expect(component.visibleStages).toEqual(['Pending', 'Rejected']);
    expect(component.stageClass('Rejected')).toBe('rejected');
  });
});
