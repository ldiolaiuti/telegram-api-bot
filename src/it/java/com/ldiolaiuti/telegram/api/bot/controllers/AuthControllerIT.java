package com.ldiolaiuti.telegram.api.bot.controllers;

import com.ldiolaiuti.telegram.api.bot.models.dtos.LoginRequest;
import com.ldiolaiuti.telegram.api.bot.models.dtos.LoginResponse;
import com.ldiolaiuti.telegram.api.bot.models.dtos.NewUserDTO;
import com.ldiolaiuti.telegram.api.bot.utils.DbSetupExtension;
import com.ldiolaiuti.telegram.api.bot.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
@ExtendWith(DbSetupExtension.class)
class AuthControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void shouldSignupUser() {
        NewUserDTO dto = NewUserDTO.builder()
                .username("ldiolaiuti")
                .password("myP@ssw0rd")
                .password2("myP@ssw0rd")
                .build();

        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signup", dto, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("User successfully created.");
    }

    @Test
    void shouldNotSignupUserDueToBeanValidationException() {
        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signup", NewUserDTO.builder().build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody())
                .contains("username: must not be blank")
                .contains("password: must not be blank")
                .contains("password2: must not be blank");
    }

    @Test
    void shouldNotSignupUserDueToPasswordDoesNotMatch() {
        NewUserDTO dto = NewUserDTO.builder()
                .username("ldiolaiuti")
                .password("myP@ssw0rd")
                .password2("myP@ssw0rd2")
                .build();

        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signup", dto, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("The entered passwords do not match");
    }

    @Test
    void shouldNotSignupUserDueToPasswordTooShort() {
        NewUserDTO dto = NewUserDTO.builder()
                .username("ldiolaiuti")
                .password("Abc1!")
                .password2("Abc1!")
                .build();

        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signup", dto, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("The minimum length allowed for the password is 6 characters");
    }

    @Test
    void shouldNotSignupUserDueToPasswordDoesNotContainsUppercaseLetter() {
        NewUserDTO dtoMissingUppercaseLetter = NewUserDTO.builder()
                .username("ldiolaiuti")
                .password("myp@ssw0rd")
                .password2("myp@ssw0rd")
                .build();

        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signup", dtoMissingUppercaseLetter, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("Password must contain at least one uppercase letter, one lowercase letter, one number and one special character (@$!%*#?&)");
    }

    @Test
    void shouldNotSignupUserDueToPasswordDoesNotContainsLowercaseLetter() {
        NewUserDTO dtoMissingLowercaseLetter = NewUserDTO.builder()
                .username("ldiolaiuti")
                .password("MYP@SSW0RD")
                .password2("MYP@SSW0RD")
                .build();

        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signup", dtoMissingLowercaseLetter, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("Password must contain at least one uppercase letter, one lowercase letter, one number and one special character (@$!%*#?&)");
    }

    @Test
    void shouldNotSignupUserDueToPasswordDoesNotContainsNumber() {
        NewUserDTO dtoMissingNumber = NewUserDTO.builder()
                .username("ldiolaiuti")
                .password("myP@ssword")
                .password2("myP@ssword")
                .build();

        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signup", dtoMissingNumber, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("Password must contain at least one uppercase letter, one lowercase letter, one number and one special character (@$!%*#?&)");
    }

    @Test
    void shouldNotSignupUserDueToPasswordDoesNotContainsSpecialCharacter() {
        NewUserDTO dtoMissingSpecialChar = NewUserDTO.builder()
                .username("ldiolaiuti")
                .password("myPassw0rd")
                .password2("myPassw0rd")
                .build();

        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signup", dtoMissingSpecialChar, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("Password must contain at least one uppercase letter, one lowercase letter, one number and one special character (@$!%*#?&)");
    }

    @Test
    void shouldNotSignupUserDueToUserAlreadyExists() {
        NewUserDTO dtoMissingSpecialChar = NewUserDTO.builder()
                .username("TestUser")
                .password("myP@ssw0rd")
                .password2("myP@ssw0rd")
                .build();

        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signup", dtoMissingSpecialChar, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("User TestUser already exists");
    }

    @Test
    void shouldSignIn() {
        LoginRequest request = LoginRequest.builder()
                .username("TestUser")
                .password("T3stP@ass")
                .build();

        ResponseEntity<LoginResponse> response = testRestTemplate.postForEntity("/auth/signin", request, LoginResponse.class);

        Jws<Claims> claimsJws = JwtUtils.parseToken(response.getBody().getToken());

        Instant now = Instant.now();
        assertThat(claimsJws.getBody().get("name")).isEqualTo("TestUser");
        assertThat(claimsJws.getBody().getIssuedAt()).isBefore(Date.from(now));
        assertThat(claimsJws.getBody().getExpiration()).isAfter(Date.from(now));

    }

    @Test
    void shouldNotSigninUserDueToBeanValidationException() {
        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signin", LoginRequest.builder().build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody())
                .contains("username: must not be blank")
                .contains("password: must not be blank");
    }

    @Test
    void shouldNotSigninUserDueToUserDoesNotExist() {
        LoginRequest request = LoginRequest.builder()
                .username("ldiolaiuti")
                .password("T3stP@ass")
                .build();

        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signin", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("Cannot login with provided credentials");
    }
}