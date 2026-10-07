package com.examly.springapp.service;

import com.examly.springapp.model.ErrorLog;
import com.examly.springapp.repository.ErrorLogRepo;
import org.springframework.stereotype.Service;

@Service
public class ErrorLogServiceImpl implements ErrorLogService {
    private final ErrorLogRepo errorLogRepo;

    public ErrorLogServiceImpl(ErrorLogRepo errorLogRepo) {
        this.errorLogRepo = errorLogRepo;
    }

    @Override
    public ErrorLog record(int status, String exceptionType, String message, String path) {
        return errorLogRepo.save(new ErrorLog(status, exceptionType, message, path));
    }
}
