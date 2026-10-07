package com.examly.springapp.repository;

import com.examly.springapp.model.DriverRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRequestRepo extends JpaRepository<DriverRequest, Long> {
    List<DriverRequest> findByUserUserId(Long userId);
    List<DriverRequest> findByDriverDriverId(Long driverId);
}
