package com.examly.springapp.service;

import com.examly.springapp.dto.DriverDTO;
import com.examly.springapp.exceptions.DriverDeletionException;
import com.examly.springapp.exceptions.DuplicateDriverException;
import com.examly.springapp.mapper.DtoMapper;
import com.examly.springapp.model.Driver;
import com.examly.springapp.repository.DriverRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverRepo driverRepo;

    @Override
    public DriverDTO addDriver(DriverDTO driverDto) {
        Optional<Driver> existingDriver = driverRepo.findByLicenseNumber(driverDto.getLicenseNumber());
        if (existingDriver.isPresent()) {
            throw new DuplicateDriverException("Driver with license number " + driverDto.getLicenseNumber() + " already exists.");
        }
        Driver driver = DtoMapper.toEntity(driverDto);
        if (driver.getAvailabilityStatus() == null) {
            driver.setAvailabilityStatus("Active"); // a new driver starts as Active
        }
        return DtoMapper.toDTO(driverRepo.save(driver));
    }

    @Override
    public Optional<DriverDTO> getDriverById(Long driverId) {
        return driverRepo.findById(driverId).map(DtoMapper::toDTO);
    }

    @Override
    public List<DriverDTO> getAllDrivers() {
        return DtoMapper.toDriverDTOs(driverRepo.findAll());
    }

    @Override
    public DriverDTO updateDriver(Long driverId, DriverDTO driverDto) {
        Optional<Driver> existingDriverOpt = driverRepo.findById(driverId);
        if (existingDriverOpt.isPresent()) {
            Driver existingDriver = existingDriverOpt.get();
            existingDriver.setDriverName(driverDto.getDriverName());
            existingDriver.setLicenseNumber(driverDto.getLicenseNumber());
            existingDriver.setExperienceYears(driverDto.getExperienceYears());
            existingDriver.setContactNumber(driverDto.getContactNumber());
            if (driverDto.getAvailabilityStatus() != null) {
                existingDriver.setAvailabilityStatus(driverDto.getAvailabilityStatus());
            }
            existingDriver.setAddress(driverDto.getAddress());
            existingDriver.setVehicleType(driverDto.getVehicleType());
            existingDriver.setHourlyRate(driverDto.getHourlyRate());
            existingDriver.setImage(driverDto.getImage());
            return DtoMapper.toDTO(driverRepo.save(existingDriver));
        }
        return null;
    }

    @Override
    public DriverDTO deleteDriver(Long driverId) {
        Optional<Driver> existingDriver = driverRepo.findById(driverId);
        if (existingDriver.isPresent()) {
            try {
                driverRepo.delete(existingDriver.get());
                return DtoMapper.toDTO(existingDriver.get());
            } catch (Exception e) {
                throw new DriverDeletionException("Failed to delete driver with ID: " + driverId);
            }
        }
        return null;
    }
}
