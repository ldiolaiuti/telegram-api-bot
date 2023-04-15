package com.ldiolaiuti.telegram.api.bot.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * Utility class to manage JWT
 */
@UtilityClass
@Slf4j
public class JwtUtils {

    /**
     * Key is hardcoded here for simplicity
     */
    private final static Key hmacKey =
            new SecretKeySpec(
                    Base64.getDecoder()
                            .decode("asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4"),
                    SignatureAlgorithm.HS256.getJcaName());

    /**
     * Generate a sample JWT
     * @param username username, used as claim
     * @return JWT in String format
     */
    public String generateToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claim("name", username)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(5, ChronoUnit.MINUTES)))
                .signWith(hmacKey)
                .compact();
    }

    /**
     * Parse a JWT
     * @param jwt - String format
     * @return List of claims
     */
    public Jws<Claims> parseToken(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(jwt);
    }

}
