package com.examly.springapp.service;

import com.examly.springapp.dto.DriverDTO;
import java.util.List;
import java.util.Optional;

public interface DriverService {
    DriverDTO addDriver(DriverDTO driver);
    Optional<DriverDTO> getDriverById(Long driverId);
    List<DriverDTO> getAllDrivers();
    DriverDTO updateDriver(Long driverId, DriverDTO driver);
    DriverDTO deleteDriver(Long driverId);
}
