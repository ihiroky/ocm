package net.ihiroky.ocm;

/**
 * This exception is thrown when input arguments are invalid.
 *
 * @author Hiroki Itoh
 */
public class ArgumentParseException extends Exception {

    /** serial version. */
    private static final long serialVersionUID = -8485214691225423888L;

    public ArgumentParseException() {
    }

    public ArgumentParseException(String message) {
        super(message);
    }

    public ArgumentParseException(Throwable cause) {
        super(cause);
    }

    public ArgumentParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
