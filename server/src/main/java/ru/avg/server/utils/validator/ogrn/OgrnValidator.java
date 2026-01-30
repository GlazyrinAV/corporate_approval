package ru.avg.server.utils.validator.ogrn;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator implementation for the {@link Ogrn} annotation that validates Russian Main State Registration Numbers (OGRN).
 * This validator ensures that a field annotated with {@link Ogrn} contains a valid 13-digit numeric value,
 * supporting both String and numeric (Long, Integer) field types by converting values to String representation.
 * <p>
 * The validation logic checks that:
 * <ul>
 *   <li>The value is not null</li>
 *   <li>The string representation consists exactly of 13 digits</li>
 *   <li>No other characters (including spaces, dashes, or letters) are present</li>
 * </ul>
 * </p>
 * <p>
 * This validator is used automatically by the Bean Validation framework when a field is annotated with {@link Ogrn}.
 * It supports various field types through the use of {@code toString()} conversion, making it flexible for different
 * data models while maintaining consistent validation rules.
 * </p>
 *
 * @see Ogrn
 * @see ConstraintValidator
 * @author AVG
 * @since 1.0
 */
public class OgrnValidator implements ConstraintValidator<Ogrn, Object> {

    /**
     * Regular expression pattern to match exactly 13 consecutive digits.
     * This pattern enforces the Russian OGRN format requirement of being a 13-digit number
     * with no additional characters, spaces, or formatting.
     * The pattern is compiled once at class loading time for performance efficiency.
     *
     * @see Pattern
     */
    private static final Pattern PATTERN = Pattern.compile("^\\d{13}$");

    /**
     * Initializes the validator with the constraint annotation.
     * This method is called by the Bean Validation framework before any validation occurs.
     * <p>
     * In this implementation, no initialization logic is required as the validation
     * rules are fixed (13-digit numeric pattern). However, this method could be extended
     * to support configurable parameters from the annotation if needed in the future.
     * </p>
     *
     * @param constraintAnnotation the annotation instance containing validation parameters
     */
    @Override
    public void initialize(Ogrn constraintAnnotation) {
        // Initialization logic if needed (e.g., configurable length)
    }

    /**
     * Validates whether the provided value is a valid OGRN (13-digit number).
     * <p>
     * The validation process:
     * <ol>
     *   <li>Checks if the value is null - returns false if null</li>
     *   <li>Converts the value to its string representation using {@code toString()}</li>
     *   <li>Matches the string against the 13-digit pattern using regular expression</li>
     *   <li>Returns true only if the string consists exactly of 13 digits</li>
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
     * @return true if the value is a valid 13-digit OGRN, false otherwise (including null values)
     * @see ConstraintValidator#isValid(Object, ConstraintValidatorContext)
     * @see #PATTERN
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        String ogrn = value.toString();
        return PATTERN.matcher(ogrn).matches();
    }
}