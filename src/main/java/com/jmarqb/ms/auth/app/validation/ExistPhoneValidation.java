package com.jmarqb.ms.auth.app.validation;

import com.jmarqb.ms.auth.app.services.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ExistPhoneValidation implements ConstraintValidator<ExistPhone, String> {

    private final UserService userService;

    public ExistPhoneValidation(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(userService == null) {
            return true;
        }
        return !userService.existsByPhone(value);
    }
}
