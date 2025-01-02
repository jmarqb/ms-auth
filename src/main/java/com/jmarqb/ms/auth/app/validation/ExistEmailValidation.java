package com.jmarqb.ms.auth.app.validation;

import com.jmarqb.ms.auth.app.services.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExistEmailValidation implements ConstraintValidator<ExistEmail, String> {

    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(userService == null) {
            return true;
        }
        return !userService.existsByEmail(value);
    }
}
