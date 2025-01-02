package com.jmarqb.ms.auth.app.validation;

import com.jmarqb.ms.auth.app.services.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ExistEmailValidation implements ConstraintValidator<ExistEmail, String> {

    private final UserService userService;

    public ExistEmailValidation(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(userService == null) {
            return true;
        }
        return !userService.existsByEmail(value);
    }
}
