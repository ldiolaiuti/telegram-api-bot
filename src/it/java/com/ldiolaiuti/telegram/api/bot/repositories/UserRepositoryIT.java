package com.ldiolaiuti.telegram.api.bot.repositories;

import com.ldiolaiuti.telegram.api.bot.utils.DbSetupExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("it")
@ExtendWith(DbSetupExtension.class)
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldExistsByUsername() {
        boolean exists = userRepository.existsByUsernameIgnoreCase("TestUser");
        assertThat(exists).isTrue();
    }

    @Test
    void shouldExistsByUsernameIgnoreCase() {
        boolean exists = userRepository.existsByUsernameIgnoreCase("testuser");
        assertThat(exists).isTrue();
    }

    @Test
    void shouldNotExistsByUsername() {
        boolean exists = userRepository.existsByUsernameIgnoreCase("NewUser");
        assertThat(exists).isFalse();
    }

    @Test
    void shouldExistsByUsernameAndPassword() {
        boolean exists = userRepository.existsByUsernameAndPassword("TestUser", "T3stP@ass");
        assertThat(exists).isTrue();
    }

    @Test
    void shouldNotExistsByUsernameAndPasswordDueToUsernameDoesNotMatch() {
        boolean exists = userRepository.existsByUsernameAndPassword("InvalidUser", "T3stP@ass");
        assertThat(exists).isFalse();
    }

    @Test
    void shouldNotExistsByUsernameAndPasswordDueToPasswordDoesNotMatch() {
        boolean exists = userRepository.existsByUsernameAndPassword("TestUser", "InvalidPass");
        assertThat(exists).isFalse();
    }

}