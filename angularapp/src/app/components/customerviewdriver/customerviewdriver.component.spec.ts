import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';

import { CustomerviewdriverComponent } from './customerviewdriver.component';

describe('CustomerviewdriverComponent', () => {
  let component: CustomerviewdriverComponent;
  let fixture: ComponentFixture<CustomerviewdriverComponent>;
  let http: HttpTestingController;

  const base = 'http://localhost:8080/api';
  const text = () => (fixture.nativeElement as HTMLElement).textContent || '';
  const drivers = [
    { driverId: 1, driverName: 'demo name one', licenseNumber: '123', experienceYears: 3, contactNumber: '91', availabilityStatus: 'Inactive', address: 'a', vehicleType: 'SUV', hourlyRate: 450, image: '' },
    { driverId: 2, driverName: 'demo name two', licenseNumber: '124', experienceYears: 2, contactNumber: '92', availabilityStatus: 'Active', address: 'a', vehicleType: 'Sedan', hourlyRate: 230, image: '' },
    { driverId: 3, driverName: 'demo name three', licenseNumber: '125', experienceYears: 7, contactNumber: '93', availabilityStatus: 'Active', address: 'a', vehicleType: 'Truck', hourlyRate: 330, image: '' }
  ];

  const init = (requests: any[] | null, status = 200) => {
    localStorage.setItem('userId', '2');
    fixture = TestBed.createComponent(CustomerviewdriverComponent);
    component = fixture.componentInstance;
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
    http.expectOne(`${base}/driver`).flush(drivers);
    const req = http.expectOne(`${base}/driverRequest/user/2`);
    if (status === 404) {
      req.flush('', { status: 404, statusText: 'Not Found' });
    } else {
      req.flush(requests);
    }
    fixture.detectChanges();
  };

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [FormsModule, RouterTestingModule, HttpClientTestingModule],
      declarations: [CustomerviewdriverComponent]
    });
  });

  afterEach(() => localStorage.clear());

  it('should create and list drivers', () => {
    init([]);
    expect(component).toBeTruthy();
    expect(text()).toContain('Available Drivers');
    expect(fixture.nativeElement.querySelectorAll('.driver-card').length).toBe(3);
  });

  it('shows "Oops! No records found" when there are no drivers', () => {
    localStorage.setItem('userId', '2');
    fixture = TestBed.createComponent(CustomerviewdriverComponent);
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
    http.expectOne(`${base}/driver`).flush(null, { status: 204, statusText: 'No Content' });
    http.expectOne(`${base}/driverRequest/user/2`).flush('', { status: 404, statusText: 'Not Found' });
    fixture.detectChanges();
    expect(text()).toContain('Oops! No records found');
  });

  it('only enables Request for active drivers that are not already requested', () => {
    init([{ driverRequestId: 9, status: 'Pending', driver: { driverId: 3 } }]);
    const el = fixture.nativeElement as HTMLElement;
    expect((el.querySelector('#request-1') as HTMLButtonElement).disabled).toBeTrue();
    expect((el.querySelector('#request-2') as HTMLButtonElement).disabled).toBeFalse();
    expect(el.querySelector('#request-3')).toBeNull();
    expect(el.querySelector('#requested-3')?.textContent).toContain('Requested');
  });

  it('handles a 404 (no requests yet) and navigates to the request form', () => {
    init(null, 404);
    const navigate = spyOn(TestBed.inject(Router), 'navigate').and.returnValue(Promise.resolve(true));
    component.requestDriver(component.drivers[1]);
    expect(navigate).toHaveBeenCalledWith(['/customer-request', 2]);
  });

  it('filters by the search text', () => {
    init([]);
    component.searchText = 'truck';
    expect(component.displayedDrivers.length).toBe(1);
  });

  it('runs the AI search and can clear it', () => {
    init([]);
    component.aiQuery = 'big truck for moving';
    component.runAiSearch();
    const req = http.expectOne(`${base}/ai/driver-search`);
    expect(req.request.body).toBe('big truck for moving');
    req.flush([drivers[2]]);
    fixture.detectChanges();
    expect(component.displayedDrivers.length).toBe(1);
    expect(text()).toContain('Showing AI best matches for');
    component.clearAiSearch();
    expect(component.displayedDrivers.length).toBe(3);
  });
});
