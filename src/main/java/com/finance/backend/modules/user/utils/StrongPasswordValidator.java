package com.finance.backend.modules.user.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator
        implements ConstraintValidator<StrongPassword, String> {

    @Override
    public boolean isValid(
            String password,
            ConstraintValidatorContext context) {

        if (password == null) {
            return false;
        }

        boolean upper = false;
        boolean lower = false;
        boolean digit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                upper = true;
            } else if (Character.isLowerCase(c)) {
                lower = true;
            } else if (Character.isDigit(c)) {
                digit = true;
            }
        }

        if (password.length() < 8) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "La contraseña debe tener al menos 8 caracteres").addConstraintViolation();

            return false;
        }

        if (!upper) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "La contraseña debe contener al menos una letra mayúscula").addConstraintViolation();

            return false;
        }

        if (!lower) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "La contraseña debe contener al menos una letra minúscula").addConstraintViolation();

            return false;
        }

        if (!digit) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "La contraseña debe contener al menos un número").addConstraintViolation();

            return false;
        }

        return true;
    }
}