package net.ihiroky.ocm;

/**
 * Shows a type which stores option's value is invalid.
 *
 * @author Hiroki Itoh
 */
public class IllegalArgumentTypeException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -955411597706144432L;

    public IllegalArgumentTypeException() {
    }

    public IllegalArgumentTypeException(String message) {
        super(message);

    }

    public IllegalArgumentTypeException(Throwable cause) {
        super(cause);
    }

    public IllegalArgumentTypeException(String message, Throwable cause) {
        super(message, cause);
    }

}
