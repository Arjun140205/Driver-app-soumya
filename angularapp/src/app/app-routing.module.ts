import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthGuard, GuestGuard } from './components/authguard/authguard.guard';
import { LoginComponent } from './components/login/login.component';
import { SignupComponent } from './components/signup/signup.component';
import { HomePageComponent } from './components/home-page/home-page.component';
import { DriverManagementComponent } from './components/driver-management/driver-management.component';
import { AddAdminComponent } from './components/add-admin/add-admin.component';
import { AdminViewDriversComponent } from './components/admin-view-drivers/admin-view-drivers.component';
import { AdminviewrequestsComponent } from './components/adminviewrequests/adminviewrequests.component';
import { AdminviewfeedbackComponent } from './components/adminviewfeedback/adminviewfeedback.component';
import { CustomerviewdriverComponent } from './components/customerviewdriver/customerviewdriver.component';
import { CustomerRequestComponent } from './components/customer-request/customer-request.component';
import { CustomerviewrequestedComponent } from './components/customerviewrequested/customerviewrequested.component';
import { CustomerpostfeedbackComponent } from './components/customerpostfeedback/customerpostfeedback.component';
import { CustomerviewfeedbackComponent } from './components/customerviewfeedback/customerviewfeedback.component';
import { ErrorComponent } from './components/error/error.component';

const routes: Routes = [
  // The login page is the first page rendered when the application loads.
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent, canActivate: [GuestGuard] },
  { path: 'signup', component: SignupComponent, canActivate: [GuestGuard] },

  // Common (Admin and Customer)
  { path: 'home', component: HomePageComponent, canActivate: [AuthGuard] },

  // Admin
  { path: 'driver-management', component: DriverManagementComponent, canActivate: [AuthGuard], data: { role: 'Admin' } },
  { path: 'driver-management/:driverId', component: DriverManagementComponent, canActivate: [AuthGuard], data: { role: 'Admin' } },
  { path: 'admin-view-drivers', component: AdminViewDriversComponent, canActivate: [AuthGuard], data: { role: 'Admin' } },
  { path: 'adminviewrequests', component: AdminviewrequestsComponent, canActivate: [AuthGuard], data: { role: 'Admin' } },
  { path: 'add-admin', component: AddAdminComponent, canActivate: [AuthGuard], data: { role: 'Admin' } },
  { path: 'adminviewfeedback', component: AdminviewfeedbackComponent, canActivate: [AuthGuard], data: { role: 'Admin' } },

  // Customer
  { path: 'customerviewdriver', component: CustomerviewdriverComponent, canActivate: [AuthGuard], data: { role: 'Customer' } },
  { path: 'customer-request/:driverId', component: CustomerRequestComponent, canActivate: [AuthGuard], data: { role: 'Customer' } },
  { path: 'customer-request/edit/:driverRequestId', component: CustomerRequestComponent, canActivate: [AuthGuard], data: { role: 'Customer' } },
  { path: 'customerviewrequested', component: CustomerviewrequestedComponent, canActivate: [AuthGuard], data: { role: 'Customer' } },
  { path: 'customerpostfeedback', component: CustomerpostfeedbackComponent, canActivate: [AuthGuard], data: { role: 'Customer' } },
  { path: 'customerviewfeedback', component: CustomerviewfeedbackComponent, canActivate: [AuthGuard], data: { role: 'Customer' } },

  // Error page
  { path: 'error', component: ErrorComponent },
  { path: '**', component: ErrorComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
