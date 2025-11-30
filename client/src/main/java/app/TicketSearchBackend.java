package app;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import entities.Ticket;
import entities.TicketException;
import entities.Type;

/**
 * Defines the actions the GUI uses to search for {@link Ticket}s.
 *
 * Default implementations return an empty list so UI code can operate when
 * no search-capable backend is provided. Implementations should override
 * these methods to provide real search behaviour.
 */
public interface TicketSearchBackend {

    /**
     * Search for tickets by name/text.
     *
     * @param name text to search (may be null)
     * @return list of matching tickets (never null)
     * @throws TicketException if something failed during search
     */
    default List<Ticket> getTicketsByName(String name) throws TicketException {
        Objects.requireNonNull(name == null ? "" : name);
        return Collections.emptyList();
    }

    /**
     * Search for tickets by name/text and type.
     *
     * @param name text to search (may be null)
     * @param type ticket type filter (may be null)
     * @return list of matching tickets (never null)
     * @throws TicketException if something failed during search
     */
    default List<Ticket> getTicketsByNameAndType(String name, Type type) throws TicketException {
        Objects.requireNonNull(name == null ? "" : name);
        return Collections.emptyList();
    }
}