package com.ldiolaiuti.telegram.api.bot.controllers;

import com.ldiolaiuti.telegram.api.bot.exceptions.InvalidTokenException;
import com.ldiolaiuti.telegram.api.bot.exceptions.TelegramException;
import com.ldiolaiuti.telegram.api.bot.repositories.TelegramRepository;
import com.ldiolaiuti.telegram.api.bot.services.TelegramService;
import com.ldiolaiuti.telegram.api.bot.utils.JwtUtils;
import feign.FeignException;
import feign.Request;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
class TelegramControllerIT {

    @MockBean
    private TelegramRepository telegramRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @SneakyThrows
    @Test
    void shouldSendMessage() {
        when(telegramRepository.sendMessage(any(), any()))
                .thenReturn("{\"ok\": true}");
        String token = JwtUtils.generateToken("TestUser");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("text", "message");
        body.add("image", new FileSystemResource("src/test/resources/test-image.jpg"));

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/telegram/send_message",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody()).isEqualTo("Message successfully sent");

        Mockito.verify(telegramRepository, times(1)).sendMessage(any(), any());
    }

    @SneakyThrows
    @Test
    void shouldNotSendMessageDueToInvalidToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer invalid_token");

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("text", "message");
        body.add("image", new FileSystemResource("src/test/resources/test-image.jpg"));

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/telegram/send_message",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody()).isEqualTo("Provided token is not valid");

        Mockito.verifyNoInteractions(telegramRepository);
    }

    @SneakyThrows
    @Test
    void shouldNotSendMessageDueToInvalidResponse() {
        String token = JwtUtils.generateToken("TestUser");

        when(telegramRepository.sendMessage(any(), any()))
                .thenReturn("{\"ok\": false}");


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + token);

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("text", "message");
        body.add("image", new FileSystemResource("src/test/resources/test-image.jpg"));

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/telegram/send_message",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody()).isEqualTo("An error occured while contacting Telegram.");

        Mockito.verify(telegramRepository, times(1)).sendMessage(any(), any());
    }

    @SneakyThrows
    @Test
    void shouldNotSendMessageDueToFeignException() {
        Request request = Request.create("POST", "test", Collections.emptyMap(), null, null);
        String token = JwtUtils.generateToken("TestUser");

        doThrow(new FeignException.BadRequest("A Feign Exception occurred", request, null, Collections.emptyMap()))
                .when(telegramRepository).sendMessage(any(), any());


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + token);

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("text", "message");
        body.add("image", new FileSystemResource("src/test/resources/test-image.jpg"));

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/telegram/send_message",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody()).isEqualTo("An error occured while contacting Telegram.");

        Mockito.verify(telegramRepository, times(1)).sendMessage(any(), any());
    }
}