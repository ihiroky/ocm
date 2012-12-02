package net.ihiroky.ocm;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

/**
 * Holds field reflection object and it's {@link Arguments} annotation to set up a value to the field.
 *
 * @author Hiroki Itoh
 */
public class ArgumentsHandler {

    /** an object which has field annotated by {@link Arguments}. */
    private Object target;

    /** a field (concrete subtype of {@code java.util.Collection}) annotated by {@link Arguments}. */
    private Field field;

    /** {@link Arguments} annotation. */
    private Arguments arguments;

    /**
     *
     */
    protected ArgumentsHandler() {
    }

    /**
     * Sets a target.
     * @param target target to be set.
     */
    protected void setTarget(Object target) {
        this.target = target;
    }

    /**
     * Sets a field.
     * @param field feld to be set.
     */
    protected void setField(Field field) {
        this.field = field;
        field.setAccessible(true);
    }

    /**
     * Sets {@link Arguments}.
     * @param arguments aruments to be set.
     */
    protected void setArguments(Arguments arguments) {
        this.arguments = arguments;
    }

    /**
     * Allocates {@code List} object to the field if available.
     * @throws IllegalStateException if the reflection is failed.
     */
    protected void ensureArgument() {
        try {

            if (field != null && field.get(target) == null) {
                @SuppressWarnings("unchecked")
                Collection<String> c = (Collection<String>) field.getType().newInstance();
                field.set(target, c);
            }
        } catch (Exception e) {
            throw new IllegalStateException("failed to assign an object. "
                    + field.getName() + " must be a concrete subtype of java.util.Collection<String>.", e);
        }
    }

    /**
     * Gets {@link Arguments}.
     * @return arguments.
     */
    public Arguments getArguments() {
        return arguments;
    }

    /**
     * Adds an argument.
     *
     * @param value - an argument in command line arguments。
     * @throws ArgumentParseException if a reflection is failed.
     */
    @SuppressWarnings("unchecked")
    public void add(String value) throws ArgumentParseException {
        try {
            ensureArgument();
            Collection<String> args = (Collection<String>) field.get(target);
            args.add(value);
        } catch (ClassCastException cce) {
            throw new ArgumentParseException(
                    "a field annotated by @Arguments must be a instance of java.util.List", cce);
        } catch (Exception e) {
            throw new ArgumentParseException("failed to set argument " + value, e);
        }
    }

    /**
     * Checks if the field has no value.
     *
     * @return true if fields has no value.
     */
    @SuppressWarnings("unchecked")
    public boolean isEmpty() {
        try {
            List<String> args = (List<String>) field.get(target);
            return (args == null || args.isEmpty());
        } catch (Exception e) {
            //
            throw new Error("failed to get argument " + field.getName(), e);
        }
    }
}
