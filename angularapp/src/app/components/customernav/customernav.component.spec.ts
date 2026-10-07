import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { CustomernavComponent } from './customernav.component';
import { AuthService } from '../../services/auth.service';

describe('CustomernavComponent', () => {
  let component: CustomernavComponent;
  let fixture: ComponentFixture<CustomernavComponent>;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      declarations: [CustomernavComponent]
    });
    fixture = TestBed.createComponent(CustomernavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => localStorage.clear());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('shows the username and role and the customer menu', () => {
    const auth = TestBed.inject(AuthService);
    (auth as any).usernameSubject.next('demo');
    (auth as any).userRoleSubject.next('Customer');
    fixture.detectChanges();
    const el = fixture.nativeElement as HTMLElement;
    expect(el.querySelector('#userInfo')?.textContent).toContain('demo / Customer');
    const text = el.textContent || '';
    ['Home', 'Available Drivers', 'My Requests', 'Feedback', 'Logout']
      .forEach((label) => expect(text).toContain(label));
  });

  it('asks for confirmation before logging out', () => {
    component.openLogoutPopup();
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Are you sure you want to logout?');
    component.cancelLogout();
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).not.toContain('Are you sure you want to logout?');
  });
});
