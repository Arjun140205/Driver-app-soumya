package com.examly.springapp.service;

import com.examly.springapp.dto.DriverDTO;
import com.examly.springapp.exceptions.DuplicateDriverException;
import com.examly.springapp.model.Driver;
import com.examly.springapp.repository.DriverRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverUniquenessSprintTest {
    @Mock private DriverRepo driverRepo;
    @InjectMocks private DriverServiceImpl driverService;

    @Test
    void uniqueLicenseNumberCreatesDriver() {
        DriverDTO input = driver("DL12345");
        when(driverRepo.existsByLicenseNumber("DL12345")).thenReturn(false);
        when(driverRepo.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DriverDTO saved = driverService.addDriver(input);

        assertEquals("DL12345", saved.getLicenseNumber());
        assertEquals("Active", saved.getAvailabilityStatus());
        verify(driverRepo).save(any(Driver.class));
    }

    @Test
    void duplicateLicenseNumberRaisesBusinessException() {
        when(driverRepo.existsByLicenseNumber("DL12345")).thenReturn(true);
        assertThrows(DuplicateDriverException.class, () -> driverService.addDriver(driver("DL12345")));
        verify(driverRepo, never()).save(any(Driver.class));
    }

    private DriverDTO driver(String licenseNumber) {
        DriverDTO dto = new DriverDTO();
        dto.setDriverName("Asha Driver");
        dto.setLicenseNumber(licenseNumber);
        dto.setExperienceYears(5);
        dto.setContactNumber("9876543210");
        dto.setAddress("Pune");
        dto.setVehicleType("Sedan");
        dto.setHourlyRate(250.0);
        return dto;
    }
}
