package ui.swing;

import java.util.List;
import java.util.Observable;

import app.TicketManagementBackend;
import entities.Priority;
import entities.Ticket;
import entities.TicketException;
import entities.Type;

public class SwingMainModel extends Observable {
    private final TicketManagementBackend backend;

    public SwingMainModel(TicketManagementBackend backend) {
        if (backend == null) throw new IllegalArgumentException("backend must not be null");
        this.backend = backend;
    }

    public List<Ticket> getAllTickets() throws TicketException {
        return backend.getAllTickets();
    }

    public void tmDataChanged() {
        setChanged();
        notifyObservers(this);
    }

    public Ticket getTicket(int id) throws TicketException {
        if (id < 0) return null;
        return backend.getTicketById(id);
    }

    public Ticket createNewTicket(String reporter, String topic,
        String description, Type type, Priority priority)
        throws TicketException {
        // simple null-coercion to avoid NPEs in backend
        if (reporter == null) reporter = "unknown";
        if (topic == null) topic = "";
    Ticket newTicket = backend.createNewTicket(reporter, topic, description, type, priority);
    setChanged();
    notifyObservers(this);
    return newTicket;
    }

    public void rejectTicket(int id) throws TicketException {
    backend.rejectTicket(id);
    setChanged();
    notifyObservers(this);
    }

    public void acceptTicket(int id) throws TicketException {
    backend.acceptTicket(id);
    setChanged();
    notifyObservers(this);
    }

    public void closeTicket(int id) throws TicketException {
    backend.closeTicket(id);
    setChanged();
    notifyObservers(this);
    }
    
    public List<Ticket> searchTicket(String name, Type type) throws TicketException {
        try {
            if (name == null) name = "";
            if (type == null) {
                return backend.getTicketsByName(name);
            } else {
                return backend.getTicketsByNameAndType(name, type);
            }
        } catch (UnsupportedOperationException e) {
            throw new TicketException("No search service registered", e);
        }
    }
}
