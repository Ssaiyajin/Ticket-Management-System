package app;

/**
 * Functional contract to trigger a graceful shutdown of resources.
 *
 * Implementations should perform best‑effort cleanup and return promptly.
 */
@FunctionalInterface
public interface Shutdown {

    /**
     * Trigger graceful shutdown (best‑effort).
     */
    void triggerShutdown();
}
