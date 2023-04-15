package com.ldiolaiuti.telegram.api.bot.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("it")
class TelegramServiceIT {

    @Autowired
    private TelegramService telegramService;

    @Test
    void shouldSendMessage() {
        telegramService.sendMessage("token", null, "message");
    }

}