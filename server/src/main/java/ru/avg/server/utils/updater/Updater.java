package ru.avg.server.utils.updater;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility component for performing partial updates on objects using reflection.
 * Copies non-null and non-blank field values from a source object to a target object.
 * This class supports PATCH-style semantics where only provided (non-null, non-blank)
 * fields are updated in the target object.
 *
 * <p>The implementation uses Java reflection to discover getter methods in the source
 * object and corresponding setter methods in the target object. For each field, if both
 * accessor methods exist and the value is valid (non-null and, for strings, non-blank),
 * the value is copied from source to target.</p>
 *
 * <p>This component is designed to be injected as a Spring bean into services that
 * require generic partial update capabilities, promoting code reuse and reducing
 * boilerplate in update operations.</p>
 *
 * @implNote This class relies on runtime reflection which has performance implications.
 *           It should be used cautiously in high-throughput or latency-sensitive scenarios.
 *           Consider alternatives like MapStruct or BeanUtils for better performance
 *           in production-critical paths.
 */
@Component
public class Updater {

    /**
     * Performs a partial update of the target object using values from the source object.
     * Only fields with non-null and non-blank (for String types) values in the source
     * are copied to the target. The method uses reflection to locate and invoke
     * getter methods on the source and setter methods on the target.
     *
     * <p>For String fields, "blank" is determined by {@link String#trim()} followed by
     * {@link String#isEmpty()} check. For all other types, only null values are skipped.</p>
     *
     * <p>The method mutates the target object in-place and returns it for method chaining.
     * Both source and target objects must follow JavaBean conventions with proper
     * getter and setter methods for the fields to be processed.</p>
     *
     * @param <T>    the type of the objects; typically a JavaBean with getters and setters
     * @param target the destination object to be updated; must not be null
     * @param source the source object containing new values; must not be null
     * @return the updated target object with copied values from source
     * @throws IllegalArgumentException if an error occurs during reflection operations,
     *                                  such as inaccessible methods or invocation failures
     */
    public <T> T update(T target, T source) {

        T result = target;

        for (Field field : source.getClass().getDeclaredFields()) {
            String name = Character.toUpperCase(field.getName().charAt(0))
                    + field.getName().substring(1);
            Method getter;
            try {
                getter = source.getClass().getDeclaredMethod("get" + name);
            } catch (Exception ex) {
                getter = null;
            }
            Method setter;
            try {
                setter = result.getClass().getDeclaredMethod("set" + name, field.getType());
            } catch (Exception ex) {
                setter = null;
            }
            try {
                if (getter != null && setter != null && getter.invoke(source) != null &&
                        !getter.invoke(source).toString().trim().isBlank()) {
                    setter.invoke(result, getter.invoke(source));
                }
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        return result;
    }
}