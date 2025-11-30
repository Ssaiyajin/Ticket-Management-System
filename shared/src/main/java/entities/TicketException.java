package entities;

/**
 * Exception to be thrown if a problem occurs during ticket handling.
 */
public class TicketException extends Exception {

    private static final long serialVersionUID = 1L;

    /** Empty exception. */
    public TicketException() {
        super();
    }

    /** Exception with description. */
    public TicketException(String message) {
        super(message);
    }

    /** Exception with cause only. */
    public TicketException(Throwable cause) {
        super(cause);
    }

    /** Exception with description and cause. */
    public TicketException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a TicketException with a formatted message.
     * Usage: TicketException.of("Failed to process %s", id)
     */
    public static TicketException of(String format, Object... args) {
        return new TicketException(String.format(format, args));
    }

    /**
     * Wrap any throwable into a TicketException (no-op if already a TicketException).
     */
    public static TicketException wrap(Throwable t) {
        if (t instanceof TicketException) {
            return (TicketException) t;
        }
        return new TicketException(t);
    }
}
