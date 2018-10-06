package csse2002.block.world;

/**
 * An exception that indicates that an action is formatted incorrectly.
 */
public class ActionFormatException extends Exception {

    /**
     * Constructs a new exception with no message.
     */
    public ActionFormatException() {
        super();
    }

    /**
     * Constructs an exception with the given message.
     */
    public ActionFormatException(String message) {
        super(message);
    }

}