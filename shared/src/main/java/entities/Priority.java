package entities;

import java.io.Serializable;

/**
 * Enumeration to describe the Priority of a {@link Ticket}.
 *
 * Aligned with protobuf (rpc.ticketmanagement.TicketManagementProto.Priority).
 *
 * Values:
 * - UNKNOWN (maps to PRIORITY_UNKNOWN)
 * - LOW
 * - MEDIUM
 * - HIGH
 *
 * Backwards-compatible aliases (deprecated) for older naming.
 */
public enum Priority implements Serializable {
    UNKNOWN,
    LOW,
    MEDIUM,
    HIGH;

    private static final long serialVersionUID = 1L;

    /**
     * Backwards-compatible aliases for older code that used CRITICAL/MAJOR/MINOR.
     * Prefer the canonical names LOW/MEDIUM/HIGH.
     */
    @Deprecated public static final Priority CRITICAL = HIGH;
    @Deprecated public static final Priority MAJOR    = MEDIUM;
    @Deprecated public static final Priority MINOR    = LOW;

    public static Priority fromProto(rpc.ticketmanagement.TicketManagementProto.Priority proto) {
        if (proto == null) return UNKNOWN;
        switch (proto) {
            case LOW:
                return LOW;
            case MEDIUM:
                return MEDIUM;
            case HIGH:
                return HIGH;
            case PRIORITY_UNKNOWN:
            default:
                return UNKNOWN;
        }
    }

    public rpc.ticketmanagement.TicketManagementProto.Priority toProto() {
        switch (this) {
            case LOW:
                return rpc.ticketmanagement.TicketManagementProto.Priority.LOW;
            case MEDIUM:
                return rpc.ticketmanagement.TicketManagementProto.Priority.MEDIUM;
            case HIGH:
                return rpc.ticketmanagement.TicketManagementProto.Priority.HIGH;
            case UNKNOWN:
            default:
                return rpc.ticketmanagement.TicketManagementProto.Priority.PRIORITY_UNKNOWN;
        }
    }
}
