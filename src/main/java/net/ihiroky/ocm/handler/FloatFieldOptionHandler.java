package net.ihiroky.ocm.handler;

import net.ihiroky.ocm.FieldOptionHandler;

/**
 * Stores a value of {@link Float} type option.
 *
 * @author Hiroki Itoh
  */
public class FloatFieldOptionHandler extends FieldOptionHandler<Float> {

    /**
     * Converts a value to {@link Float}.
     * @return a {@link Float} value.
     */
    @Override
    public Float handle(String value) {
        return Float.parseFloat(value);
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
