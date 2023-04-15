package com.ldiolaiuti.telegram.api.bot.mappers;

import com.ldiolaiuti.telegram.api.bot.models.User;
import com.ldiolaiuti.telegram.api.bot.models.dtos.NewUserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(NewUserDTO newUserDTO);

}
