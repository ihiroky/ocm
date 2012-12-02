package net.ihiroky.ocm.handler;

import net.ihiroky.ocm.FieldOptionHandler;

/**
 * Stores a value of {@link Boolean} type option.
 *
 * @author Hiroki Itoh
 */
public class BooleanFieldOptionHandler extends FieldOptionHandler<Boolean> {

    /**
     * Converts value to {@link Boolean}.
     * @return a {@link Boolean} value
     */
    @Override
    public Boolean handle(String value) {
        return Boolean.valueOf(value);
    }

    /**
     * {@inheritDoc}
     * @return false.
     */
    @Override
    public boolean canAcceptHyphenValue() {
        return false;
    }

}
