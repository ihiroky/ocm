package net.ihiroky.ocm.handler;

import net.ihiroky.ocm.FieldOptionHandler;

/**
 * Stores a value of {@link Byte} type option.
 *
 * @author Hiroki Itoh
 */
public class ByteFieldOptionHandler extends FieldOptionHandler<Byte> {

    /**
     * Convert a value to {@link Byte}.
     * @return a {@link Byte} value
     */
    @Override
    public Byte handle(String value) {
        return Byte.parseByte(value);
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
