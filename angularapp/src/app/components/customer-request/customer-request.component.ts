import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DriverRequestService } from '../../services/driver-request.service';
import { AuthService } from '../../services/auth.service';
import { DriverRequest } from '../../models/driver-request.model';
import { todayString, toDateString, toTimeString } from '../../utils/format';

@Component({
  selector: 'app-customer-request',
  templateUrl: './customer-request.component.html',
  styleUrls: ['./customer-request.component.css']
})
export class CustomerRequestComponent implements OnInit {
  requestForm: FormGroup;
  minDate: string = todayString();

  isEditMode: boolean = false;
  driverId?: number;
  driverRequestId?: number;
  private originalTripDate: string = '';

  submitted: boolean = false;
  formError: string = '';
  successMessage: string = '';
  showSuccessPopup: boolean = false;

  constructor(
    private fb: FormBuilder,
    private driverRequestService: DriverRequestService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.requestForm = this.fb.group({
      pickupLocation: ['', [Validators.required, Validators.pattern(/\S/)]],
      dropLocation: ['', [Validators.required, Validators.pattern(/\S/)]],
      tripDate: ['', [Validators.required, (control: AbstractControl) => this.notInThePast(control)]],
      timeSlot: ['', [Validators.required]],
      estimatedDuration: ['', [Validators.required, Validators.pattern(/\S/)]],
      comments: ['']
    });
  }

  ngOnInit(): void {
    const requestId = this.route.snapshot.paramMap.get('driverRequestId');
    const driverId = this.route.snapshot.paramMap.get('driverId');
    if (requestId) {
      this.isEditMode = true;
      this.driverRequestId = Number(requestId);
      this.driverRequestService.getDriverRequestById(this.driverRequestId).subscribe({
        next: (request) => {
          this.driverId = request.driver?.driverId;
          this.originalTripDate = toDateString(request.tripDate);
          this.requestForm.patchValue({
            pickupLocation: request.pickupLocation,
            dropLocation: request.dropLocation,
            tripDate: this.originalTripDate,
            timeSlot: toTimeString(request.timeSlot),
            estimatedDuration: request.estimatedDuration,
            comments: request.comments || ''
          });
        },
        error: () => this.router.navigate(['/customerviewrequested'])
      });
    } else if (driverId) {
      this.driverId = Number(driverId);
    }
  }

  /** A trip cannot be booked in the past (an unchanged date of an existing request is allowed). */
  private notInThePast(control: AbstractControl): ValidationErrors | null {
    const value: string = control.value;
    if (!value || value === this.originalTripDate) {
      return null;
    }
    return value < todayString() ? { pastDate: true } : null;
  }

  get f() {
    return this.requestForm.controls;
  }

  showError(field: string): boolean {
    const control = this.requestForm.get(field)!;
    return control.invalid && (control.touched || this.submitted);
  }

  onSubmit(): void {
    this.submitted = true;
    this.formError = '';
    if (this.requestForm.invalid) {
      this.formError = 'All required fields must be filled out';
      return;
    }
    const value = this.requestForm.value;
    const details = {
      tripDate: value.tripDate,
      timeSlot: value.timeSlot,
      pickupLocation: String(value.pickupLocation).trim(),
      dropLocation: String(value.dropLocation).trim(),
      estimatedDuration: String(value.estimatedDuration).trim(),
      comments: value.comments || ''
    };

    if (this.isEditMode && this.driverRequestId !== undefined) {
      this.driverRequestService.updateDriverRequest(this.driverRequestId, details as unknown as DriverRequest).subscribe({
        next: () => this.showSuccess('Request Updated Successfully!')
      });
    } else {
      const userId = this.authService.getUserId();
      const request = {
        ...details,
        user: { userId },
        driver: { driverId: this.driverId },
        requestDate: todayString(),
        status: 'Pending'
      };
      this.driverRequestService.addDriverRequest(request as unknown as DriverRequest).subscribe({
        next: () => this.showSuccess('Request Submitted Successfully!')
      });
    }
  }

  private showSuccess(message: string): void {
    this.successMessage = message;
    this.showSuccessPopup = true;
  }

  closeSuccessPopup(): void {
    this.showSuccessPopup = false;
    this.router.navigate([this.isEditMode ? '/customerviewrequested' : '/customerviewdriver']);
  }

  goBack(): void {
    this.router.navigate([this.isEditMode ? '/customerviewrequested' : '/customerviewdriver']);
  }
}
