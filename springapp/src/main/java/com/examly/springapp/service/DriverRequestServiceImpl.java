package com.examly.springapp.service;

import com.examly.springapp.dto.DriverRequestDTO;
import com.examly.springapp.exceptions.DriverRequestDeletionException;
import com.examly.springapp.mapper.DtoMapper;
import com.examly.springapp.model.DriverRequest;
import com.examly.springapp.model.Driver;
import com.examly.springapp.repository.DriverRepo;
import com.examly.springapp.repository.DriverRequestRepo;
import com.examly.springapp.exceptions.InvalidRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DriverRequestServiceImpl implements DriverRequestService {

    private static final Logger log = LoggerFactory.getLogger(DriverRequestServiceImpl.class);
    private final DriverRequestRepo driverRequestRepo;
    private final DriverRepo driverRepo;
    private final long cancellationCutoffHours;

    public DriverRequestServiceImpl(DriverRequestRepo driverRequestRepo, DriverRepo driverRepo,
            @Value("${cancellation.cutoff-hours:2}") long cancellationCutoffHours) {
        this.driverRequestRepo = driverRequestRepo;
        this.driverRepo = driverRepo;
        this.cancellationCutoffHours = cancellationCutoffHours;
    }

    @Override
    public DriverRequestDTO addDriverRequest(DriverRequestDTO driverRequestDto) {
        DriverRequest driverRequest = DtoMapper.toEntity(driverRequestDto);
        if (driverRequest.getRequestDate() == null) {
            driverRequest.setRequestDate(LocalDate.now());
        }
        if (driverRequest.getStatus() == null) {
            driverRequest.setStatus("Pending"); // a new request always starts as Pending
        }
        return DtoMapper.toDTO(driverRequestRepo.save(driverRequest));
    }

    @Override
    public Optional<DriverRequestDTO> getDriverRequestById(Long driverRequestId) {
        return driverRequestRepo.findById(driverRequestId).map(DtoMapper::toDTO);
    }

    @Override
    public List<DriverRequestDTO> getAllDriverRequests() {
        return DtoMapper.toDriverRequestDTOs(driverRequestRepo.findAll());
    }

    @Override
    public DriverRequestDTO updateDriverRequest(Long driverRequestId, DriverRequestDTO driverRequest) {
        Optional<DriverRequest> existingReqOpt = driverRequestRepo.findById(driverRequestId);
        if (existingReqOpt.isPresent()) {
            DriverRequest existingReq = existingReqOpt.get();
            if (driverRequest.getStatus() != null) existingReq.setStatus(driverRequest.getStatus());
            if (driverRequest.getTripDate() != null) existingReq.setTripDate(driverRequest.getTripDate());
            if (driverRequest.getTimeSlot() != null) existingReq.setTimeSlot(driverRequest.getTimeSlot());
            if (driverRequest.getPickupLocation() != null) existingReq.setPickupLocation(driverRequest.getPickupLocation());
            if (driverRequest.getDropLocation() != null) existingReq.setDropLocation(driverRequest.getDropLocation());
            if (driverRequest.getEstimatedDuration() != null) existingReq.setEstimatedDuration(driverRequest.getEstimatedDuration());
            if (driverRequest.getPaymentAmount() != null) existingReq.setPaymentAmount(driverRequest.getPaymentAmount());
            if (driverRequest.getComments() != null) existingReq.setComments(driverRequest.getComments());
            if (driverRequest.getActualDropTime() != null) existingReq.setActualDropTime(driverRequest.getActualDropTime());
            if (driverRequest.getActualDropDate() != null) existingReq.setActualDropDate(driverRequest.getActualDropDate());
            if (driverRequest.getActualDuration() != null) existingReq.setActualDuration(driverRequest.getActualDuration());
            if (driverRequest.getDriver() != null) existingReq.setDriver(DtoMapper.toEntity(driverRequest.getDriver()));
            return DtoMapper.toDTO(driverRequestRepo.save(existingReq));
        }
        return null;
    }

    @Override
    @Transactional
    public DriverRequestDTO deleteDriverRequest(Long driverRequestId) {
        Optional<DriverRequest> existingReqOpt = driverRequestRepo.findById(driverRequestId);
        if (existingReqOpt.isPresent()) {
            DriverRequest request = existingReqOpt.get();
            String status = request.getStatus();
            if ("Cancelled".equalsIgnoreCase(status) || "Trip End".equalsIgnoreCase(status)
                    || "Closed".equalsIgnoreCase(status) || "Rejected".equalsIgnoreCase(status)) {
                throw new InvalidRequestException("This request can no longer be cancelled.");
            }
            if ("Approved".equalsIgnoreCase(status)) {
                LocalDateTime departure = request.getTripDate() == null || request.getTimeSlot() == null
                        ? null : LocalDateTime.of(request.getTripDate(), request.getTimeSlot());
                if (departure == null || !LocalDateTime.now().isBefore(departure.minusHours(cancellationCutoffHours))) {
                    throw new InvalidRequestException("Cancellation is no longer available because the cancellation window has expired.");
                }
            }
            Driver assignedDriver = request.getDriver();
            request.setStatus("Cancelled");
            request.setDriver(null);
            DriverRequest saved = driverRequestRepo.save(request);
            if (assignedDriver != null) {
                driverRepo.findById(assignedDriver.getDriverId()).ifPresent(driver -> {
                    driver.setAvailabilityStatus("Active");
                    driverRepo.save(driver);
                });
            }
            log.info("Cancelled driver request id={} and released assigned driver", driverRequestId);
            return DtoMapper.toDTO(saved);
        }
        return null;
    }

    @Override
    public List<DriverRequestDTO> findDriverRequestsByUserId(Long userId) {
        return driverRequestRepo.findByUserUserId(userId).stream().map(DtoMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<DriverRequestDTO> findDriverRequestsByDriverId(Long driverId) {
        return driverRequestRepo.findByDriverDriverId(driverId).stream().map(DtoMapper::toDTO).collect(Collectors.toList());
    }
}
