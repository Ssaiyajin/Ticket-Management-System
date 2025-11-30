package entities;

import java.io.Serializable;
import java.util.Objects;

/**
 * Ticket representation used across server/client/shared code.
 */
public class Ticket implements Serializable, Cloneable {

    private static final long serialVersionUID = -6979364632920616224L;

    private int id;
    private String reporter;
    private String topic;
    private String description;
    private Type type;
    private Priority priority;
    private Status status;

    public Ticket() {
        this.status = Status.NEW;
    }

    public Ticket(int id, String reporter, String topic, String description, Type type, Priority priority) {
        this(id, reporter, topic, description, type, priority, Status.NEW);
    }

    public Ticket(int id, String reporter, String topic, String description, Type type, Priority priority,
                  Status status) {
        this.id = id;
        this.reporter = reporter;
        this.topic = topic;
        this.description = description;
        this.type = type;
        this.priority = priority;
        this.status = status == null ? Status.NEW : status;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getReporter() {
        return reporter;
    }

    public Status getStatus() {
        return status;
    }

    public String getTopic() {
        return topic;
    }

    public Type getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setStatus(Status status) {
        this.status = status == null ? Status.NEW : status;
    }

    @Override
    public String toString() {
        return "Ticket #" + id + ": " + topic + " (reported by: " + reporter + ")\n" +
               "Status: " + status + "\t Type: " + type + "\t Priority: " + priority + "\n" +
               "Description:\n" + description;
    }

    @Override
    public Ticket clone() {
        try {
            Ticket cloned = (Ticket) super.clone();
            // Strings and enums are immutable; shallow copy is sufficient
            return cloned;
        } catch (CloneNotSupportedException e) {
            // should not happen since we implement Cloneable
            throw new AssertionError(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ticket ticket = (Ticket) o;
        return id == ticket.id &&
               Objects.equals(reporter, ticket.reporter) &&
               Objects.equals(topic, ticket.topic) &&
               Objects.equals(description, ticket.description) &&
               type == ticket.type &&
               priority == ticket.priority &&
               status == ticket.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reporter, topic, description, type, priority, status);
    }
}
