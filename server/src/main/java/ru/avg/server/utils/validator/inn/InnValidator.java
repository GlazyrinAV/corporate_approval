package ru.avg.server.utils.validator.inn;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class InnValidator implements ConstraintValidator<Inn, Long> {

    private final Pattern pattern = Pattern.compile("^\\d{10}$");

    @Override
    public boolean isValid(Long number, ConstraintValidatorContext constraintValidatorContext) {
        if (number == null) {
            return false;
        }
        return pattern.matcher(number.toString()).matches();
    }
}