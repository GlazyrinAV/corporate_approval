package ru.avg.server.utils.validator.inn;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator for INN (Individual Taxpayer Number) fields annotated with {@link Inn}.
 * Supports both String and Long types, ensuring the value matches a 10-digit numeric pattern.
 */
public class InnValidator implements ConstraintValidator<Inn, Object> {

    private static final Pattern PATTERN = Pattern.compile("^\\d{10}$");

    @Override
    public void initialize(Inn constraintAnnotation) {
        // Initialization logic if needed (e.g., configurable length)
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        String inn = value.toString();
        return PATTERN.matcher(inn).matches();
    }
}