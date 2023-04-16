package com.ldiolaiuti.telegram.api.bot.services;

import com.ldiolaiuti.telegram.api.bot.exceptions.InvalidTokenException;
import com.ldiolaiuti.telegram.api.bot.exceptions.TelegramException;
import com.ldiolaiuti.telegram.api.bot.models.dtos.TelegramParams;
import com.ldiolaiuti.telegram.api.bot.repositories.TelegramRepository;
import com.ldiolaiuti.telegram.api.bot.utils.JwtUtils;
import feign.FeignException;
import feign.Request;
import feign.Response;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("it")
class TelegramServiceIT {

    @Autowired
    private TelegramService telegramService;

    @MockBean
    private TelegramRepository telegramRepository;

    @SneakyThrows
    @Test
    void shouldSendMessage() {
        when(telegramRepository.sendMessage(any(), any()))
                .thenReturn("{\"ok\": true}");
        String token = JwtUtils.generateToken("TestUser");

        telegramService.sendMessage(token, null, "message");

        Mockito.verify(telegramRepository, times(1)).sendMessage(any(), any());
    }

    @SneakyThrows
    @Test
    void shouldNotSendMessageDueToInvalidToken() {

        String exceptionMessage = assertThrows(
                InvalidTokenException.class, () ->
                        telegramService.sendMessage("invalid_token", null, "message")
        ).getMessage();

        assertThat(exceptionMessage).isEqualTo("Provided token is not valid");
        Mockito.verifyNoInteractions(telegramRepository);
    }

    @SneakyThrows
    @Test
    void shouldNotSendMessageDueToInvalidResponse() {
        String token = JwtUtils.generateToken("TestUser");

        when(telegramRepository.sendMessage(any(), any()))
                .thenReturn("{\"ok\": false}");

        String exceptionMessage = assertThrows(
                TelegramException.class, () ->
                        telegramService.sendMessage(token, null, "message")
        ).getMessage();

        assertThat(exceptionMessage).isEqualTo("{\"ok\": false}");
        Mockito.verify(telegramRepository, times(1)).sendMessage(any(), any());
    }

    @SneakyThrows
    @Test
    void shouldNotSendMessageDueToFeignException() {
        Request request = Request.create("POST", "test", Collections.emptyMap(), null, null);
        String token = JwtUtils.generateToken("TestUser");

        doThrow(new FeignException.BadRequest("A Feign Exception occurred", request, null, Collections.emptyMap()))
                .when(telegramRepository).sendMessage(any(), any());

        String exceptionMessage = assertThrows(
                TelegramException.class, () ->
                        telegramService.sendMessage(token, null, "message")
        ).getMessage();

        assertThat(exceptionMessage).isEqualTo("A Feign Exception occurred");
        Mockito.verify(telegramRepository, times(1)).sendMessage(any(), any());
    }

}