package com.examly.springapp.controller;

import com.examly.springapp.dto.DriverRequestDTO;
import com.examly.springapp.dto.validation.OnCreate;
import com.examly.springapp.service.DriverRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class DriverRequestController {

    @Autowired
    private DriverRequestService driverRequestService;

    @PostMapping("/driverRequest")
    public ResponseEntity<DriverRequestDTO> addDriverRequest(@Validated(OnCreate.class) @RequestBody DriverRequestDTO driverRequest) {
        DriverRequestDTO savedRequest = driverRequestService.addDriverRequest(driverRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRequest);
    }

    @GetMapping("/driverRequest/{driverRequestId}")
    public ResponseEntity<DriverRequestDTO> viewDriverRequestById(@PathVariable Long driverRequestId) {
        Optional<DriverRequestDTO> reqOpt = driverRequestService.getDriverRequestById(driverRequestId);
        if (reqOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(reqOpt.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/driverRequest/user/{userId}")
    public ResponseEntity<List<DriverRequestDTO>> viewDriverRequestsByUserId(@PathVariable Long userId) {
        List<DriverRequestDTO> requests = driverRequestService.findDriverRequestsByUserId(userId);
        if (requests == null || requests.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(requests);
    }

    @GetMapping("/driverRequest")
    public ResponseEntity<List<DriverRequestDTO>> viewAllDriverRequests() {
        List<DriverRequestDTO> requests = driverRequestService.getAllDriverRequests();
        if (requests.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(requests);
    }

    @PutMapping("/driverRequest/{driverRequestId}")
    public ResponseEntity<DriverRequestDTO> updateDriverRequest(@PathVariable Long driverRequestId, @RequestBody DriverRequestDTO driverRequest) {
        DriverRequestDTO updated = driverRequestService.updateDriverRequest(driverRequestId, driverRequest);
        if (updated != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/driverRequest/driver/{driverId}")
    public ResponseEntity<List<DriverRequestDTO>> viewDriverRequestsByDriverId(@PathVariable Long driverId) {
        List<DriverRequestDTO> requests = driverRequestService.findDriverRequestsByDriverId(driverId);
        if (requests == null || requests.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(requests);
    }

    @DeleteMapping("/driverRequest/{driverRequestId}")
    public ResponseEntity<DriverRequestDTO> deleteDriverRequest(@PathVariable Long driverRequestId) {
        DriverRequestDTO deleted = driverRequestService.deleteDriverRequest(driverRequestId);
        if (deleted != null) {
            return ResponseEntity.status(HttpStatus.OK).body(deleted);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
