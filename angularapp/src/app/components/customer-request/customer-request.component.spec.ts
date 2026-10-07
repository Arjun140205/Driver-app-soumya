import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { CustomerRequestComponent } from './customer-request.component';

describe('CustomerRequestComponent', () => {
  let component: CustomerRequestComponent;
  let fixture: ComponentFixture<CustomerRequestComponent>;
  let http: HttpTestingController;

  const url = 'http://localhost:8080/api/driverRequest';
  const text = () => (fixture.nativeElement as HTMLElement).textContent || '';
  const future = () => {
    const d = new Date();
    d.setDate(d.getDate() + 5);
    return d.toISOString().substring(0, 10);
  };

  const setup = (params: { [key: string]: string }) => {
    localStorage.setItem('userId', '2');
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule, HttpClientTestingModule],
      declarations: [CustomerRequestComponent],
      providers: [{ provide: ActivatedRoute, useValue: { snapshot: { paramMap: convertToParamMap(params) } } }]
    });
    fixture = TestBed.createComponent(CustomerRequestComponent);
    component = fixture.componentInstance;
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  };

  afterEach(() => localStorage.clear());

  it('should create and show the New Driver Request form', () => {
    setup({ driverId: '3' });
    expect(component).toBeTruthy();
    expect(text()).toContain('New Driver Request');
    expect(text()).toContain('Submit Request');
  });

  it('shows validation messages on an empty submit', () => {
    setup({ driverId: '3' });
    component.onSubmit();
    fixture.detectChanges();
    ['*Pickup Location is required', '*Drop Location is required', '*Trip Date is required', '*Time Slot is required',
      '*Estimated Duration is required', 'All required fields must be filled out'].forEach((m) => expect(text()).toContain(m));
  });

  it('rejects a trip date in the past', () => {
    setup({ driverId: '3' });
    component.requestForm.patchValue({ tripDate: '2000-01-01' });
    expect(component.f['tripDate'].errors?.['pastDate']).toBeTrue();
  });

  it('submits a nested request for the selected driver', () => {
    setup({ driverId: '3' });
    component.requestForm.setValue({ pickupLocation: 'demo pick 2', dropLocation: 'demo drop 2', tripDate: future(), timeSlot: '22:56', estimatedDuration: '3 hours', comments: 'demo comments' });
    component.onSubmit();
    const req = http.expectOne(url);
    expect(req.request.method).toBe('POST');
    expect(req.request.body.user).toEqual({ userId: 2 });
    expect(req.request.body.driver).toEqual({ driverId: 3 });
    expect(req.request.body.status).toBe('Pending');
    req.flush({}, { status: 201, statusText: 'Created' });
    fixture.detectChanges();
    expect(text()).toContain('Request Submitted Successfully!');
  });

  it('loads and updates an existing request in edit mode', () => {
    setup({ driverRequestId: '9' });
    http.expectOne(`${url}/9`).flush({
      driverRequestId: 9, status: 'Pending', pickupLocation: 'demo pick 4', dropLocation: 'demo drop 4', tripDate: '2025-01-25',
      timeSlot: '14:35:00', estimatedDuration: '2 hours', comments: 'demo comments', driver: { driverId: 3 }
    });
    fixture.detectChanges();
    expect(text()).toContain('Edit Request');
    expect(text()).toContain('Update Request');
    expect(component.requestForm.value.timeSlot).toBe('14:35');
    component.requestForm.patchValue({ comments: 'demo comments updated' });
    component.onSubmit();
    const req = http.expectOne(`${url}/9`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body.comments).toBe('demo comments updated');
    req.flush({});
    fixture.detectChanges();
    expect(text()).toContain('Request Updated Successfully!');
  });
});
