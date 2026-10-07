import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiUrl } from '../../apiconfig';
import { DriverRequest } from '../models/driver-request.model';

@Injectable({
  providedIn: 'root'
})
export class DriverRequestService {
  public apiUrl: string = apiUrl;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  getAllDriverRequests(): Observable<DriverRequest[]> {
    return this.http.get<DriverRequest[]>(`${this.apiUrl}/api/driverRequest`, { headers: this.getHeaders() });
  }

  getDriverRequestById(driverRequestId: number): Observable<DriverRequest> {
    return this.http.get<DriverRequest>(`${this.apiUrl}/api/driverRequest/${driverRequestId}`, { headers: this.getHeaders() });
  }

  getDriverRequestsByUserId(userId: number): Observable<DriverRequest[]> {
    return this.http.get<DriverRequest[]>(`${this.apiUrl}/api/driverRequest/user/${userId}`, { headers: this.getHeaders() });
  }

  getDriverRequestsByDriverId(driverId: number): Observable<DriverRequest[]> {
    return this.http.get<DriverRequest[]>(`${this.apiUrl}/api/driverRequest/driver/${driverId}`, { headers: this.getHeaders() });
  }

  addDriverRequest(driverRequest: DriverRequest): Observable<DriverRequest> {
    return this.http.post<DriverRequest>(`${this.apiUrl}/api/driverRequest`, driverRequest, { headers: this.getHeaders() });
  }

  updateDriverRequest(driverRequestId: number, driverRequest: DriverRequest): Observable<DriverRequest> {
    return this.http.put<DriverRequest>(`${this.apiUrl}/api/driverRequest/${driverRequestId}`, driverRequest, { headers: this.getHeaders() });
  }

  deleteDriverRequest(driverRequestId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/driverRequest/${driverRequestId}`, { headers: this.getHeaders() });
  }
}
