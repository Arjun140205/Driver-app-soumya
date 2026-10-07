package com.examly.springapp.controller;

import com.examly.springapp.config.JwtUtils;
import com.examly.springapp.config.UserPrinciple;
import com.examly.springapp.dto.LoginRequestDTO;
import com.examly.springapp.dto.UserDTO;
import com.examly.springapp.model.LoginDTO;
import com.examly.springapp.model.Roles;
import com.examly.springapp.model.User;
import com.examly.springapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Public sign-up: customers only. The body is validated (@Valid), the e-mail and mobile number
     * must have been verified by OTP, and an "Admin" role in the body is refused.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO user) {
        if (user.getUserRole() != null && Roles.ADMIN.equalsIgnoreCase(user.getUserRole().trim())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Admin accounts cannot be created through sign-up."));
        }
        UserDTO createdUser = userService.createUser(user);
        if (createdUser == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("A user with this email already exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Used by the Angular app every time it starts: answers 200 with who the token belongs to when the
     * token is still accepted by this server (valid signature, not expired, the user still exists), and
     * 401 otherwise. The role is taken from the database, not from what the browser stored.
     */
    @GetMapping("/auth/validate")
    public ResponseEntity<?> validateSession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrinciple)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = ((UserPrinciple) authentication.getPrincipal()).getUser();
        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "userRole", user.getUserRole(),
                "userId", user.getUserId()));
    }

    /** Returns only the profile belonging to the currently authenticated JWT principal. */
    @GetMapping("/users/me")
    public ResponseEntity<UserDTO> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrinciple)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = ((UserPrinciple) authentication.getPrincipal()).getUser();
        UserDTO profile = new UserDTO();
        profile.setUserId(user.getUserId());
        profile.setEmail(user.getEmail());
        profile.setUsername(user.getUsername());
        profile.setMobileNumber(user.getMobileNumber());
        profile.setUserRole(user.getUserRole());
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserPrinciple userDetails = (UserPrinciple) authentication.getPrincipal();
            User user = userDetails.getUser();

            LoginDTO loginDTO = new LoginDTO(jwt, user.getUsername(), user.getUserRole(), user.getUserId());
            // Tests expect HTTP 200 OK for login
            return ResponseEntity.status(HttpStatus.OK).body(loginDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
