package entities;

/**
 * Exception to be thrown if a problem occurs during ticket handling.
 */
public class TicketException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Empty Exception.
     */
    public TicketException() {
        super();
    }

    /**
     * Exception with description.
     *
     * @param message the descriptive message
     */
    public TicketException(String message) {
        super(message);
    }

    /**
     * Exception with description and cause
     *
     * @param message the descriptive message
     * @param cause   wrapped Throwable cause
     */
    public TicketException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Exception with cause only.
     *
     * @param cause wrapped Throwable cause
     */
    public TicketException(Throwable cause) {
        super(cause == null ? null : cause.toString(), cause);
    }

    /**
     * Wrap any Throwable in a TicketException (no-op if already a TicketException).
     *
     * @param t throwable to wrap
     * @return TicketException instance
     */
    public static TicketException wrap(Throwable t) {
        if (t instanceof TicketException) return (TicketException) t;
        return new TicketException(t);
    }
}
