import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DriverRequestService } from '../../services/driver-request.service';
import { AuthService } from '../../services/auth.service';
import { DriverRequest } from '../../models/driver-request.model';
import {
  combineDateTime,
  formatDuration,
  parseDurationToMinutes,
  toDateString,
  toDisplayDate,
  toTimeString
} from '../../utils/format';
import { getDriverImage, useDefaultAvatar } from '../../utils/driver-image';

@Component({
  selector: 'app-customerviewrequested',
  templateUrl: './customerviewrequested.component.html',
  styleUrls: ['./customerviewrequested.component.css']
})
export class CustomerviewrequestedComponent implements OnInit, OnDestroy {
  requests: DriverRequest[] = [];
  loading: boolean = true;
  activeTab: 'ongoing' | 'scheduled' | 'completed' = 'ongoing';
  cancelError: string = '';
  searchText: string = '';

  selectedRequest: DriverRequest | null = null;
  showDetailsModal: boolean = false;
  showDeleteModal: boolean = false;
  showPayModal: boolean = false;
  payLoading: boolean = false;
  private payTimer: any;

  toDateString = toDateString;
  toTimeString = toTimeString;
  toDisplayDate = toDisplayDate;
  getDriverImage = getDriverImage;
  useDefaultAvatar = useDefaultAvatar;

  constructor(
    private driverRequestService: DriverRequestService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadRequests();
  }

  ngOnDestroy(): void {
    clearTimeout(this.payTimer);
  }

  loadRequests(): void {
    const userId = this.authService.getUserId();
    if (userId === null) {
      return;
    }
    this.driverRequestService.getDriverRequestsByUserId(userId).subscribe({
      // The API answers 404 / 204 when the customer has no requests yet.
      next: (requests) => { this.requests = requests || []; this.loading = false; },
      error: () => { this.requests = []; this.loading = false; }
    });
  }

  get tabRequests(): DriverRequest[] {
    return this.filteredRequests.filter((request) => {
      const status = (request.status || '').toLowerCase();
      if (this.activeTab === 'ongoing') return status === 'approved';
      if (this.activeTab === 'scheduled') return status === 'pending';
      return ['trip end', 'closed', 'rejected', 'cancelled'].includes(status);
    });
  }

  get filteredRequests(): DriverRequest[] {
    const query = this.searchText.trim().toLowerCase();
    if (!query) {
      return this.requests;
    }
    return this.requests.filter((request) => (request.driver?.driverName || '').toLowerCase().includes(query));
  }

  isPending(request: DriverRequest): boolean {
    return request.status === 'Pending';
  }

  canEndTrip(request: DriverRequest): boolean {
    return request.status === 'Approved';
  }

  canFetchPayAmount(request: DriverRequest): boolean {
    return request.status === 'Trip End' || request.status === 'Closed';
  }

  // ----- details -----
  showMore(request: DriverRequest): void {
    this.selectedRequest = request;
    this.showDetailsModal = true;
  }

  closeDetails(): void {
    this.showDetailsModal = false;
    this.selectedRequest = null;
  }

  // ----- edit / delete -----
  editRequest(request: DriverRequest): void {
    if (this.isPending(request)) {
      this.router.navigate(['/customer-request/edit', request.driverRequestId]);
    }
  }

  askDelete(request: DriverRequest): void {
    if (this.isPending(request)) {
      this.selectedRequest = request;
      this.showDeleteModal = true;
    }
  }

  cancelDelete(): void {
    this.showDeleteModal = false;
    this.selectedRequest = null;
  }

  confirmDelete(): void {
    const id = this.selectedRequest?.driverRequestId;
    if (id === undefined) {
      return;
    }
    this.driverRequestService.deleteDriverRequest(id).subscribe({
      next: () => {
        const cancelled = this.requests.find((request) => request.driverRequestId === id);
        if (cancelled) cancelled.status = 'Cancelled';
        this.cancelDelete();
      },
      error: (error) => {
        this.cancelError = error?.error?.message || 'Unable to cancel this request.';
      }
    });
  }

  // ----- trip end -----
  endTrip(request: DriverRequest): void {
    if (!this.canEndTrip(request) || request.driverRequestId === undefined) {
      return;
    }
    const now = new Date();
    const start = combineDateTime(request.tripDate, request.timeSlot);
    let minutes = start ? Math.round((now.getTime() - start.getTime()) / 60000) : 0;
    if (minutes <= 0) {
      // The trip "started" in the future (e.g. demo data): fall back to the estimated duration.
      minutes = parseDurationToMinutes(request.estimatedDuration);
    }
    const hourlyRate = Number(request.driver?.hourlyRate || 0);
    const paymentAmount = Math.round(hourlyRate * (minutes / 60) * 100) / 100;

    const update = {
      status: 'Trip End',
      actualDropDate: toDateString(now),
      actualDropTime: toTimeString(now, true),
      actualDuration: formatDuration(minutes),
      paymentAmount
    };
    this.driverRequestService
      .updateDriverRequest(request.driverRequestId, update as unknown as DriverRequest)
      .subscribe({
        next: () => {
          request.status = update.status;
          request.actualDropDate = update.actualDropDate as unknown as Date;
          request.actualDropTime = update.actualDropTime as unknown as Date;
          request.actualDuration = update.actualDuration;
          request.paymentAmount = update.paymentAmount;
        }
      });
  }

  // ----- payment -----
  fetchPayAmount(request: DriverRequest): void {
    this.selectedRequest = request;
    this.showPayModal = true;
    this.payLoading = true;
    clearTimeout(this.payTimer);
    this.payTimer = setTimeout(() => (this.payLoading = false), 1500);
  }

  closePayModal(): void {
    clearTimeout(this.payTimer);
    this.showPayModal = false;
    this.payLoading = false;
    this.selectedRequest = null;
  }

  /** The "Write a Review" button on the request card (enabled for the same requests as "Fetch Pay Amount"). */
  writeReview(request: DriverRequest): void {
    if (!this.canFetchPayAmount(request)) {
      return;
    }
    const driverId = request.driver?.driverId;
    this.router.navigate(['/customerpostfeedback'], { queryParams: driverId !== undefined ? { driverId } : {} });
  }

  canCancel(request: DriverRequest): boolean {
    return ['Pending', 'Approved'].includes(request.status || '');
  }
}
