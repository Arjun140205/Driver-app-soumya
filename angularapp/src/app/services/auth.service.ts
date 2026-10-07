import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, tap, timeout } from 'rxjs/operators';
import { apiUrl } from '../../apiconfig';
import { User } from '../models/user.model';
import { Login } from '../models/login.model';

/** What the OTP endpoints answer when an OTP was sent. */
export interface OtpSendResponse {
  message: string;
  /** 'email' / 'sms', or 'console' when the server has no mail / SMS provider configured (development mode). */
  delivery: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  public apiUrl: string = apiUrl;

  private userRoleSubject = new BehaviorSubject<string | null>(this.readStored('userRole'));
  private userIdSubject = new BehaviorSubject<number | null>(this.toNumber(this.readStored('userId')));
  private usernameSubject = new BehaviorSubject<string | null>(this.readStored('username'));

  userRole$: Observable<string | null> = this.userRoleSubject.asObservable();
  userId$: Observable<number | null> = this.userIdSubject.asObservable();
  username$: Observable<string | null> = this.usernameSubject.asObservable();

  constructor(private http: HttpClient) {}

  /** Public sign-up (customers only; the e-mail and mobile number must be OTP-verified first). */
  register(user: User): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/register`, user);
  }

  // ----- OTP verification used by the sign-up page -----
  sendEmailOtp(email: string): Observable<OtpSendResponse> {
    return this.http.post<OtpSendResponse>(`${this.apiUrl}/api/otp/email/send`, { email });
  }

  verifyEmailOtp(email: string, otp: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/otp/email/verify`, { email, otp });
  }

  sendMobileOtp(mobileNumber: string): Observable<OtpSendResponse> {
    return this.http.post<OtpSendResponse>(`${this.apiUrl}/api/otp/mobile/send`, { mobileNumber });
  }

  verifyMobileOtp(mobileNumber: string, otp: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/otp/mobile/verify`, { mobileNumber, otp });
  }

  /** Admin only: creates another admin account. */
  registerAdmin(user: User): Observable<any> {
    const headers = new HttpHeaders({ Authorization: `Bearer ${this.getToken()}` });
    return this.http.post(`${this.apiUrl}/api/admin/register`, user, { headers });
  }

  login(login: Login): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/api/login`, login).pipe(
      tap((response) => {
        if (response && response.token) {
          localStorage.setItem('token', response.token);
          localStorage.setItem('userRole', response.userRole);
          localStorage.setItem('userId', String(response.userId));
          localStorage.setItem('username', response.username);
          this.userRoleSubject.next(response.userRole);
          this.userIdSubject.next(Number(response.userId));
          this.usernameSubject.next(response.username);
        }
      })
    );
  }

  /**
   * Runs once when the app starts. A token found in the browser is only trusted after the server
   * confirms it (it may belong to a server that was restarted, a deleted user, or a changed role):
   *  - the server accepts it  -> the stored username / role / id are replaced by the server's values
   *  - the server rejects it  -> the browser's login is cleared, so the login page is shown
   *  - the server cannot be reached -> the stored login is kept (the pages report the problem themselves)
   */
  validateSession(): Observable<boolean> {
    const token = this.getToken();
    if (!token) {
      return of(false);
    }
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
    return this.http.get<any>(`${this.apiUrl}/api/auth/validate`, { headers }).pipe(
      timeout(4000),
      tap((session) => this.storeSession(session.userRole, session.userId, session.username)),
      map(() => true),
      catchError((err) => {
        if (err && (err.status === 401 || err.status === 403)) {
          this.logout();
          return of(false);
        }
        return of(true);
      })
    );
  }

  private storeSession(userRole: string, userId: number, username: string): void {
    localStorage.setItem('userRole', userRole);
    localStorage.setItem('userId', String(userId));
    localStorage.setItem('username', username);
    this.userRoleSubject.next(userRole);
    this.userIdSubject.next(Number(userId));
    this.usernameSubject.next(username);
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    this.userRoleSubject.next(null);
    this.userIdSubject.next(null);
    this.usernameSubject.next(null);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }
    if (this.isTokenExpired(token)) {
      this.logout();
      return false;
    }
    return true;
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUserRole(): string | null {
    return localStorage.getItem('userRole');
  }

  getUserId(): number | null {
    return this.toNumber(localStorage.getItem('userId'));
  }

  getMyProfile(): Observable<User> {
    const headers = new HttpHeaders({ Authorization: `Bearer ${this.getToken()}` });
    return this.http.get<User>(`${this.apiUrl}/api/users/me`, { headers });
  }

  getUsername(): string | null {
    return localStorage.getItem('username');
  }

  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
      return !!payload.exp && payload.exp * 1000 < Date.now();
    } catch {
      // A token we cannot decode is left for the server to judge.
      return false;
    }
  }

  private readStored(key: string): string | null {
    try {
      return localStorage.getItem(key);
    } catch {
      return null;
    }
  }

  private toNumber(value: string | null): number | null {
    if (value === null || value === '' || isNaN(Number(value))) {
      return null;
    }
    return Number(value);
  }
}
