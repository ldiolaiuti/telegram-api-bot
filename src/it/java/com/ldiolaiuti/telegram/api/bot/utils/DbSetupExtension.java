package com.ldiolaiuti.telegram.api.bot.utils;

import com.ldiolaiuti.telegram.api.bot.models.User;
import com.ldiolaiuti.telegram.api.bot.repositories.UserRepository;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class DbSetupExtension implements BeforeEachCallback, AfterEachCallback, BeforeAllCallback {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void beforeAll(ExtensionContext context) {
        Locale.setDefault(Locale.ENGLISH);
        injectSpringBeans(context);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        userRepository.saveAll(getTestUsers());
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        userRepository.deleteAll();
    }

    private List<User> getTestUsers() {
        return List.of(
                User.builder()
                        .username("TestUser")
                        .password("T3stP@ass")
                        .build()
        );
    }

    /**
     * Should inject manually with reflection because in a JUnit Extension @{@link Autowired} is not honored
     *
     * @param context junit context
     */
    private void injectSpringBeans(ExtensionContext context) {
        ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
        Stream.of(this.getClass().getDeclaredFields())
                .filter(field -> field.getAnnotation(Autowired.class) != null)
                .forEach(field -> {
                    try {
                        Object bean = applicationContext.getBean(field.getType());
                        field.setAccessible(true);
                        field.set(this, bean);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
