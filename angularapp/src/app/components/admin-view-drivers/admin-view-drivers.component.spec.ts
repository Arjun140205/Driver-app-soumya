import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { AdminViewDriversComponent } from './admin-view-drivers.component';

describe('AdminViewDriversComponent', () => {
  let component: AdminViewDriversComponent;
  let fixture: ComponentFixture<AdminViewDriversComponent>;
  let http: HttpTestingController;

  const url = 'http://localhost:8080/api/driver';
  const text = () => (fixture.nativeElement as HTMLElement).textContent || '';
  const drivers = [
    { driverId: 1, driverName: 'demo name one', licenseNumber: '1234567890987654', experienceYears: 3, contactNumber: '91234567899', availabilityStatus: 'Inactive', address: 'demo address', vehicleType: 'SUV', hourlyRate: 450, image: '' },
    { driverId: 2, driverName: 'demo name two', licenseNumber: '1234567890987655', experienceYears: 2, contactNumber: '91234567893', availabilityStatus: 'Active', address: 'demo address', vehicleType: 'Sedan', hourlyRate: 230, image: '' }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, RouterTestingModule, HttpClientTestingModule],
      declarations: [AdminViewDriversComponent]
    });
    fixture = TestBed.createComponent(AdminViewDriversComponent);
    component = fixture.componentInstance;
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  it('should create', () => {
    http.expectOne(url).flush(null);
    expect(component).toBeTruthy();
  });

  it('shows "Oops! No data found." when there are no drivers', () => {
    http.expectOne(url).flush(null, { status: 204, statusText: 'No Content' });
    fixture.detectChanges();
    expect(text()).toContain('Drivers');
    expect(text()).toContain('Oops! No data found.');
  });

  it('renders a card per driver and locks Edit/Delete for inactive drivers', () => {
    http.expectOne(url).flush(drivers);
    fixture.detectChanges();
    const el = fixture.nativeElement as HTMLElement;
    expect(el.querySelectorAll('.driver-card').length).toBe(2);
    expect((el.querySelector('#edit-1') as HTMLButtonElement).disabled).toBeTrue();
    expect((el.querySelector('#edit-2') as HTMLButtonElement).disabled).toBeFalse();
  });

  it('filters by name and by status', () => {
    http.expectOne(url).flush(drivers);
    component.searchText = 'two';
    component.applyFilters();
    expect(component.filteredDrivers.length).toBe(1);
    component.searchText = '';
    component.statusFilter = 'Inactive';
    component.applyFilters();
    expect(component.filteredDrivers[0].driverName).toBe('demo name one');
  });

  it('lists the other statuses in the Action menu and updates the driver', () => {
    http.expectOne(url).flush(drivers);
    expect(component.otherStatuses(drivers[1] as any)).toEqual(['Inactive', 'On Leave']);
    component.changeStatus(component.drivers[1], 'On Leave');
    const req = http.expectOne(`${url}/2`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body.availabilityStatus).toBe('On Leave');
    req.flush({});
    expect(component.drivers[1].availabilityStatus).toBe('On Leave');
  });

  it('asks for confirmation and deletes a driver', () => {
    http.expectOne(url).flush(drivers);
    component.openDeletePopup(component.drivers[1]);
    fixture.detectChanges();
    expect(text()).toContain('Are you sure you want to delete this driver?');
    component.confirmDelete();
    const req = http.expectOne(`${url}/2`);
    expect(req.request.method).toBe('DELETE');
    req.flush({});
    http.expectOne(url).flush([drivers[0]]);
    expect(component.drivers.length).toBe(1);
  });
});
