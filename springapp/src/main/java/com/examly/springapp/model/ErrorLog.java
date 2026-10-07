package com.examly.springapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** One row per handled exception / error, stored in the "ErrorLogs" table. */
@Entity
@Table(name = "ErrorLogs")
public class ErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long errorLogId;

    private LocalDateTime loggedAt;
    private int status;
    private String exceptionType;

    @Column(length = 1000)
    private String message;

    private String path;

    public ErrorLog() {
    }

    public ErrorLog(int status, String exceptionType, String message, String path) {
        this.loggedAt = LocalDateTime.now();
        this.status = status;
        this.exceptionType = exceptionType;
        this.message = message != null && message.length() > 1000 ? message.substring(0, 1000) : message;
        this.path = path;
    }

    public Long getErrorLogId() {
        return errorLogId;
    }

    public void setErrorLogId(Long errorLogId) {
        this.errorLogId = errorLogId;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
