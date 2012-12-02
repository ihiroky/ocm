package net.ihiroky.ocm.handler;

import net.ihiroky.ocm.FieldOptionHandler;

/**
 * Stores a value of {@link String} type option.
 *
 * @author Hiroki Itoh
 * @version $Revision: 1.1 $ $Date: 2010/09/22 04:58:18 $
 */
public class StringFieldOptionHandler extends FieldOptionHandler<String> {

    /**
     * returns a specific value itself.
     * @return a value itself.
     */
    @Override
    public String handle(String value) {
        return value;
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
