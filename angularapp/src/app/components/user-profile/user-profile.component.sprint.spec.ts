import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { UserProfileComponent } from './user-profile.component';

describe('UserProfileComponent sprint behavior', () => {
  let fixture: ComponentFixture<UserProfileComponent>;
  let auth: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    auth = jasmine.createSpyObj<AuthService>('AuthService', ['getMyProfile']);
    await TestBed.configureTestingModule({ declarations: [UserProfileComponent], providers: [{ provide: AuthService, useValue: auth }] }).compileComponents();
  });

  it('loads the authenticated profile and clears loading', () => {
    auth.getMyProfile.and.returnValue(of({ username: 'Asha', email: 'asha@example.com', mobileNumber: '9876543210', userRole: 'Customer' } as any));
    fixture = TestBed.createComponent(UserProfileComponent);
    fixture.detectChanges();
    expect(fixture.componentInstance.profile?.username).toBe('Asha');
    expect(fixture.componentInstance.loading).toBeFalse();
  });

  it('shows a friendly error after a profile request fails', () => {
    auth.getMyProfile.and.returnValue(throwError(() => new Error('offline')));
    fixture = TestBed.createComponent(UserProfileComponent);
    fixture.detectChanges();
    expect(fixture.componentInstance.error).toContain('could not load');
    expect(fixture.componentInstance.loading).toBeFalse();
  });
});
