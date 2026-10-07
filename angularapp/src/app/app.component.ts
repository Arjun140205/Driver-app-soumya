import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'angularapp';

  isAdmin$: Observable<boolean>;
  isCustomer$: Observable<boolean>;

  constructor(private authService: AuthService) {
    this.isAdmin$ = this.authService.userRole$.pipe(map((role) => (role || '').toLowerCase() === 'admin'));
    this.isCustomer$ = this.authService.userRole$.pipe(map((role) => (role || '').toLowerCase() === 'customer'));
  }
}
