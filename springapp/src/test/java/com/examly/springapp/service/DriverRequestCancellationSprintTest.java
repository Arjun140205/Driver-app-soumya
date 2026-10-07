package com.examly.springapp.service;

import com.examly.springapp.exceptions.InvalidRequestException;
import com.examly.springapp.model.Driver;
import com.examly.springapp.model.DriverRequest;
import com.examly.springapp.repository.DriverRepo;
import com.examly.springapp.repository.DriverRequestRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverRequestCancellationSprintTest {
    @Mock private DriverRequestRepo requestRepo;
    @Mock private DriverRepo driverRepo;

    @Test
    void cancellationReleasesDriverAndKeepsCancelledRequest() {
        DriverRequestServiceImpl service = new DriverRequestServiceImpl(requestRepo, driverRepo, 2);
        Driver driver = new Driver();
        driver.setDriverId(7L);
        driver.setAvailabilityStatus("Inactive");
        DriverRequest request = request("Approved", LocalDate.now().plusDays(1), LocalTime.now(), driver);
        when(requestRepo.findById(9L)).thenReturn(Optional.of(request));
        when(requestRepo.save(any(DriverRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(driverRepo.findById(7L)).thenReturn(Optional.of(driver));

        assertEquals("Cancelled", service.deleteDriverRequest(9L).getStatus());
        assertNull(request.getDriver());
        assertEquals("Active", driver.getAvailabilityStatus());
        verify(driverRepo).save(driver);
    }

    @Test
    void cancellationIsRejectedAfterCutoff() {
        DriverRequestServiceImpl service = new DriverRequestServiceImpl(requestRepo, driverRepo, 2);
        DriverRequest request = request("Approved", LocalDate.now(), LocalTime.now().minusHours(1), null);
        when(requestRepo.findById(9L)).thenReturn(Optional.of(request));
        assertThrows(InvalidRequestException.class, () -> service.deleteDriverRequest(9L));
        verify(requestRepo, never()).save(any());
    }

    private DriverRequest request(String status, LocalDate date, LocalTime time, Driver driver) {
        DriverRequest request = new DriverRequest();
        request.setDriverRequestId(9L);
        request.setStatus(status);
        request.setTripDate(date);
        request.setTimeSlot(time);
        request.setDriver(driver);
        return request;
    }
}
