package app;

import java.util.List;

import entities.Priority;
import entities.Ticket;
import entities.TicketException;
import entities.Type;

/**
 * API used by the GUI to create, list and modify {@link Ticket}s.
 *
 * This backend abstraction also extends {@link Shutdown} (to allow clients to trigger
 * a graceful shutdown of resources) and {@link TicketSearchBackend} (search capabilities).
 *
 * Status transition semantics used by the UI:
 * - acceptTicket(...) transitions a ticket from OPEN (alias NEW) to IN_PROGRESS
 * - rejectTicket(...) transitions a ticket from OPEN (alias NEW) to CLOSED
 * - closeTicket(...) transitions a ticket from IN_PROGRESS to CLOSED
 *
 * Implementations must enforce transition rules and throw {@link TicketException}
 * when an operation is not permitted or a ticket id is unknown.
 */
public interface TicketManagementBackend extends Shutdown, TicketSearchBackend {

    /**
     * Create a new ticket with the supplied information.
     *
     * @param reporter    the name of the reporter (may be null/empty)
     * @param topic       the topic/title of the ticket (may be null/empty)
     * @param description textual description (may be null)
     * @param type        ticket {@link Type} (may be null)
     * @param priority    ticket {@link Priority} (may be null)
     * @return the created {@link Ticket} (with assigned id)
     * @throws TicketException if the creation failed for any reason
     */
    Ticket createNewTicket(String reporter, String topic, String description, Type type, Priority priority)
            throws TicketException;

    /**
     * Return a snapshot list of all tickets currently available in the system.
     *
     * @return list of tickets (never null, may be empty)
     * @throws TicketException if a technical problem occurs
     */
    List<Ticket> getAllTickets() throws TicketException;

    /**
     * Return a single ticket by id.
     *
     * @param id ticket id
     * @return the {@link Ticket} or null if not found
     * @throws TicketException if a technical error occurs while retrieving the ticket
     */
    Ticket getTicketById(int id) throws TicketException;

    /**
     * Accept a ticket: transition from OPEN (alias NEW) to IN_PROGRESS.
     *
     * Implementations must validate the current status and throw {@link TicketException}
     * if the transition is not allowed or the ticket does not exist.
     *
     * @param id ticket id
     * @return updated {@link Ticket}
     * @throws TicketException if the status change is not allowed or the ticket is unknown
     */
    Ticket acceptTicket(int id) throws TicketException;

    /**
     * Reject a ticket: transition from OPEN (alias NEW) to CLOSED.
     *
     * Implementations must validate the current status and throw {@link TicketException}
     * if the transition is not allowed or the ticket does not exist.
     *
     * @param id ticket id
     * @return updated {@link Ticket}
     * @throws TicketException if the status change is not allowed or the ticket is unknown
     */
    Ticket rejectTicket(int id) throws TicketException;

    /**
     * Close a ticket: transition from IN_PROGRESS to CLOSED.
     *
     * Implementations must validate the current status and throw {@link TicketException}
     * if the transition is not allowed or the ticket does not exist.
     *
     * @param id ticket id
     * @return updated {@link Ticket}
     * @throws TicketException if the status change is not allowed or the ticket is unknown
     */
    Ticket closeTicket(int id) throws TicketException;

}
