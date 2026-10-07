import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiUrl } from '../../apiconfig';
import { Driver } from '../models/driver.model';

@Injectable({
  providedIn: 'root'
})
export class DriverService {
  public apiUrl: string = apiUrl;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  getAllDrivers(): Observable<Driver[]> {
    return this.http.get<Driver[]>(`${this.apiUrl}/api/driver`, { headers: this.getHeaders() });
  }

  getDriverById(driverId: number): Observable<Driver> {
    return this.http.get<Driver>(`${this.apiUrl}/api/driver/${driverId}`, { headers: this.getHeaders() });
  }

  addDriver(driver: Driver): Observable<Driver> {
    return this.http.post<Driver>(`${this.apiUrl}/api/driver`, driver, { headers: this.getHeaders() });
  }

  updateDriver(driverId: number, driver: Driver): Observable<Driver> {
    return this.http.put<Driver>(`${this.apiUrl}/api/driver/${driverId}`, driver, { headers: this.getHeaders() });
  }

  deleteDriver(driverId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/driver/${driverId}`, { headers: this.getHeaders() });
  }
}
