package com.examly.springapp.mapper;

import com.examly.springapp.dto.DriverDTO;
import com.examly.springapp.dto.DriverRequestDTO;
import com.examly.springapp.dto.ErrorLogDTO;
import com.examly.springapp.dto.FeedbackDTO;
import com.examly.springapp.dto.UserDTO;
import com.examly.springapp.model.Driver;
import com.examly.springapp.model.DriverRequest;
import com.examly.springapp.model.ErrorLog;
import com.examly.springapp.model.Feedback;
import com.examly.springapp.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * The single place where entities and DTOs are converted into each other.
 * Controllers and services talk in DTOs; only the repositories ever see entities.
 * Every method is null-safe (a null input gives a null output).
 */
public final class DtoMapper {

    private DtoMapper() {
    }

    // ------------------------------------------------------------------ User

    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        // the (encoded) password is deliberately not copied into the DTO
        dto.setUsername(user.getUsername());
        dto.setMobileNumber(user.getMobileNumber());
        dto.setUserRole(user.getUserRole());
        return dto;
    }

    public static User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setUsername(dto.getUsername());
        user.setMobileNumber(dto.getMobileNumber());
        user.setUserRole(dto.getUserRole());
        return user;
    }

    // ------------------------------------------------------------------ Driver

    public static DriverDTO toDTO(Driver driver) {
        if (driver == null) {
            return null;
        }
        DriverDTO dto = new DriverDTO();
        dto.setDriverId(driver.getDriverId());
        dto.setDriverName(driver.getDriverName());
        dto.setLicenseNumber(driver.getLicenseNumber());
        dto.setExperienceYears(driver.getExperienceYears());
        dto.setContactNumber(driver.getContactNumber());
        dto.setAvailabilityStatus(driver.getAvailabilityStatus());
        dto.setAddress(driver.getAddress());
        dto.setVehicleType(driver.getVehicleType());
        dto.setHourlyRate(driver.getHourlyRate());
        dto.setImage(driver.getImage());
        return dto;
    }

    public static Driver toEntity(DriverDTO dto) {
        if (dto == null) {
            return null;
        }
        Driver driver = new Driver();
        driver.setDriverId(dto.getDriverId());
        driver.setDriverName(dto.getDriverName());
        driver.setLicenseNumber(dto.getLicenseNumber());
        driver.setExperienceYears(dto.getExperienceYears());
        driver.setContactNumber(dto.getContactNumber());
        driver.setAvailabilityStatus(dto.getAvailabilityStatus());
        driver.setAddress(dto.getAddress());
        driver.setVehicleType(dto.getVehicleType());
        driver.setHourlyRate(dto.getHourlyRate());
        driver.setImage(dto.getImage());
        return driver;
    }

    public static List<DriverDTO> toDriverDTOs(List<Driver> drivers) {
        List<DriverDTO> result = new ArrayList<>();
        if (drivers != null) {
            for (Driver driver : drivers) {
                result.add(toDTO(driver));
            }
        }
        return result;
    }

    // ------------------------------------------------------------------ DriverRequest

    public static DriverRequestDTO toDTO(DriverRequest request) {
        if (request == null) {
            return null;
        }
        DriverRequestDTO dto = new DriverRequestDTO();
        dto.setDriverRequestId(request.getDriverRequestId());
        dto.setUser(toDTO(request.getUser()));
        dto.setDriver(toDTO(request.getDriver()));
        dto.setRequestDate(request.getRequestDate());
        dto.setStatus(request.getStatus());
        dto.setTripDate(request.getTripDate());
        dto.setTimeSlot(request.getTimeSlot());
        dto.setPickupLocation(request.getPickupLocation());
        dto.setDropLocation(request.getDropLocation());
        dto.setEstimatedDuration(request.getEstimatedDuration());
        dto.setPaymentAmount(request.getPaymentAmount());
        dto.setComments(request.getComments());
        dto.setActualDropTime(request.getActualDropTime());
        dto.setActualDropDate(request.getActualDropDate());
        dto.setActualDuration(request.getActualDuration());
        return dto;
    }

    public static DriverRequest toEntity(DriverRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        DriverRequest request = new DriverRequest();
        request.setDriverRequestId(dto.getDriverRequestId());
        request.setUser(toEntity(dto.getUser()));
        request.setDriver(toEntity(dto.getDriver()));
        request.setRequestDate(dto.getRequestDate());
        request.setStatus(dto.getStatus());
        request.setTripDate(dto.getTripDate());
        request.setTimeSlot(dto.getTimeSlot());
        request.setPickupLocation(dto.getPickupLocation());
        request.setDropLocation(dto.getDropLocation());
        request.setEstimatedDuration(dto.getEstimatedDuration());
        request.setPaymentAmount(dto.getPaymentAmount());
        request.setComments(dto.getComments());
        request.setActualDropTime(dto.getActualDropTime());
        request.setActualDropDate(dto.getActualDropDate());
        request.setActualDuration(dto.getActualDuration());
        return request;
    }

    public static List<DriverRequestDTO> toDriverRequestDTOs(List<DriverRequest> requests) {
        List<DriverRequestDTO> result = new ArrayList<>();
        if (requests != null) {
            for (DriverRequest request : requests) {
                result.add(toDTO(request));
            }
        }
        return result;
    }

    // ------------------------------------------------------------------ Feedback

    public static FeedbackDTO toDTO(Feedback feedback) {
        if (feedback == null) {
            return null;
        }
        FeedbackDTO dto = new FeedbackDTO();
        dto.setFeedbackId(feedback.getFeedbackId());
        dto.setFeedbackText(feedback.getFeedbackText());
        dto.setDate(feedback.getDate());
        dto.setUser(toDTO(feedback.getUser()));
        dto.setDriver(toDTO(feedback.getDriver()));
        dto.setCategory(feedback.getCategory());
        dto.setRating(feedback.getRating());
        dto.setSentiment(feedback.getSentiment());
        dto.setSentimentScore(feedback.getSentimentScore());
        dto.setAiTags(feedback.getAiTags());
        return dto;
    }

    public static Feedback toEntity(FeedbackDTO dto) {
        if (dto == null) {
            return null;
        }
        Feedback feedback = new Feedback();
        feedback.setFeedbackId(dto.getFeedbackId());
        feedback.setFeedbackText(dto.getFeedbackText());
        feedback.setDate(dto.getDate());
        feedback.setUser(toEntity(dto.getUser()));
        feedback.setDriver(toEntity(dto.getDriver()));
        feedback.setCategory(dto.getCategory());
        feedback.setRating(dto.getRating());
        feedback.setSentiment(dto.getSentiment());
        feedback.setSentimentScore(dto.getSentimentScore());
        feedback.setAiTags(dto.getAiTags());
        return feedback;
    }

    public static List<FeedbackDTO> toFeedbackDTOs(List<Feedback> feedbacks) {
        List<FeedbackDTO> result = new ArrayList<>();
        if (feedbacks != null) {
            for (Feedback feedback : feedbacks) {
                result.add(toDTO(feedback));
            }
        }
        return result;
    }

    // ------------------------------------------------------------------ ErrorLog

    public static ErrorLogDTO toDTO(ErrorLog log) {
        if (log == null) {
            return null;
        }
        ErrorLogDTO dto = new ErrorLogDTO(log.getStatus(), log.getExceptionType(), log.getMessage(), log.getPath());
        dto.setErrorLogId(log.getErrorLogId());
        dto.setLoggedAt(log.getLoggedAt());
        return dto;
    }

    public static ErrorLog toEntity(ErrorLogDTO dto) {
        if (dto == null) {
            return null;
        }
        // the entity constructor stamps the time and trims over-long messages to the column size
        ErrorLog log = new ErrorLog(dto.getStatus(), dto.getExceptionType(), dto.getMessage(), dto.getPath());
        log.setErrorLogId(dto.getErrorLogId());
        if (dto.getLoggedAt() != null) {
            log.setLoggedAt(dto.getLoggedAt());
        }
        return log;
    }
}
