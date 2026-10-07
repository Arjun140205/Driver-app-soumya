import { TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AppComponent } from './app.component';
import { AuthService } from './services/auth.service';

describe('AppComponent', () => {
  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      declarations: [AppComponent],
      schemas: [NO_ERRORS_SCHEMA]
    });
  });

  afterEach(() => localStorage.clear());

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it(`should have as title 'angularapp'`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    expect(fixture.componentInstance.title).toEqual('angularapp');
  });

  it('should render a router outlet and no navbar while logged out', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const el = fixture.nativeElement as HTMLElement;
    expect(el.querySelector('router-outlet')).toBeTruthy();
    expect(el.querySelector('app-adminnav')).toBeNull();
    expect(el.querySelector('app-customernav')).toBeNull();
  });

  it('should render the matching navbar for the logged in role', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const auth = TestBed.inject(AuthService);
    fixture.detectChanges();
    (auth as any).userRoleSubject.next('Admin');
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).querySelector('app-adminnav')).toBeTruthy();
    (auth as any).userRoleSubject.next('Customer');
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).querySelector('app-customernav')).toBeTruthy();
  });
});
