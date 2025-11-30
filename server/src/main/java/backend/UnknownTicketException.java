package backend;

/**
 * Exception thrown when a requested ticket cannot be found.
 */
public final class UnknownTicketException extends Exception {

    private static final long serialVersionUID = -4619078398004213294L;

    /**
     * Default exception with a neutral message.
     */
    public UnknownTicketException() {
        super("Unknown ticket");
    }

    public UnknownTicketException(String message) {
        super(message);
    }

    public UnknownTicketException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownTicketException(Throwable cause) {
        super(cause == null ? "Unknown ticket" : cause.toString(), cause);
    }

    public UnknownTicketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
