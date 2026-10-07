package com.examly.springapp.controller;

import com.examly.springapp.dto.UserDTO;
import com.examly.springapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin-only operations. Two layers protect it: SecurityConfig only lets /api/admin/** through for
 * the ADMIN role, and @PreAuthorize checks the role again right on the controller.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /** Lets a logged-in admin create another admin. */
    @PostMapping("/register")
    public ResponseEntity<?> addAdmin(@Valid @RequestBody UserDTO admin) {
        UserDTO created = userService.createAdmin(admin);
        if (created == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("A user with this email already exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
