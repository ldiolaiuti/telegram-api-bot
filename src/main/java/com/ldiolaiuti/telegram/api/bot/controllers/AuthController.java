package com.ldiolaiuti.telegram.api.bot.controllers;

import com.ldiolaiuti.telegram.api.bot.models.dtos.LoginRequest;
import com.ldiolaiuti.telegram.api.bot.models.dtos.NewUserDTO;
import com.ldiolaiuti.telegram.api.bot.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolationException;

@RestController
@RequestMapping(path = "/auth",
        produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Tag(name = "Authorization API",
        description = "Provides all APIs for Authorization services")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Signup a new user")
    public ResponseEntity<String> signup(@RequestBody NewUserDTO newUserDTO) {
        try {
            authService.signup(newUserDTO);
            return new ResponseEntity<>("User successfully created.", HttpStatus.OK);
        } catch (ConstraintViolationException | IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signin")
    @Operation(summary = "Signin as an existing user")
    public ResponseEntity<Object> signin(@RequestBody LoginRequest loginRequest) {
        try {
            return new ResponseEntity<>(authService.signin(loginRequest), HttpStatus.OK);
        } catch (ConstraintViolationException | IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
