import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { FeedbackService } from '../../services/feedback.service';
import { AuthService } from '../../services/auth.service';
import { Feedback } from '../../models/feedback.model';
import { FEEDBACK_CATEGORIES } from '../../utils/constants';
import { todayString } from '../../utils/format';

@Component({
  selector: 'app-customerpostfeedback',
  templateUrl: './customerpostfeedback.component.html',
  styleUrls: ['./customerpostfeedback.component.css']
})
export class CustomerpostfeedbackComponent implements OnInit {
  feedbackForm: FormGroup;
  categories: string[] = FEEDBACK_CATEGORIES;
  stars: number[] = [1, 2, 3, 4, 5];
  driverId?: number;

  submitted: boolean = false;
  formError: string = '';
  showSuccessPopup: boolean = false;

  constructor(
    private fb: FormBuilder,
    private feedbackService: FeedbackService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.feedbackForm = this.fb.group({
      category: ['', [Validators.required]],
      rating: [0, [Validators.required, Validators.min(1), Validators.max(5)]],
      feedbackText: ['', [Validators.required, Validators.pattern(/\S/)]]
    });
  }

  ngOnInit(): void {
    const driverId = this.route.snapshot.queryParamMap?.get('driverId');
    if (driverId) {
      this.driverId = Number(driverId);
    }
  }

  get f() {
    return this.feedbackForm.controls;
  }

  setRating(rating: number): void {
    this.feedbackForm.patchValue({ rating });
    this.feedbackForm.get('rating')!.markAsTouched();
  }

  showError(field: string): boolean {
    const control = this.feedbackForm.get(field)!;
    return control.invalid && (control.touched || this.submitted);
  }

  onSubmit(): void {
    this.submitted = true;
    this.formError = '';
    if (this.feedbackForm.invalid) {
      this.formError = 'All fields are required';
      return;
    }
    const userId = this.authService.getUserId();
    const value = this.feedbackForm.value;
    const feedback = {
      feedbackText: String(value.feedbackText).trim(),
      category: value.category,
      rating: Number(value.rating),
      date: todayString(),
      userId,
      user: { userId },
      driver: this.driverId !== undefined ? { driverId: this.driverId } : undefined
    };
    this.feedbackService.sendFeedback(feedback as unknown as Feedback).subscribe({
      next: () => (this.showSuccessPopup = true)
    });
  }

  closeSuccessPopup(): void {
    this.showSuccessPopup = false;
    this.router.navigate(['/customerviewfeedback']);
  }

  goBack(): void {
    this.router.navigate(['/customerviewrequested']);
  }
}
