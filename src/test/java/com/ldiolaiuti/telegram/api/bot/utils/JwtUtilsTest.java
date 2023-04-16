package com.ldiolaiuti.telegram.api.bot.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {


    @Test
    void shouldGenerateAndParseJwt() {
        String jwt = JwtUtils.generateToken("TestUser");

        assertThat(jwt).isNotNull();

        Jws<Claims> claimsJws = JwtUtils.parseToken(jwt);

        Instant now = Instant.now();
        assertThat(claimsJws.getBody().get("name")).isEqualTo("TestUser");
        assertThat(claimsJws.getBody().getIssuedAt()).isBefore(Date.from(now));
        assertThat(claimsJws.getBody().getExpiration()).isAfter(Date.from(now));
    }

}