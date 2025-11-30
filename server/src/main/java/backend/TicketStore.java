package backend;

import java.util.List;

import entities.Priority;
import entities.Status;
import entities.Ticket;
import entities.Type;

/**
 * Thread-safe abstraction for a ticket storage backend.
 *
 * Implementations must provide creation, status update and listing operations.
 * A small convenience default method is provided to lookup a ticket by id using getAllTickets().
 */
public interface TicketStore {

    /**
     * Store a new ticket and return the created Ticket (with assigned id).
     *
     * @param reporter    reporter name
     * @param topic       brief topic/title
     * @param description detailed description
     * @param type        ticket type
     * @param priority    ticket priority
     * @return created Ticket instance (with id assigned)
     */
    Ticket storeNewTicket(String reporter, String topic, String description,
                          Type type, Priority priority);

    /**
     * Update the status of an existing ticket.
     *
     * @param ticketId  id of the ticket to update
     * @param newStatus new status to set
     * @throws UnknownTicketException if the ticket id does not exist
     * @throws IllegalStateException  if the provided status is invalid
     */
    void updateTicketStatus(int ticketId, Status newStatus) throws UnknownTicketException, IllegalStateException;

    /**
     * Return a snapshot list of all tickets.
     *
     * @return list of tickets (may be empty, never null)
     */
    List<Ticket> getAllTickets();

    /**
     * Convenience lookup: find a ticket by id. Default implementation scans getAllTickets().
     *
     * Implementations that can provide a more efficient lookup should override this method.
     *
     * @param id ticket id
     * @return Ticket if found, otherwise null
     */
    default Ticket findTicketById(int id) {
        if (id < 0) return null;
        for (Ticket t : getAllTickets()) {
            if (t != null && t.getId() == id) return t;
        }
        return null;
    }
}
