import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DriverService } from '../../services/driver.service';
import { Driver } from '../../models/driver.model';
import { VEHICLE_TYPES } from '../../utils/constants';
import { serverMessage } from '../../utils/http-errors';

const MAX_IMAGE_BYTES = 2 * 1024 * 1024; // 2 MB

@Component({
  selector: 'app-driver-management',
  templateUrl: './driver-management.component.html',
  styleUrls: ['./driver-management.component.css']
})
export class DriverManagementComponent implements OnInit {
  @ViewChild('imageInput') imageInput?: ElementRef<HTMLInputElement>;

  driverForm: FormGroup;
  vehicleTypes: string[] = VEHICLE_TYPES;

  isEditMode: boolean = false;
  driverId?: number;
  availabilityStatus: string = 'Active'; // Initially "Active"
  existingImage: string = '';
  imageBase64: string = '';
  imageError: string = '';

  submitted: boolean = false;
  formError: string = '';
  serverError: string = '';
  successMessage: string = '';
  showSuccessPopup: boolean = false;

  constructor(
    private fb: FormBuilder,
    private driverService: DriverService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.driverForm = this.fb.group({
      driverName: ['', [Validators.required, Validators.pattern(/\S/)]],
      licenseNumber: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9 \-\/]{5,25}$/)]],
      experienceYears: [null, [Validators.required, Validators.min(0), Validators.max(60), Validators.pattern(/^\d+$/)]],
      contactNumber: ['', [Validators.required, Validators.pattern(/^\+?\d{10,13}$/)]],
      vehicleType: ['', [Validators.required]],
      hourlyRate: [null, [Validators.required, Validators.min(1)]],
      address: ['', [Validators.required, Validators.pattern(/\S/)]]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('driverId');
    if (id) {
      this.isEditMode = true;
      this.driverId = Number(id);
      this.driverService.getDriverById(this.driverId).subscribe({
        next: (driver) => {
          this.availabilityStatus = driver.availabilityStatus || 'Active';
          this.existingImage = driver.image || '';
          this.driverForm.patchValue({
            driverName: driver.driverName,
            licenseNumber: driver.licenseNumber,
            experienceYears: driver.experienceYears,
            contactNumber: driver.contactNumber,
            vehicleType: driver.vehicleType,
            hourlyRate: driver.hourlyRate,
            address: driver.address
          });
        },
        error: () => this.router.navigate(['/admin-view-drivers'])
      });
    }
  }

  get f() {
    return this.driverForm.controls;
  }

  showError(field: string): boolean {
    const control = this.driverForm.get(field)!;
    return control.invalid && (control.touched || this.submitted);
  }

  get imageMissing(): boolean {
    return this.submitted && !this.isEditMode && !this.imageBase64;
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files && input.files.length ? input.files[0] : null;
    this.imageError = '';
    this.imageBase64 = '';
    if (!file) {
      return;
    }
    if (!file.type.startsWith('image/')) {
      this.imageError = 'Please choose an image file';
      input.value = '';
      return;
    }
    if (file.size > MAX_IMAGE_BYTES) {
      this.imageError = 'Image must be smaller than 2 MB';
      input.value = '';
      return;
    }
    const reader = new FileReader();
    reader.onload = () => (this.imageBase64 = String(reader.result));
    reader.readAsDataURL(file);
  }

  onSubmit(): void {
    this.submitted = true;
    this.formError = '';
    this.serverError = '';
    const imageOk = this.isEditMode || !!this.imageBase64;
    if (this.driverForm.invalid || !imageOk || this.imageError) {
      this.formError = 'All fields are required';
      return;
    }

    const value = this.driverForm.value;
    const driver: Driver = {
      driverName: String(value.driverName).trim(),
      licenseNumber: String(value.licenseNumber).trim(),
      experienceYears: Number(value.experienceYears),
      contactNumber: String(value.contactNumber).trim(),
      availabilityStatus: this.availabilityStatus,
      address: String(value.address).trim(),
      vehicleType: value.vehicleType,
      hourlyRate: Number(value.hourlyRate),
      image: this.imageBase64 || this.existingImage
    };

    if (this.isEditMode && this.driverId !== undefined) {
      this.driverService.updateDriver(this.driverId, driver).subscribe({
        next: () => this.showSuccess('Driver Updated Successfully!'),
        error: (err) => this.handleError(err)
      });
    } else {
      this.driverService.addDriver(driver).subscribe({
        next: () => this.showSuccess('Driver Added Successfully!'),
        error: (err) => this.handleError(err)
      });
    }
  }

  private showSuccess(message: string): void {
    this.successMessage = message;
    this.showSuccessPopup = true;
  }

  private handleError(err: any): void {
    if (err && err.status === 409) {
      this.serverError = 'A driver with this license number already exists';
    } else if (err && err.status >= 400 && err.status < 500) {
      this.serverError = serverMessage(err, 'Unable to save the driver. Please check the details and try again.');
    }
  }

  closeSuccessPopup(): void {
    this.showSuccessPopup = false;
    if (this.isEditMode) {
      this.router.navigate(['/admin-view-drivers']);
    } else {
      this.resetForm();
    }
  }

  private resetForm(): void {
    this.driverForm.reset({ driverName: '', licenseNumber: '', experienceYears: null, contactNumber: '', vehicleType: '', hourlyRate: null, address: '' });
    this.submitted = false;
    this.formError = '';
    this.imageBase64 = '';
    this.imageError = '';
    if (this.imageInput) {
      this.imageInput.nativeElement.value = '';
    }
  }

  goBack(): void {
    this.router.navigate(['/admin-view-drivers']);
  }
}
