package com.examly.springapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // switches on @PreAuthorize (used by AdminController)
public class SecurityConfig {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    public JwtAuthenticationFilter authenticationJwtTokenFilter() {
        return new JwtAuthenticationFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configure(http))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(unauthorizedHandler)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .authorizeHttpRequests(auth -> auth
                // Public
                .requestMatchers("/api/register", "/api/login").permitAll()
                .requestMatchers("/api/otp/**").permitAll()          // sign-up OTP verification
                .requestMatchers("/api/admin/**").hasRole("ADMIN")   // e.g. adding another admin
                .requestMatchers(HttpMethod.POST, "/api/ai/driver-search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/ai/driver/**").hasAnyRole("ADMIN", "CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/api/ai/feedback/analyze-existing").hasRole("ADMIN")
                .requestMatchers("/h2-console/**").permitAll()

                // ======= Driver =======
                .requestMatchers(HttpMethod.GET, "/api/driver").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/driver").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/driver/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/driver/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/driver/**").hasRole("ADMIN")

                // ======= DriverRequest =======
                .requestMatchers(HttpMethod.POST, "/api/driverRequest").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/api/driverRequest").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/driverRequest/user/**").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/api/driverRequest/driver/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/driverRequest/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/driverRequest/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/driverRequest/**").hasRole("CUSTOMER")

                // ======= Feedback =======
                .requestMatchers(HttpMethod.POST, "/api/feedback").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/api/feedback").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/feedback/user/**").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/api/feedback/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/feedback/**").hasRole("CUSTOMER")

                .anyRequest().authenticated()
            );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }
}
