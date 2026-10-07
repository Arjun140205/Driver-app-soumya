import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { AuthGuard } from './authguard.guard';
import { AuthService } from '../../services/auth.service';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let auth: AuthService;
  let router: Router;

  const routeFor = (role?: string) => ({ data: role ? { role } : {} } as unknown as ActivatedRouteSnapshot);

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({ imports: [RouterTestingModule, HttpClientTestingModule] });
    guard = TestBed.inject(AuthGuard);
    auth = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
  });

  afterEach(() => localStorage.clear());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('redirects to the login page when nobody is logged in', () => {
    const result = guard.canActivate(routeFor('Admin'));
    expect(router.serializeUrl(result as any)).toBe('/login');
  });

  it('redirects to the login page when the role does not match', () => {
    localStorage.setItem('token', 'abc');
    localStorage.setItem('userRole', 'Customer');
    const result = guard.canActivate(routeFor('Admin'));
    expect(router.serializeUrl(result as any)).toBe('/login');
  });

  it('allows a logged in user with the right role', () => {
    localStorage.setItem('token', 'abc');
    localStorage.setItem('userRole', 'Admin');
    expect(guard.canActivate(routeFor('Admin'))).toBeTrue();
    expect(auth.isLoggedIn()).toBeTrue();
  });
});
