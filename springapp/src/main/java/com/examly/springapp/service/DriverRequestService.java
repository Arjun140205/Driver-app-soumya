package com.examly.springapp.service;

import com.examly.springapp.dto.DriverRequestDTO;
import java.util.List;
import java.util.Optional;

public interface DriverRequestService {
    DriverRequestDTO addDriverRequest(DriverRequestDTO driverRequest);
    Optional<DriverRequestDTO> getDriverRequestById(Long driverRequestId);
    List<DriverRequestDTO> getAllDriverRequests();
    DriverRequestDTO updateDriverRequest(Long driverRequestId, DriverRequestDTO driverRequest);
    DriverRequestDTO deleteDriverRequest(Long driverRequestId);
    List<DriverRequestDTO> findDriverRequestsByUserId(Long userId);
    List<DriverRequestDTO> findDriverRequestsByDriverId(Long driverId);
}
