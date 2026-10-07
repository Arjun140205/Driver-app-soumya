import { Injectable, Injector } from '@angular/core';
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from './auth.service';

/**
 * Sends the user to the error page when the server is unreachable or fails (status 0 / 5xx)
 * and back to the login page when a stored token is no longer accepted (401).
 * Business errors (4xx) are left for the components to handle.
 */
@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
  // AuthService is resolved lazily: it depends on HttpClient, which depends on this interceptor.
  constructor(private router: Router, private injector: Injector) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: unknown) => {
        const isAuthCall =
          request.url.includes('/api/login') || request.url.includes('/api/register') || request.url.includes('/api/otp/') ||
          request.url.includes('/api/auth/validate');
        const isAiCall = request.url.includes('/api/ai/'); // AI features degrade gracefully
        if (error instanceof HttpErrorResponse && !isAuthCall && !isAiCall) {
          if (error.status === 0 || error.status >= 500) {
            this.router.navigate(['/error']);
          } else if (error.status === 401 && localStorage.getItem('token')) {
            this.injector.get(AuthService).logout();
            this.router.navigate(['/login']);
          }
        }
        return throwError(() => error);
      })
    );
  }
}
