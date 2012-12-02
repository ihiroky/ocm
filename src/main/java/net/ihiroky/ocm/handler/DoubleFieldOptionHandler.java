package net.ihiroky.ocm.handler;

import net.ihiroky.ocm.FieldOptionHandler;

/**
 * Stores a value of {@link Double} type option.
 *
 * @author Hiroki Itoh
  */
public class DoubleFieldOptionHandler extends FieldOptionHandler<Double> {

    /**
     * Converts a value to {@link Double}.
     * @return a {@link Double} value.
     */
    @Override
    public Double handle(String value) {
        return Double.parseDouble(value);
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
