package com.examly.springapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.examly.springapp.dto.validation.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object for {@link com.examly.springapp.model.User}.
 * It is the request body of "register" and "add admin" (the constraints below are checked there)
 * and the nested "user" inside driver requests and feedback, which only carries a userId and is
 * not validated. The entity itself is never exposed.
 */
public class UserDTO {
    private Long userId;
    @NotBlank(message = "Email is required")
    @Pattern(regexp = ValidationPatterns.EMAIL, message = "Please enter a valid email address")
    private String email;
    // The password may be sent TO the server (register / login) but is never written into a response.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Password is required")
    @Pattern(regexp = ValidationPatterns.PASSWORD,
            message = "Password must be 8-64 characters with an upper-case letter, a lower-case letter, a number and a special character, and no spaces")
    private String password;
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = ValidationPatterns.MOBILE, message = "Mobile number must be 10 digits")
    private String mobileNumber;
    private String userRole;

    public UserDTO() {
    }

    public UserDTO(Long userId, String email, String password, String username, String mobileNumber, String userRole) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.username = username;
        this.mobileNumber = mobileNumber;
        this.userRole = userRole;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
