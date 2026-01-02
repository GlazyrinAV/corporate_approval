package ru.avg.server.utils.validator.inn;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator implementation for the {@link Inn} annotation that validates Russian Individual Taxpayer Numbers (INN).
 * This validator ensures that a field annotated with {@link Inn} contains a valid 10-digit numeric value,
 * supporting both String and numeric (Long, Integer) field types by converting values to String representation.
 * <p>
 * The validation logic checks that:
 * <ul>
 *   <li>The value is not null</li>
 *   <li>The string representation consists exactly of 10 digits</li>
 *   <li>No other characters (including spaces, dashes, or letters) are present</li>
 * </ul>
 * </p>
 * <p>
 * This validator is used automatically by the Bean Validation framework when a field is annotated with {@link Inn}.
 * It supports various field types through the use of {@code toString()} conversion, making it flexible for different
 * data models while maintaining consistent validation rules.
 * </p>
 *
 * @see Inn
 * @see ConstraintValidator
 * @author AVG
 * @since 1.0
 */
public class InnValidator implements ConstraintValidator<Inn, Object> {

    /**
     * Regular expression pattern to match exactly 10 consecutive digits.
     * This pattern enforces the Russian INN format requirement of being a 10-digit number
     * with no additional characters, spaces, or formatting.
     * The pattern is compiled once at class loading time for performance efficiency.
     *
     * @see Pattern
     */
    private static final Pattern PATTERN = Pattern.compile("^\\d{10}$");

    /**
     * Initializes the validator with the constraint annotation.
     * This method is called by the Bean Validation framework before any validation occurs.
     * <p>
     * In this implementation, no initialization logic is required as the validation
     * rules are fixed (10-digit numeric pattern). However, this method could be extended
     * to support configurable parameters from the annotation if needed in the future.
     * </p>
     *
     * @param constraintAnnotation the annotation instance containing validation parameters
     */
    @Override
    public void initialize(Inn constraintAnnotation) {
        // Initialization logic if needed (e.g., configurable length)
    }

    /**
     * Validates whether the provided value is a valid INN (10-digit number).
     * <p>
     * The validation process:
     * <ol>
     *   <li>Checks if the value is null - returns false if null</li>
     *   <li>Converts the value to its string representation using {@code toString()}</li>
     *   <li>Matches the string against the 10-digit pattern using regular expression</li>
     *   <li>Returns true only if the string consists exactly of 10 digits</li>
     * </ol>
     * </p>
     * <p>
     * This method supports various input types (String, Long, Integer, etc.) through
     * the use of {@code toString()}, making it flexible for different field types
     * while maintaining consistent validation rules.
     * </p>
     *
     * @param value   the value to validate, can be of any type but typically String, Long, or Integer
     * @param context context for constraint validation, providing access to validation metadata and configuration
     * @return true if the value is a valid 10-digit INN, false otherwise (including null values)
     * @see ConstraintValidator#isValid(Object, ConstraintValidatorContext)
     * @see #PATTERN
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        String inn = value.toString();
        return PATTERN.matcher(inn).matches();
    }
}