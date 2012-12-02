package net.ihiroky.ocm.handler;

import net.ihiroky.ocm.FieldOptionHandler;

/**
 * Stores a value of {@link Long} type option.
 *
 * @author Hiroki Itoh
  */
public class LongFieldOptionHandler extends FieldOptionHandler<Long> {

    /**
     * Converts a value to {@link Long}.
     * @return a {@link Long} value.
     */
    @Override
    public Long handle(String value) {
        return Long.parseLong(value);
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
