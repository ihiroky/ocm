package net.ihiroky.ocm.handler;

import net.ihiroky.ocm.FieldOptionHandler;

/**
 * Stores a value of {@link Short} type option.
 *
 * @author Hiroki Itoh
 */
public class ShortFieldOptionHandler extends FieldOptionHandler<Short> {

    /**
     * Converts a value to {@link Short}.
     * @return a {@link Short} value.
     */
    @Override
    public Short handle(String value) {
        return Short.parseShort(value);
    }

    /**
     * {@inheritDoc}
     * @return true.
     */
    @Override
    public boolean canAcceptHyphenValue() {
        return true;
    }

}
