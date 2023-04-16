package com.ldiolaiuti.telegram.api.bot.services;

import org.springframework.stereotype.Service;

import javax.validation.*;
import java.util.Set;

/**
 * This class acts as a standard Java bean validation service
 */
@Service
public class BeanValidationService {

    private final Validator validator;

    public BeanValidationService() {
        try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    public void validate(Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> violations = this.validator.validate(object, groups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
