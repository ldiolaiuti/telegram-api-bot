package com.ldiolaiuti.telegram.api.bot.services;

import com.google.common.base.Preconditions;
import com.ldiolaiuti.telegram.api.bot.mappers.UserMapper;
import com.ldiolaiuti.telegram.api.bot.models.User;
import com.ldiolaiuti.telegram.api.bot.models.dtos.NewUserDTO;
import com.ldiolaiuti.telegram.api.bot.repositories.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Created on 15/04/23 by ldiolaiuti
 * <p>
 * Class to provide Authorization signup and signin services
 */
@Service
public class AuthService {

    private final static String PASSWORD_REGEX =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*#?&])([a-zA-Z0-9@$!%*#?&]+)$";

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final BeanValidationService beanValidationService;

    public AuthService(UserRepository userRepository,
                       UserMapper userMapper,
                       BeanValidationService beanValidationService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.beanValidationService = beanValidationService;
    }

    /**
     * Signup API. This API register a new user with the credentials provided.
     * Some validation checks are performed:
     * <ul>
     *     <li>Username, password and confirmation password must be provided</li>
     *     <li>Password and confirmation password must match</li>
     *     <li>The minimum length allowed for the password is 6 characters</li>
     *     <li>Password must contain at least one uppercase letter, one lowercase letter, one number and one special character (@$!%*#?&)</li>
     *     <li>Username must be unique</li>
     * </ul>
     * @param dto
     * @return created entity
     */
    public User signup(NewUserDTO dto) {
        validateInput(dto);

        return userRepository.save(userMapper.toEntity(dto));
    }

    private void validateInput(NewUserDTO dto) {
        beanValidationService.validate(dto);

        Preconditions.checkArgument(dto.getPassword().equals(dto.getPassword2()),
                "The entered passwords do not match");
        Preconditions.checkArgument(dto.getPassword().length() >= 6,
                "The minimum length allowed for the password is 6 characters");
        Preconditions.checkArgument(dto.getPassword().matches(PASSWORD_REGEX),
                "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character (@$!%*#?&)");
        Preconditions.checkArgument(!userRepository.existsByUsernameIgnoreCase(dto.getUsername()),
                "User " + dto.getUsername() + " already exists");
    }
}
