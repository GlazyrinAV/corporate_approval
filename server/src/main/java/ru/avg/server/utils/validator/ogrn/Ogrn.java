package ru.avg.server.utils.validator.ogrn;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to validate that a field contains a valid Russian OGRN (Main State Registration Number).
 * Supports fields of type String, Long, or Integer containing a 13-digit numeric value.
 * <p>
 * The OGRN is a unique identifier assigned to legal entities in Russia upon state registration.
 * It consists of 13 digits with the following structure:
 * <ul>
 *   <li>Positions 1–2: Two-digit code of the Russian Federation subject</li>
 *   <li>Position 3: Check digit calculated using a specific algorithm</li>
 *   <li>Positions 4–12: Serial number within the region</li>
 *   <li>Position 13: Check digit (same as position 3)</li>
 * </ul>
 * </p>
 * <p>
 * This annotation can be applied to fields, method parameters, constructors, or other annotations.
 * When used, it triggers validation via {@link OgrnValidator} to ensure the value meets format
 * and checksum requirements defined by Russian legislation.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 * {@code @Ogrn private String companyOgrn;}
 * {@code @Ogrn private Long ogrnNumber;}
 * </pre>
 * </p>
 *
 * @see OgrnValidator for the implementation of validation logic
 * @author AVG
 * @since 1.0
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {OgrnValidator.class})
public @interface Ogrn {

    /**
     * Default message returned when validation fails.
     * Provides clear feedback about the expected format: a 13-digit number.
     *
     * @return the error message displayed if the annotated value is invalid
     */
    String message() default "OGRN must be a 13-digit number";

    /**
     * Specifies the validation groups this constraint belongs to.
     * Allows selective application of constraints during validation.
     * <p>
     * For example, different groups can be used for create vs update operations,
     * enabling context-sensitive validation rules.
     * </p>
     *
     * @return an array of group classes; defaults to empty
     * @see jakarta.validation.groups.Default
     */
    Class<?>[] groups() default {};

    /**
     * Allows association of metadata payloads with the constraint.
     * Payloads are typically used by framework or tooling code (e.g., logging, reporting)
     * to attach additional information to constraints without affecting validation logic.
     * <p>
     * Common use cases include severity levels (INFO, WARNING, ERROR) or error codes.
     * </p>
     *
     * @return an array of payload classes; defaults to empty
     */
    Class<? extends Payload>[] payload() default {};
}