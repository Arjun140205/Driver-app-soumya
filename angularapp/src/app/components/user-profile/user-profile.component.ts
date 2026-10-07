import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {
  profile: User | null = null;
  loading = true;
  error = '';

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.getMyProfile().subscribe({
      next: (profile) => { this.profile = profile; this.loading = false; },
      error: () => { this.error = 'We could not load your profile. Please try again.'; this.loading = false; }
    });
  }
}
