package app;

import java.util.List;

import entities.Priority;
import entities.Ticket;
import entities.TicketException;
import entities.Type;

/**
 * Simple JMSTicketManagementBackend stub.
 *
 * - Currently delegates to an in-memory LocalTicketManagementBackend as a safe fallback
 *   so the UI and tests can operate without a JMS infrastructure.
 * - Replace the delegate calls with a real JMS implementation when ready.
 */
public class JMSTicketManagementBackend implements TicketManagementBackend {

    // fallback delegate used until JMS integration is implemented
    private final LocalTicketManagementBackend delegate = new LocalTicketManagementBackend();

    public JMSTicketManagementBackend() {
        // TODO: initialize JMS resources (ConnectionFactory, Connection, Session, Destinations, etc.)
    }

    @Override
    public void triggerShutdown() {
        // TODO: cleanly close JMS resources
        delegate.triggerShutdown();
    }

    @Override
    public Ticket createNewTicket(String reporter, String topic, String description, Type type, Priority priority)
            throws TicketException {
        // TODO: send create request via JMS and wait/receive response
        return delegate.createNewTicket(reporter, topic, description, type, priority);
    }

    @Override
    public List<Ticket> getAllTickets() throws TicketException {
        // TODO: request list via JMS or subscribe to updates
        return delegate.getAllTickets();
    }

    @Override
    public Ticket getTicketById(int id) throws TicketException {
        // TODO: implement remote lookup via JMS
        return delegate.getTicketById(id);
    }

    @Override
    public Ticket acceptTicket(int id) throws TicketException {
        // TODO: publish accept action via JMS
        return delegate.acceptTicket(id);
    }

    @Override
    public Ticket rejectTicket(int id) throws TicketException {
        // TODO: publish reject action via JMS
        return delegate.rejectTicket(id);
    }

    @Override
    public Ticket closeTicket(int id) throws TicketException {
        // TODO: publish close action via JMS
        return delegate.closeTicket(id);
    }
}
