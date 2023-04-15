package com.ldiolaiuti.telegram.api.bot.services;

import com.ldiolaiuti.telegram.api.bot.exceptions.InvalidTokenException;
import com.ldiolaiuti.telegram.api.bot.models.dtos.TelegramParams;
import com.ldiolaiuti.telegram.api.bot.repositories.TelegramRepository;
import com.ldiolaiuti.telegram.api.bot.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TelegramService {

    private final String chatId;

    private final TelegramRepository telegramRepository;

    public TelegramService(@Value("${telegram.bot.chatId}") String chatId,
                           TelegramRepository telegramRepository) {
        this.chatId = chatId;
        this.telegramRepository = telegramRepository;
    }

    public void sendMessage(String token) {
        if (!JwtUtils.isValid(token.replace("Bearer ", ""))) {
            throw new InvalidTokenException("Provided token is not valid");
        }

        String s = telegramRepository.sendMessage(
                TelegramParams.builder()
                        .chat_id(chatId)
                        .text("Test message")
                        .build());
        log.info("Sent message: " + s);
    }
}
