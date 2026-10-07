package com.examly.springapp.controller;

import com.examly.springapp.dto.DriverDTO;
import com.examly.springapp.dto.validation.OnCreate;
import com.examly.springapp.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @PostMapping("/driver")
    public ResponseEntity<DriverDTO> addDriver(@Validated(OnCreate.class) @RequestBody DriverDTO driver) {
        try {
            DriverDTO savedDriver = driverService.addDriver(driver);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDriver);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<DriverDTO> viewDriverById(@PathVariable Long driverId) {
        Optional<DriverDTO> driverOpt = driverService.getDriverById(driverId);
        if (driverOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(driverOpt.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/driver")
    public ResponseEntity<List<DriverDTO>> viewAllDrivers() {
        List<DriverDTO> drivers = driverService.getAllDrivers();
        if (drivers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(drivers);
    }

    @PutMapping("/driver/{driverId}")
    public ResponseEntity<DriverDTO> updateDriver(@PathVariable Long driverId, @Valid @RequestBody DriverDTO driver) {
        DriverDTO updatedDriver = driverService.updateDriver(driverId, driver);
        if (updatedDriver != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updatedDriver);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/driver/{driverId}")
    public ResponseEntity<DriverDTO> deleteDriver(@PathVariable Long driverId) {
        DriverDTO deletedDriver = driverService.deleteDriver(driverId);
        if (deletedDriver != null) {
            return ResponseEntity.status(HttpStatus.OK).body(deletedDriver);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
