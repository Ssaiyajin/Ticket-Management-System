package entities;

import java.io.Serializable;
import java.util.Objects;

/**
 * Ticket representation.
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
        this(0, null, null, null, null, null, Status.NEW);
    }

    public Ticket(int id, String reporter, String topic, String description, Type type, Priority priority) {
        this(id, reporter, topic, description, type, priority, Status.NEW);
    }

    public Ticket(int id, String reporter, String topic, String description, Type type, Priority priority, Status status) {
        this.id = id;
        this.reporter = reporter;
        this.topic = topic;
        this.description = description;
        this.type = type;
        this.priority = priority;
        this.status = status == null ? Status.NEW : status;
    }

    /** Copy constructor */
    public Ticket(Ticket other) {
        Objects.requireNonNull(other, "other ticket must not be null");
        this.id = other.id;
        this.reporter = other.reporter;
        this.topic = other.topic;
        this.description = other.description;
        this.type = other.type;
        this.priority = other.priority;
        this.status = other.status;
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
        this.status = status;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Ticket #").append(id)
                .append(": ").append(topic)
                .append(" (reported by: ").append(reporter).append(")\n")
                .append("Status: ").append(status)
                .append("\t Type: ").append(type)
                .append("\t Priority: ").append(priority)
                .append("\nDescription:\n").append(description)
                .toString();
    }

    @Override
    public Ticket clone() {
        return new Ticket(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket)) return false;
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
