import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomePageComponent } from './home-page.component';

describe('HomePageComponent', () => {
  let component: HomePageComponent;
  let fixture: ComponentFixture<HomePageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({ declarations: [HomePageComponent] });
    fixture = TestBed.createComponent(HomePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('shows the application information and contact details', () => {
    const text = (fixture.nativeElement as HTMLElement).textContent || '';
    expect(text).toContain('DriveU');
    expect(text).toContain('Welcome to DriveU');
    expect(text).toContain('Contact Us');
    expect(text).toContain('987-654-3210');
    expect(text).toContain('support@driveu.com');
  });
});
