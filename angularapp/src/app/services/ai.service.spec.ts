import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { AiService } from './ai.service';

describe('AiService', () => {
  let service: AiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(AiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should POST the natural language query to /api/ai/driver-search', () => {
    service.searchDrivers('van driver').subscribe();
    const req = http.expectOne(`${service.apiUrl}/api/ai/driver-search`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBe('van driver');
    expect(req.request.headers.has('Authorization')).toBeTrue();
    req.flush([]);
  });

  it('should GET the feedback summary of a driver', () => {
    service.getDriverFeedbackSummary(4).subscribe();
    const req = http.expectOne(`${service.apiUrl}/api/ai/driver/4/feedback-summary`);
    expect(req.request.method).toBe('GET');
    req.flush({});
  });
});
