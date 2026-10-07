package com.examly.springapp.config;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    /**
     * Secret that signs the login tokens (jwt.secret / JWT_SECRET). When it is empty a new random
     * secret is generated every time the server starts, so every token issued before the restart
     * stops working and everybody has to log in again.
     */
    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms:86400000}")
    private int jwtExpirationMs;

    @PostConstruct
    void initSecret() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            byte[] key = new byte[64];
            new SecureRandom().nextBytes(key);
            jwtSecret = Base64.getEncoder().encodeToString(key);
            log.info("No jwt.secret configured: generated a random one. All logins end when the server restarts.");
        }
    }

    public String generateJwtToken(Authentication authentication) {

        UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
        } catch (MalformedJwtException e) {
        } catch (ExpiredJwtException e) {
        } catch (UnsupportedJwtException e) {
        } catch (IllegalArgumentException e) {
        }

        return false;
    }
}
