package net.ihiroky.ocm.handler;

import net.ihiroky.ocm.FieldOptionHandler;

/**
 * Stores a value of {@link Integer} type option.
 *
 * @author Hiroki Itoh
  */
public class IntFieldOptionHandler extends FieldOptionHandler<Integer> {

    /**
     * Convert a value to {@link Integer}.
     * @return a {@link Integer} value.
     */
    @Override
    public Integer handle(String value) {
        return Integer.parseInt(value);
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
