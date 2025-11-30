package entities;

import java.io.Serializable;

/**
 * Enumeration to describe the Status of a {@link Ticket} or {@link TransferTicket}.
 *
 * This enum is aligned with the protobuf definition (rpc.ticketmanagement.TicketManagementProto.Status).
 *
 * Values:
 * - UNKNOWN (maps to STATUS_UNKNOWN)
 * - OPEN
 * - IN_PROGRESS
 * - RESOLVED
 * - CLOSED
 *
 * Compatibility: some existing code used Status.NEW; a deprecated alias NEW is provided and maps to OPEN.
 */
public enum Status implements Serializable {
    UNKNOWN,
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED;

    private static final long serialVersionUID = 1L;

    /**
     * Backwards-compatible alias used by older code. Prefer {@link #OPEN}.
     */
    @Deprecated
    public static final Status NEW = OPEN;

    /**
     * Convert from protobuf enum to this enum.
     */
    public static Status fromProto(rpc.ticketmanagement.TicketManagementProto.Status proto) {
        if (proto == null) return UNKNOWN;
        switch (proto) {
            case OPEN:
                return OPEN;
            case IN_PROGRESS:
                return IN_PROGRESS;
            case RESOLVED:
                return RESOLVED;
            case CLOSED:
                return CLOSED;
            case STATUS_UNKNOWN:
            default:
                return UNKNOWN;
        }
    }

    /**
     * Convert this enum to the protobuf enum.
     */
    public rpc.ticketmanagement.TicketManagementProto.Status toProto() {
        switch (this) {
            case OPEN:
                return rpc.ticketmanagement.TicketManagementProto.Status.OPEN;
            case IN_PROGRESS:
                return rpc.ticketmanagement.TicketManagementProto.Status.IN_PROGRESS;
            case RESOLVED:
                return rpc.ticketmanagement.TicketManagementProto.Status.RESOLVED;
            case CLOSED:
                return rpc.ticketmanagement.TicketManagementProto.Status.CLOSED;
            case UNKNOWN:
            default:
                return rpc.ticketmanagement.TicketManagementProto.Status.STATUS_UNKNOWN;
        }
    }
}
