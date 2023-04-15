package com.ldiolaiuti.telegram.api.bot.services;

import com.ldiolaiuti.telegram.api.bot.models.User;
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
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("it")
@ExtendWith(DbSetupExtension.class)
class AuthServiceIT {

    @Autowired
    private AuthService authService;

    @Test
    void shouldSignupUser() {
        NewUserDTO dto = NewUserDTO.builder()
                .username("ldiolaiuti")
                .password("myP@ssw0rd")
                .password2("myP@ssw0rd")
                .build();

        User user = authService.signup(dto);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isNotNull().isPositive();
        assertThat(user.getUsername()).isEqualTo(dto.getUsername());
        assertThat(user.getPassword()).isEqualTo(dto.getPassword());
    }

    @Test
    void shouldNotSignupUserDueToBeanValidationException() {
        String exceptionMessage = assertThrows(
                ConstraintViolationException.class, () ->
                        authService.signup(NewUserDTO.builder().build())
        ).getMessage();

        assertThat(exceptionMessage)
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

        String exceptionMessage = assertThrows(
                IllegalArgumentException.class, () ->
                        authService.signup(dto)
        ).getMessage();

        assertThat(exceptionMessage).isEqualTo("The entered passwords do not match");
    }

    @Test
    void shouldNotSignupUserDueToPasswordTooShort() {
        NewUserDTO dto = NewUserDTO.builder()
                .username("ldiolaiuti")
                .password("Abc1!")
                .password2("Abc1!")
                .build();

        String exceptionMessage = assertThrows(
                IllegalArgumentException.class, () ->
                        authService.signup(dto)
        ).getMessage();

        assertThat(exceptionMessage).isEqualTo("The minimum length allowed for the password is 6 characters");
    }

    @Test
    void shouldNotSignupUserDueToPasswordDoesNotContainsUppercaseLetter() {
        NewUserDTO dtoMissingUppercaseLetter = NewUserDTO.builder()
                .username("ldiolaiuti")
                .password("myp@ssw0rd")
                .password2("myp@ssw0rd")
                .build();

        String exceptionMessage = assertThrows(
                IllegalArgumentException.class, () ->
                        authService.signup(dtoMissingUppercaseLetter)
        ).getMessage();

        assertThat(exceptionMessage).isEqualTo("Password must contain at least one uppercase letter, one lowercase letter, one number and one special character (@$!%*#?&)");
    }

    @Test
    void shouldNotSignupUserDueToPasswordDoesNotContainsLowercaseLetter() {
        NewUserDTO dtoMissingLowercaseLetter = NewUserDTO.builder()
                .username("ldiolaiuti")
                .password("MYP@SSW0RD")
                .password2("MYP@SSW0RD")
                .build();

        String exceptionMessage = assertThrows(
                IllegalArgumentException.class, () ->
                        authService.signup(dtoMissingLowercaseLetter)
        ).getMessage();

        assertThat(exceptionMessage).isEqualTo("Password must contain at least one uppercase letter, one lowercase letter, one number and one special character (@$!%*#?&)");
    }

    @Test
    void shouldNotSignupUserDueToPasswordDoesNotContainsNumber() {
        NewUserDTO dtoMissingNumber = NewUserDTO.builder()
                .username("ldiolaiuti")
                .password("myP@ssword")
                .password2("myP@ssword")
                .build();

        String exceptionMessage = assertThrows(
                IllegalArgumentException.class, () ->
                        authService.signup(dtoMissingNumber)
        ).getMessage();

        assertThat(exceptionMessage).isEqualTo("Password must contain at least one uppercase letter, one lowercase letter, one number and one special character (@$!%*#?&)");
    }

    @Test
    void shouldNotSignupUserDueToPasswordDoesNotContainsSpecialCharacter() {
        NewUserDTO dtoMissingSpecialChar = NewUserDTO.builder()
                .username("ldiolaiuti")
                .password("myPassw0rd")
                .password2("myPassw0rd")
                .build();

        String exceptionMessage = assertThrows(
                IllegalArgumentException.class, () ->
                        authService.signup(dtoMissingSpecialChar)
        ).getMessage();

        assertThat(exceptionMessage).isEqualTo("Password must contain at least one uppercase letter, one lowercase letter, one number and one special character (@$!%*#?&)");
    }

    @Test
    void shouldNotSignupUserDueToUserAlreadyExists() {
        NewUserDTO dtoMissingSpecialChar = NewUserDTO.builder()
                .username("TestUser")
                .password("myP@ssw0rd")
                .password2("myP@ssw0rd")
                .build();

        String exceptionMessage = assertThrows(
                IllegalArgumentException.class, () ->
                        authService.signup(dtoMissingSpecialChar)
        ).getMessage();

        assertThat(exceptionMessage).isEqualTo("User TestUser already exists");
    }

    @Test
    void shouldSignIn() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("TestUser")
                .password("T3stP@ass")
                .build();

        LoginResponse loginResponse = authService.signin(loginRequest);

        Jws<Claims> claimsJws = JwtUtils.parseToken(loginResponse.getToken());

        Instant now = Instant.now();
        assertThat(claimsJws.getBody().get("name")).isEqualTo("TestUser");
        assertThat(claimsJws.getBody().getIssuedAt()).isBefore(Date.from(now));
        assertThat(claimsJws.getBody().getExpiration()).isAfter(Date.from(now));

    }

    @Test
    void shouldNotSigninUserDueToBeanValidationException() {
        String exceptionMessage = assertThrows(
                ConstraintViolationException.class, () ->
                        authService.signin(LoginRequest.builder().build())
        ).getMessage();

        assertThat(exceptionMessage)
                .contains("username: must not be blank")
                .contains("password: must not be blank");
    }

    @Test
    void shouldNotSigninUserDueToUserDoesNotExist() {
        LoginRequest request = LoginRequest.builder()
                .username("ldiolaiuti")
                .password("T3stP@ass")
                .build();

        String exceptionMessage = assertThrows(
                IllegalArgumentException.class, () ->
                        authService.signin(request)
        ).getMessage();

        assertThat(exceptionMessage).isEqualTo("Cannot login with provided credentials");
    }

}