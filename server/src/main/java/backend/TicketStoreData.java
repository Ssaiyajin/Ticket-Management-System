package backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;

import entities.Priority;
import entities.RawData;
import entities.Status;
import entities.Ticket;
import entities.Type;

/**
 * Thread-safe in-memory ticket store with simple support for assembling chunked RawData payloads.
 *
 * Notes:
 * - Uses Gson to deserialize the payload carried in RawData.data (textual JSON).
 * - Chunk assembly key uses clientId + ":" + requestType to avoid collisions per-client/request-type.
 */
public class TicketStoreData implements TicketStore {

    private static final Map<Integer, Ticket> ticketData = new ConcurrentHashMap<>();
    private static final Map<String, RawData[]> chunkPartialData = new ConcurrentHashMap<>();
    private static final AtomicInteger idGenerator = new AtomicInteger(1);

    private final String rawTicketData; // JSON string passed to handleRequest

    public TicketStoreData(String rawTicketData) {
        this.rawTicketData = rawTicketData;
    }

    /**
     * Parse the rawTicketData payload and store or assemble tickets as appropriate.
     */
    public void handleRequest() {
        Gson g = new Gson();
        RawData raw = g.fromJson(this.rawTicketData, RawData.class);
        if (raw == null) return;

        if (isFullTicket(raw) && !isUpdateRequest(raw)) {
            Ticket t = createFullTicket(raw);
            if (t != null) {
                storeNewTicket(t.getReporter(), t.getTopic(), t.getDescription(), t.getType(), t.getPriority());
            }
            return;
        }

        if (!isFullTicket(raw)) {
            Ticket assembled = createTicketFromChunk(raw);
            if (assembled != null) {
                storeNewTicket(assembled.getReporter(), assembled.getTopic(), assembled.getDescription(),
                        assembled.getType(), assembled.getPriority());
            }
        }

        // update requests are currently not handled here; they should be routed to updateTicketStatus(...)
    }

    /**
     * Create Ticket from a RawData that carries a full JSON ticket in data.
     */
    public Ticket createFullTicket(RawData rawDataObject) {
        if (rawDataObject == null || rawDataObject.getData() == null) return null;
        try {
            return new Gson().fromJson(rawDataObject.getData(), Ticket.class);
        } catch (Exception ex) {
            System.out.println("Failed to parse full ticket JSON: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Assemble chunked RawData pieces for a client/request into a Ticket once all chunks arrive.
     * Returns assembled Ticket if assembly completed, otherwise null.
     */
    public Ticket createTicketFromChunk(RawData rawDataObject) {
        if (rawDataObject == null) return null;
        String key = makeChunkKey(rawDataObject);
        int totalChunks = rawDataObject.getTotalChunks();
        int chunkNo = rawDataObject.getChunkNo();

        if (totalChunks <= 0 || chunkNo <= 0 || chunkNo > totalChunks) {
            System.out.println("Invalid chunk metadata: totalChunks=" + totalChunks + " chunkNo=" + chunkNo);
            return null;
        }

        // ensure array exists
        chunkPartialData.compute(key, (k, existing) -> {
            if (existing == null || existing.length != totalChunks) {
                RawData[] arr = new RawData[totalChunks];
                if (existing != null) {
                    System.arraycopy(existing, 0, arr, 0, Math.min(existing.length, arr.length));
                }
                arr[chunkNo - 1] = rawDataObject;
                return arr;
            } else {
                existing[chunkNo - 1] = rawDataObject;
                return existing;
            }
        });

        // check completeness
        if (ifFull(key)) {
            RawData[] parts = chunkPartialData.remove(key);
            if (parts == null) return null;

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                for (RawData part : parts) {
                    if (part == null) {
                        // incomplete, shouldn't happen because of ifFull guard
                        return null;
                    }
                    String d = part.getData();
                    if (d != null) {
                        baos.write(d.getBytes(StandardCharsets.UTF_8));
                    }
                }
                byte[] fullBytes = baos.toByteArray();
                String fullJson = new String(fullBytes, StandardCharsets.UTF_8);
                return new Gson().fromJson(fullJson, Ticket.class);
            } catch (IOException | RuntimeException ex) {
                System.out.println("Error assembling chunks: " + ex.getMessage());
                return null;
            }
        }

        return null; // not yet complete
    }

    private static String makeChunkKey(RawData r) {
        String client = r.getClientId() == null ? "unknown" : r.getClientId();
        String req = r.getRequestType() == null ? "unknown" : r.getRequestType();
        return client + ":" + req;
    }

    public static synchronized boolean ifFull(String key) {
        RawData[] arr = chunkPartialData.get(key);
        if (arr == null) return false;
        for (RawData rd : arr) if (rd == null) return false;
        return true;
    }

    public static synchronized boolean putPartialTicketData(String key, RawData[] x) {
        if (key == null || x == null) return false;
        chunkPartialData.put(key, x);
        return true;
    }

    public static synchronized boolean deletePartialData(String key) {
        if (key == null) return false;
        return chunkPartialData.remove(key) != null;
    }

    /**
     * Convenience: get partial data array for a key (may be null).
     */
    public static synchronized RawData[] getPartialData(String key) {
        if (key == null) return null;
        return chunkPartialData.get(key);
    }

    public boolean isFullTicket(RawData rawDataObject) {
        return rawDataObject != null && rawDataObject.getChunkNo() == 1 && rawDataObject.getTotalChunks() == 1;
    }

    public boolean isUpdateRequest(RawData rawDataObject) {
        return rawDataObject != null && "update".equalsIgnoreCase(rawDataObject.getRequestType());
    }

    @Override
    public Ticket storeNewTicket(String reporter, String topic, String description, Type type, Priority priority) {
        int id = idGenerator.getAndIncrement();
        Ticket t = new Ticket(id, reporter, topic, description, type, priority);
        ticketData.put(id, t);
        return t;
    }

    @Override
    public void updateTicketStatus(int ticketId, Status newStatus)
            throws UnknownTicketException, IllegalStateException {
        Ticket t = ticketData.get(ticketId);
        if (t == null) throw new UnknownTicketException("Ticket id " + ticketId + " not found");
        if (newStatus == null) throw new IllegalStateException("Status must not be null");
        t.setStatus(newStatus);
    }

    @Override
    public List<Ticket> getAllTickets() {
        return new ArrayList<>(ticketData.values());
    }
}
