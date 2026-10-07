import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-customernav',
  templateUrl: './customernav.component.html',
  styleUrls: ['./customernav.component.css']
})
export class CustomernavComponent implements OnInit, OnDestroy {
  username: string = '';
  userRole: string = '';
  showLogoutPopup: boolean = false;

  private subscriptions = new Subscription();

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.subscriptions.add(this.authService.username$.subscribe((name) => (this.username = name || '')));
    this.subscriptions.add(this.authService.userRole$.subscribe((role) => (this.userRole = role || '')));
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  openLogoutPopup(): void {
    this.showLogoutPopup = true;
  }

  cancelLogout(): void {
    this.showLogoutPopup = false;
  }

  confirmLogout(): void {
    this.showLogoutPopup = false;
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
