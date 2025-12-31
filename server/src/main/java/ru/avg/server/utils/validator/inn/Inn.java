package ru.avg.server.utils.validator.inn;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to validate that a field contains a valid Russian INN (Individual Taxpayer Number).
 * Supports fields of type String, Long, or Integer containing a 10-digit numeric value.
 * <p>
 * Example usage:
 * <pre>
 * {@code @Inn private String companyInn;}
 * {@code @Inn private Long taxpayerNumber;}
 * </pre>
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {InnValidator.class})
public @interface Inn {

    /**
     * Default message if validation fails.
     * Changed from generic "id format error" to more specific and user-friendly message.
     *
     * @return the error message
     */
    String message() default "INN must be a 10-digit number";

    /**
     * Groups the constraints by type for selective validation.
     *
     * @return the constraint groups
     */
    Class<?>[] groups() default {};

    /**
     * Used to assign payload objects to constraints.
     * Payloads can be used by clients of the Bean Validation API to associate metadata with a constraint.
     *
     * @return the payload classes
     */
    Class<? extends Payload>[] payload() default {};
}