package com.ldiolaiuti.telegram.api.bot.controllers;

import com.ldiolaiuti.telegram.api.bot.exceptions.InvalidTokenException;
import com.ldiolaiuti.telegram.api.bot.models.dtos.NewUserDTO;
import com.ldiolaiuti.telegram.api.bot.services.TelegramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolationException;

@RestController
@RequestMapping(path = "/telegram",
        produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Tag(name = "Telegram API",
        description = "Provides all to comunicate with a Telegram bot")
public class TelegramController {

    private final TelegramService telegramService;

    public TelegramController(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @PostMapping("/send_message")
    @Operation(summary = "Send a message to a Telegram bot")
    public ResponseEntity<String> sendMessage(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                              @RequestPart MultipartFile image,
                                              @RequestPart String text) {
        try {
            telegramService.sendMessage(token, image, text);
            return new ResponseEntity<>("Message successfully sent", HttpStatus.OK);
        } catch (InvalidTokenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
