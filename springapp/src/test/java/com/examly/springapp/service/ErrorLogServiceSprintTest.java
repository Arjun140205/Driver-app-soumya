package com.examly.springapp.service;

import com.examly.springapp.model.ErrorLog;
import com.examly.springapp.repository.ErrorLogRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorLogServiceSprintTest {
    @Mock private ErrorLogRepo errorLogRepo;
    @InjectMocks private ErrorLogServiceImpl service;

    @Test
    void createsPersistentErrorRecordWithTimestamp() {
        when(errorLogRepo.save(any(ErrorLog.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ErrorLog saved = service.record(400, "MethodArgumentNotValidException", "Validation failed", "/api/register");
        assertEquals(400, saved.getStatus());
        assertEquals("Validation failed", saved.getMessage());
        assertNotNull(saved.getLoggedAt());
    }
}
