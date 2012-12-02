package net.ihiroky.ocm.handler;

import net.ihiroky.ocm.FieldOptionHandler;

/**
 * Stores a value of {@link Character} type option.
 *
 * @author Hiroki Itoh
  */
public class CharFieldOptionHandler extends FieldOptionHandler<Character> {

    /**
     * Converts value to {@link Character}.
     * @return a {@link Character} value.
     */
    @Override
    public Character handle(String value) {
        return value.charAt(0);
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
