package com.examly.springapp.service;

import com.examly.springapp.model.ErrorLog;

public interface ErrorLogService {
    ErrorLog record(int status, String exceptionType, String message, String path);
}
