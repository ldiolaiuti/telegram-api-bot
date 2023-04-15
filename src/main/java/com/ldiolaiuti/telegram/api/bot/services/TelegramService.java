package com.ldiolaiuti.telegram.api.bot.services;

import com.ldiolaiuti.telegram.api.bot.models.dtos.TelegramParams;
import com.ldiolaiuti.telegram.api.bot.repositories.TelegramRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TelegramService {


    private final static String TELEGRAM_BOT_USERNAME = "test_ldiolaiuti_bot";
    private final static String TELEGRAM_BOT_TOKEN = "6005519215:AAG9frXRs4O15AE-8HglpuUqwbL-8T7FOd4";

    private final static String CHAT_ID = "237896500";

    private final TelegramRepository telegramRepository;

    public TelegramService(TelegramRepository telegramRepository) {
        this.telegramRepository = telegramRepository;
    }

    public void sendMessage() {
        String s = telegramRepository.sendMessage(
                TelegramParams.builder()
                        .chat_id(CHAT_ID)
                        .text("Test message")
                        .build());
        log.info("Sent message: " + s);
    }
}
