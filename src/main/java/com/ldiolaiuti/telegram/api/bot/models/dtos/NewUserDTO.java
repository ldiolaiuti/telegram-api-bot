package com.ldiolaiuti.telegram.api.bot.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String password2;

}
