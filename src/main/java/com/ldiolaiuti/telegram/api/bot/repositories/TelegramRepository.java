package com.ldiolaiuti.telegram.api.bot.repositories;

import com.ldiolaiuti.telegram.api.bot.models.dtos.TelegramParams;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "telegramRepository", url = "${telegram.bot.url}")
public interface TelegramRepository {

    @GetMapping(path = "/sendMessage")
    String sendMessage(@SpringQueryMap TelegramParams telegramParams);
}
