package com.examly.springapp.config;

import com.examly.springapp.dto.UserDTO;
import com.examly.springapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Creates the predefined admin when the application starts (if no user with that e-mail exists).
 * Sign-up can only create customers, so this is how the very first admin comes to exist; that admin
 * can then add more admins from the "Add Admin" page. The values come from app.admin.* in
 * application.properties (or the ADMIN_EMAIL / ADMIN_PASSWORD ... environment variables).
 */
@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    @Autowired
    private UserService userService;

    @Value("${app.admin.username:admin}")
    private String username;

    @Value("${app.admin.email:admin@driveu.com}")
    private String email;

    @Value("${app.admin.mobile:9999999999}")
    private String mobile;

    @Value("${app.admin.password:Admin@123}")
    private String password;

    @Override
    public void run(String... args) {
        UserDTO admin = new UserDTO();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setMobileNumber(mobile);
        admin.setPassword(password);

        if (userService.createAdmin(admin) != null) {
            log.info("Predefined admin created: {}", email);
        } else {
            log.info("Predefined admin {} already exists", email);
        }
    }
}
