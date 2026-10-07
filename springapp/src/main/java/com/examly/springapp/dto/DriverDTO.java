package com.examly.springapp.dto;

import com.examly.springapp.dto.validation.OnCreate;
import com.examly.springapp.dto.validation.ValidationPatterns;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for {@link com.examly.springapp.model.Driver}.
 * Validated when a driver is added (POST, group OnCreate) or updated (PUT, default group); the
 * nested "driver" of a driver request / feedback only carries a driverId and is not validated.
 * Used for every driver request body and response, including the nested "driver" in
 * driver requests and feedback (where only {@code driverId} may be filled when sending).
 */
public class DriverDTO {
    private Long driverId;
    @NotBlank(message = "Driver name is required")
    private String driverName;

    @NotBlank(message = "License number is required")
    @Pattern(regexp = ValidationPatterns.LICENSE, message = "License number must be 5-25 letters, digits, spaces, '-' or '/'")
    private String licenseNumber;

    @NotNull(message = "Experience is required")
    @Min(value = 0, message = "Experience must be between 0 and 60 years")
    @Max(value = 60, message = "Experience must be between 0 and 60 years")
    private Integer experienceYears;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = ValidationPatterns.CONTACT, message = "Contact number must be 10-13 digits")
    private String contactNumber;

    // optional: a new driver defaults to "Active", an update without it keeps the current status
    @Pattern(regexp = ValidationPatterns.DRIVER_STATUS, message = "Availability status must be Active, Inactive or On Leave")
    private String availabilityStatus;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Vehicle type is required")
    @Pattern(regexp = ValidationPatterns.VEHICLE_TYPE, message = "Vehicle type must be Sedan, SUV, Hatchback, Truck, Van or Bike")
    private String vehicleType;

    @NotNull(message = "Hourly rate is required")
    @DecimalMin(value = "1", message = "Hourly rate must be at least 1")
    private Double hourlyRate;

    // required when a driver is added; an edit may leave the image unchanged
    @NotBlank(groups = OnCreate.class, message = "Driver image is required")
    @Size(max = 3_000_000, message = "Driver image is too large (maximum 2 MB)")
    private String image;

    public DriverDTO() {
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
