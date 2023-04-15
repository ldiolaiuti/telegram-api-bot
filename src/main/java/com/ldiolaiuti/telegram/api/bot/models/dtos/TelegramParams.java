package com.ldiolaiuti.telegram.api.bot.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TelegramParams {

    private String chat_id;

    private String text;
}
