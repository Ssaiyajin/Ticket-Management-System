package backend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import entities.Priority;
import entities.Status;
import entities.Ticket;
import entities.Type;

/**
 * Basic thread-safe implementation of the TicketStore interface for testing.
 *
 * - Thread-safe via ConcurrentHashMap + AtomicInteger
 * - Returns defensive copies from getAllTickets()
 * - updateTicketStatus validates input and throws UnknownTicketException when appropriate
 *
 * NOTE: Still intended for testing only.
 */
public class SimpleTicketStore implements TicketStore {

    private final AtomicInteger nextTicketId = new AtomicInteger(1);
    private final ConcurrentMap<Integer, Ticket> ticketMap = new ConcurrentHashMap<>();

    @Override
    public Ticket storeNewTicket(String reporter, String topic, String description, Type type, Priority priority) {
        if (reporter == null) reporter = "unknown";
        if (topic == null) topic = "";
        int id = nextTicketId.getAndIncrement();
        Ticket newTicket = new Ticket(id, reporter, topic, description, type, priority);
        ticketMap.put(id, newTicket);
        System.out.println("Created new Ticket id=" + id + " reporter=" + reporter + " topic=\"" + topic + "\"");
        return newTicket;
    }

    @Override
    public void updateTicketStatus(int ticketId, Status newStatus) throws UnknownTicketException, IllegalStateException {
        if (newStatus == null) throw new IllegalStateException("newStatus must not be null");
        Ticket t = ticketMap.get(ticketId);
        if (t == null) throw new UnknownTicketException("Ticket id " + ticketId + " not found");
        t.setStatus(newStatus);
    }

    @Override
    public List<Ticket> getAllTickets() {
        List<Ticket> snapshot = new ArrayList<>();
        for (Ticket t : ticketMap.values()) {
            if (t == null) continue;
            try {
                snapshot.add(t.clone());
            } catch (Exception e) {
                // fallback: add original if cloning fails
                snapshot.add(t);
            }
        }
        return snapshot;
    }
}
