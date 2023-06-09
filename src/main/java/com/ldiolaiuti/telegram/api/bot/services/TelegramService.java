package com.ldiolaiuti.telegram.api.bot.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ldiolaiuti.telegram.api.bot.exceptions.InvalidTokenException;
import com.ldiolaiuti.telegram.api.bot.exceptions.TelegramException;
import com.ldiolaiuti.telegram.api.bot.models.dtos.TelegramParams;
import com.ldiolaiuti.telegram.api.bot.repositories.TelegramRepository;
import com.ldiolaiuti.telegram.api.bot.utils.JwtUtils;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class TelegramService {

    private final String chatId;

    private final TelegramRepository telegramRepository;

    private final ObjectMapper objectMapper;

    public TelegramService(@Value("${telegram.bot.chatId}") String chatId,
                           TelegramRepository telegramRepository) {
        this.chatId = chatId;
        this.telegramRepository = telegramRepository;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Send an message containing an image and text to a Telegram bot
     * @param token Access token
     * @param image Image file to be sent
     * @param text Text message to be sent
     */
    public void sendMessage(String token, MultipartFile image, String text) {
        if (!JwtUtils.isValid(token.replace("Bearer ", ""))) {
            log.error("Provided token is not valid");
            throw new InvalidTokenException("Provided token is not valid");
        }

        try {
            log.info("Sending message to telegram bot");

            String response = telegramRepository.sendMessage(
                    TelegramParams.builder()
                            .chat_id(chatId)
                            .caption(text)
                            .parse_mode("markdown")
                            .build(),
                    image);

            if (!objectMapper.readTree(response).get("ok").asBoolean()) {
                log.error("An error occured: " + response);
                throw new TelegramException(response);
            }

            log.info("Message sent!");
        } catch (FeignException | JsonProcessingException e) {
            log.error("An error occured: " + e.getMessage());
            throw new TelegramException(e.getMessage());
        }
    }
}
