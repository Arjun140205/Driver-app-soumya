import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { AdminnavComponent } from './adminnav.component';
import { AuthService } from '../../services/auth.service';

describe('AdminnavComponent', () => {
  let component: AdminnavComponent;
  let fixture: ComponentFixture<AdminnavComponent>;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      declarations: [AdminnavComponent]
    });
    fixture = TestBed.createComponent(AdminnavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => localStorage.clear());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('shows the username and role and the admin menu', () => {
    const auth = TestBed.inject(AuthService);
    (auth as any).usernameSubject.next('adminname');
    (auth as any).userRoleSubject.next('Admin');
    fixture.detectChanges();
    const el = fixture.nativeElement as HTMLElement;
    expect(el.querySelector('#userInfo')?.textContent).toContain('adminname / Admin');
    const text = el.textContent || '';
    ['Home', 'Drivers', 'Add Driver', 'View Drivers', 'Requests', 'View Requests', 'Feedback', 'View Feedback', 'Logout']
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
