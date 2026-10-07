export interface Driver {
  driverId?: number;
  driverName: string;
  licenseNumber: string;
  experienceYears: number;
  contactNumber: string;
  availabilityStatus: string; // Initially "Active". Allowed: "Active", "Inactive", "On Leave"
  address: string;
  vehicleType: string; // e.g., "Sedan", "SUV", "Bike"
  hourlyRate: number;
  image: string; // Base64-encoded image of the driver
}
