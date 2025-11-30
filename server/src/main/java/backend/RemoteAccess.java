package backend;

import java.util.Objects;

/**
 * Abstraction for a remote access component that exposes/manages {@link TicketStore} operations.
 *
 * Implementations are expected to be Runnable (the {@link #run()} method performs the main loop)
 * and provide lifecycle hooks to prepare and shutdown resources.
 *
 * Implementations should be thread-safe and honour {@link #shutdown()} to stop cleanly.
 */
public interface RemoteAccess extends Runnable {

    /**
     * Prepare the remote access implementation for startup.
     *
     * This method is called before the implementation's {@link #run()} is started.
     * Implementations should store the {@code ticketStore} reference and initialize any
     * required resources (sockets, thread pools, etc).
     *
     * @param ticketStore non-null TicketStore the implementation will operate on
     * @throws NullPointerException if ticketStore is null
     */
    void prepareStartup(TicketStore ticketStore);

    /**
     * Request a graceful shutdown of the remote access implementation.
     *
     * Implementations should stop accepting new requests, release resources and cause
     * {@link #run()} to return in a timely manner.
     */
    void shutdown();

    /**
     * Convenience default: a human friendly name for the implementation instance.
     * Useful for logging. Implementations may override.
     *
     * @return instance name (defaults to concrete class simple name)
     */
    default String getDisplayName() {
        return this.getClass().getSimpleName();
    }
}
