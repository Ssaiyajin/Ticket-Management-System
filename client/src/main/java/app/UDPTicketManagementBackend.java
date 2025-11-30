package app;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import entities.Priority;
import entities.Ticket;
import entities.TicketException;
import entities.Type as EType;

/**
 * UDP-backed implementation of TicketManagementBackend.
 *
 * - Uses UDPConnManager to send JSON requests and parse JSON responses.
 * - Default host/port come from system properties `ticket.server.host` / `ticket.server.port`
 *   or fall back to localhost:1140.
 * - All network errors are wrapped as TicketException.
 */
public class UDPTicketManagementBackend implements TicketManagementBackend {

    private final UDPConnManager connection;
    private final Gson gson = new Gson();

    public UDPTicketManagementBackend() {
        String host = System.getProperty("ticket.server.host", "127.0.0.1");
        int port = 1140;
        try {
            port = Integer.parseInt(System.getProperty("ticket.server.port", "1140"));
        } catch (NumberFormatException ignored) {
        }
        this.connection = new UDPConnManager(host, port);
        try {
            this.connection.makeConnection();
        } catch (Exception e) {
            // Don't throw from ctor; callers will see errors when calling methods.
            System.out.println("Warning: failed to establish UDP connection: " + e.getMessage());
        }
    }

    @Override
    public void triggerShutdown() {
        try {
            // try well-known close/shutdown variants
            try {
                connection.getClass().getMethod("close").invoke(connection);
                return;
            } catch (ReflectiveOperationException ignored) {
            }
            try {
                connection.getClass().getMethod("shutdown").invoke(connection);
                return;
            } catch (ReflectiveOperationException ignored) {
            }
            try {
                connection.getClass().getMethod("closeConnection").invoke(connection);
                return;
            } catch (ReflectiveOperationException ignored) {
            }
        } catch (Exception e) {
            // best-effort - ignore
            System.out.println("Error while triggering connection shutdown: " + e.getMessage());
        }
    }

    @Override
    public Ticket createNewTicket(String reporter, String topic, String description, EType type,
            Priority priority) throws TicketException {
        if (reporter == null) reporter = "unknown";
        if (topic == null) topic = "";
        Ticket payloadTicket = new Ticket(0, reporter, topic, description, type, priority);
        RequestWrapper req = new RequestWrapper("create", payloadTicket);
        String reqJson = gson.toJson(req);
        String resp;
        try {
            resp = connection.sendData(reqJson, true);
        } catch (Exception e) {
            throw new TicketException("Failed to send create request", e);
        }
        if (resp == null || resp.isEmpty()) throw new TicketException("Empty response from server");
        try {
            ResponseTicketWrapper wrapper = gson.fromJson(resp, ResponseTicketWrapper.class);
            return wrapper == null ? null : wrapper.ticket;
        } catch (JsonSyntaxException jse) {
            throw new TicketException("Malformed response for create request", jse);
        }
    }

    @Override
    public List<Ticket> getAllTickets() throws TicketException {
        RequestWrapper req = new RequestWrapper("list", null);
        String reqJson = gson.toJson(req);
        String resp;
        try {
            resp = connection.sendData(reqJson, true);
        } catch (Exception e) {
            throw new TicketException("Failed to send list request", e);
        }
        if (resp == null || resp.isEmpty()) return Collections.emptyList();
        try {
            Type listType = new TypeToken<List<Ticket>>() {}.getType();
            return gson.fromJson(resp, listType);
        } catch (JsonSyntaxException jse) {
            throw new TicketException("Malformed response for list request", jse);
        }
    }

    @Override
    public Ticket getTicketById(int id) throws TicketException {
        RequestWrapper req = new RequestWrapper("get", id);
        String reqJson = gson.toJson(req);
        String resp;
        try {
            resp = connection.sendData(reqJson, true);
        } catch (Exception e) {
            throw new TicketException("Failed to send get request", e);
        }
        if (resp == null || resp.isEmpty()) return null;
        try {
            ResponseTicketWrapper wrapper = gson.fromJson(resp, ResponseTicketWrapper.class);
            return wrapper == null ? null : wrapper.ticket;
        } catch (JsonSyntaxException jse) {
            throw new TicketException("Malformed response for get request", jse);
        }
    }

    @Override
    public Ticket acceptTicket(int id) throws TicketException {
        return changeTicketStatus(id, "accept");
    }

    @Override
    public Ticket rejectTicket(int id) throws TicketException {
        return changeTicketStatus(id, "reject");
    }

    @Override
    public Ticket closeTicket(int id) throws TicketException {
        return changeTicketStatus(id, "close");
    }

    private Ticket changeTicketStatus(int id, String action) throws TicketException {
        RequestWrapper req = new RequestWrapper(action, id);
        String reqJson = gson.toJson(req);
        String resp;
        try {
            resp = connection.sendData(reqJson, true);
        } catch (Exception e) {
            throw new TicketException("Failed to send " + action + " request", e);
        }
        if (resp == null || resp.isEmpty()) return null;
        try {
            ResponseTicketWrapper wrapper = gson.fromJson(resp, ResponseTicketWrapper.class);
            return wrapper == null ? null : wrapper.ticket;
        } catch (JsonSyntaxException jse) {
            throw new TicketException("Malformed response for " + action + " request", jse);
        }
    }

    // simple request/response wrappers used by client/server protocol (JSON)
    private static class RequestWrapper {
        String requestType;
        Object payload;

        RequestWrapper(String requestType, Object payload) {
            this.requestType = requestType;
            this.payload = payload;
        }
    }

    private static class ResponseTicketWrapper {
        Ticket ticket;
    }
}

