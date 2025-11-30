package app;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import entities.Priority;
import entities.Status;
import entities.Ticket;
import entities.TicketException;
import entities.Type;

public class LocalTicketManagementBackend implements TicketManagementBackend {

    private final ConcurrentMap<Integer, Ticket> localTicketStore = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public LocalTicketManagementBackend() {
    }

    @Override
    public void triggerShutdown() {
        // In-memory backend: nothing to close.
    }

    @Override
    public Ticket createNewTicket(String reporter, String topic, String description, Type type, Priority priority) {
        if (reporter == null) reporter = "unknown";
        if (topic == null) topic = "";
        int id = nextId.getAndIncrement();
        Ticket newTicket = new Ticket(id, reporter, topic, description, type, priority);
        localTicketStore.put(id, newTicket);
        try {
            return newTicket.clone();
        } catch (Exception e) {
            // fallback to returning original (shouldn't happen)
            return newTicket;
        }
    }

    @Override
    public List<Ticket> getAllTickets() throws TicketException {
        return localTicketStore.values()
                .stream()
                .map(t -> {
                    try {
                        return t.clone();
                    } catch (Exception e) {
                        return t;
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public Ticket getTicketById(int id) throws TicketException {
        Ticket t = getTicketByIdInternal(id);
        try {
            return t.clone();
        } catch (Exception e) {
            return t;
        }
    }

    private Ticket getTicketByIdInternal(int id) throws TicketException {
        Ticket t = localTicketStore.get(id);
        if (t == null) throw new TicketException("Ticket ID is unknown: " + id);
        return t;
    }

    @Override
    public Ticket acceptTicket(int id) throws TicketException {
        Ticket ticketToModify = getTicketByIdInternal(id);
        synchronized (ticketToModify) {
            // Accept: OPEN (alias NEW) -> IN_PROGRESS
            if (ticketToModify.getStatus() != Status.OPEN && ticketToModify.getStatus() != Status.NEW) {
                throw new TicketException("Cannot accept Ticket as it is currently in status " + ticketToModify.getStatus());
            }
            ticketToModify.setStatus(Status.IN_PROGRESS);
            try {
                return ticketToModify.clone();
            } catch (Exception e) {
                return ticketToModify;
            }
        }
    }

    @Override
    public Ticket rejectTicket(int id) throws TicketException {
        Ticket ticketToModify = getTicketByIdInternal(id);
        synchronized (ticketToModify) {
            // Reject: OPEN (alias NEW) -> CLOSED
            if (ticketToModify.getStatus() != Status.OPEN && ticketToModify.getStatus() != Status.NEW) {
                throw new TicketException("Cannot reject Ticket as it is currently in status " + ticketToModify.getStatus());
            }
            ticketToModify.setStatus(Status.CLOSED);
            try {
                return ticketToModify.clone();
            } catch (Exception e) {
                return ticketToModify;
            }
        }
    }

    @Override
    public Ticket closeTicket(int id) throws TicketException {
        Ticket ticketToModify = getTicketByIdInternal(id);
        synchronized (ticketToModify) {
            // Close: IN_PROGRESS -> CLOSED
            if (ticketToModify.getStatus() != Status.IN_PROGRESS) {
                throw new TicketException("Cannot close Ticket as it is currently in status " + ticketToModify.getStatus());
            }
            ticketToModify.setStatus(Status.CLOSED);
            try {
                return ticketToModify.clone();
            } catch (Exception e) {
                return ticketToModify;
            }
        }
    }

}
