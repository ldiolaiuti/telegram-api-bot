package com.ldiolaiuti.telegram.api.bot.repositories;

import com.ldiolaiuti.telegram.api.bot.models.dtos.TelegramParams;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "telegramRepository", url = "${telegram.bot.url}")
public interface TelegramRepository {

    @PostMapping(path = "/sendPhoto", consumes = "multipart/form-data")
    String sendMessage(@RequestPart("data") TelegramParams telegramParams, @RequestPart("photo") MultipartFile image);
}
