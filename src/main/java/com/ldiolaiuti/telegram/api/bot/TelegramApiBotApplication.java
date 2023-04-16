package com.ldiolaiuti.telegram.api.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class TelegramApiBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramApiBotApplication.class, args);
	}

}
