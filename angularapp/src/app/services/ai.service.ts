import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiUrl } from '../../apiconfig';
import { Driver } from '../models/driver.model';
import { DriverFeedbackSummary } from '../models/ai.model';

@Injectable({
  providedIn: 'root'
})
export class AiService {
  public apiUrl: string = apiUrl;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  /** Semantic driver search: the natural-language query is sent as the request body. */
  searchDrivers(query: string): Observable<Driver[]> {
    const headers = this.getHeaders().set('Content-Type', 'text/plain');
    return this.http.post<Driver[]>(`${this.apiUrl}/api/ai/driver-search`, query, { headers });
  }

  getDriverFeedbackSummary(driverId: number): Observable<DriverFeedbackSummary> {
    return this.http.get<DriverFeedbackSummary>(
      `${this.apiUrl}/api/ai/driver/${driverId}/feedback-summary`,
      { headers: this.getHeaders() }
    );
  }
}
