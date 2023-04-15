package com.ldiolaiuti.telegram.api.bot.mappers;

import com.ldiolaiuti.telegram.api.bot.models.User;
import com.ldiolaiuti.telegram.api.bot.models.dtos.NewUserDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldConvertDtoToEntity() {
        var dto = NewUserDTO.builder()
                .username("TestUser")
                .password("T3stP@ass")
                .password2("Another Pass")
                .build();

        User entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getUsername()).isEqualTo(dto.getUsername());
        assertThat(entity.getPassword()).isEqualTo(dto.getPassword());
    }

}