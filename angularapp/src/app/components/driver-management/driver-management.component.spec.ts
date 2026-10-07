import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { DriverManagementComponent } from './driver-management.component';

describe('DriverManagementComponent', () => {
  let component: DriverManagementComponent;
  let fixture: ComponentFixture<DriverManagementComponent>;
  let http: HttpTestingController;

  const text = () => (fixture.nativeElement as HTMLElement).textContent || '';

  const setup = (driverId?: string) => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule, HttpClientTestingModule],
      declarations: [DriverManagementComponent],
      providers: [{ provide: ActivatedRoute, useValue: { snapshot: { paramMap: convertToParamMap(driverId ? { driverId } : {}) } } }]
    });
    fixture = TestBed.createComponent(DriverManagementComponent);
    component = fixture.componentInstance;
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  };

  it('should create and show the Add New Driver form', () => {
    setup();
    expect(component).toBeTruthy();
    expect(text()).toContain('Add New Driver');
    expect(text()).toContain('Add Driver');
  });

  it('shows "All fields are required" and field messages on an empty submit', () => {
    setup();
    component.onSubmit();
    fixture.detectChanges();
    expect(text()).toContain('All fields are required');
    ['*Name is required', '*License Number is required', '*Experience is required', '*Contact Number is required',
      '*Vehicle type is required', '*Hourly Rate is required', '*Address is required'].forEach((m) => expect(text()).toContain(m));
  });

  it('adds a driver with the status "Active" and shows the success popup', () => {
    setup();
    component.driverForm.setValue({
      driverName: 'demo name', licenseNumber: '1234567890987654', experienceYears: 3, contactNumber: '9123456789',
      vehicleType: 'SUV', hourlyRate: 450, address: 'demo address'
    });
    component.imageBase64 = 'data:image/png;base64,AAAA';
    component.onSubmit();
    const req = http.expectOne('http://localhost:8080/api/driver');
    expect(req.request.method).toBe('POST');
    expect(req.request.body.availabilityStatus).toBe('Active');
    req.flush({ driverId: 1 }, { status: 201, statusText: 'Created' });
    fixture.detectChanges();
    expect(text()).toContain('Driver Added Successfully!');
    component.closeSuccessPopup();
    expect(component.driverForm.value.driverName).toBe('');
  });

  it('loads the driver into the form in edit mode', () => {
    setup('7');
    http.expectOne('http://localhost:8080/api/driver/7').flush({
      driverId: 7, driverName: 'demo name three', licenseNumber: '2234567890987651', experienceYears: 7,
      contactNumber: '9123456789', availabilityStatus: 'On Leave', address: 'demo address', vehicleType: 'Truck', hourlyRate: 330, image: ''
    });
    fixture.detectChanges();
    expect(text()).toContain('Edit Driver');
    expect(text()).toContain('Update Driver');
    expect(component.driverForm.value.driverName).toBe('demo name three');
    component.onSubmit();
    const req = http.expectOne('http://localhost:8080/api/driver/7');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body.availabilityStatus).toBe('On Leave');
    req.flush({});
  });
});
