package net.ihiroky.ocm;

import java.lang.reflect.Field;


/**
 * Stores option's value to a field annotated by {@link Option}.
 *
 * @param <T> field type.
 * @author Hiroki Itoh
 */
public abstract class FieldOptionHandler<T> {

    /** {@link Option} annotation. */
    private Option option;

    /** a object which has a field annotated by {@link Option}. */
    private Object target;

    /** a field annotated by {@link Option}. */
    private Field field;

    /** option name (long name). */
    private String name;

    /** option name alias (short name). */
    private String alias;

    /** long name option suffix. */
    static final String LONG_NAME_SUFFIX = "--";

    /** short name option suffix. */
    static final String SHORT_NAME_SUFFIX = "-";

    /**
     *
     */
    protected FieldOptionHandler() {
    }

    /**
     * Sets a value of target.
     * @param target target to be set.
     */
    protected void setTarget(Object target) {
        this.target = target;
    }

    /**
     * Sets a value of field.
     * @param field field to be set.
     */
    protected void setField(Field field) {
        this.field = field;
        field.setAccessible(true);
    }

    /**
     * Sets a value of option.
     * @param option option to be set.
     */
    protected void setOption(Option option) {
        this.option = option;
        this.name = LONG_NAME_SUFFIX.concat(option.name());
        if (option.alias() != null) {
            this.alias = SHORT_NAME_SUFFIX.concat(option.alias());
        }
    }

    /**
     * Gets a value of option.
     * @return a value of option.
     */
    protected Option getOption() {
        return option;
    }

    /**
     * Gets a value of name.
     * @return a value of name.
     */
    protected String getName() {
        return name;
    }

    /**
     * Gets a value of alias.
     * @return a value of alias.
     */
    protected String getAlias() {
        return alias;
    }

    /**
     * Gets a value of target.
     * @return a value of target.
     */
    protected Object getTarget() {
        return target;
    }

    /**
     * Gets a value of field.
     * @return a value of field.
     */
    protected Field getField() {
        return field;
    }

    /**
     * Sets a type-converted value.
     *
     * @param value
     * @throws ArgumentParseException if reflection is failed.
     */
    protected void set(String value) throws ArgumentParseException {
        try {
            T handled = handle(value);
            getField().set(getTarget(),  handled);
        } catch (Exception e) {
            throw new ArgumentParseException("failed to set argument. option : " + toString(), e);
        }
    }

    /**
     * Returns a string expression of this.
     * @return a string expression.
     */
    @Override
    public String toString() {
        return (alias.length() <= SHORT_NAME_SUFFIX.length()) ? name : (alias + '/' + name);
    }

    /**
     * Converts type of value.
     *
     * @param value value to be converted.
     * @return a converted value.
     */
    public abstract T handle(String value);

    /**
     * Check if this instance can accept a value which starts with '-'.
     *
     * @return true if this instance can accept a value which starts with '-'.
     */
    public abstract boolean canAcceptHyphenValue();
}
