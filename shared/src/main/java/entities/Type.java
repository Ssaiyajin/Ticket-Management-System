package entities;

import java.io.Serializable;

/**
 * Enumeration to describe the Type of a {@link Ticket}.
 *
 * This enum is aligned with the protobuf definition (rpc.ticketmanagement.TicketManagementProto.Type).
 *
 * Values:
 * - UNKNOWN (maps to TYPE_UNKNOWN)
 * - BUG
 * - FEATURE
 * - TASK
 */
public enum Type implements Serializable {

    UNKNOWN, BUG, FEATURE, TASK;

    private static final long serialVersionUID = 1L;

    /**
     * Convert from protobuf enum to this enum.
     */
    public static Type fromProto(rpc.ticketmanagement.TicketManagementProto.Type proto) {
        if (proto == null) return UNKNOWN;
        switch (proto) {
            case BUG:
                return BUG;
            case FEATURE:
                return FEATURE;
            case TASK:
                return TASK;
            case TYPE_UNKNOWN:
            default:
                return UNKNOWN;
        }
    }

    /**
     * Convert this enum to the protobuf enum.
     */
    public rpc.ticketmanagement.TicketManagementProto.Type toProto() {
        switch (this) {
            case BUG:
                return rpc.ticketmanagement.TicketManagementProto.Type.BUG;
            case FEATURE:
                return rpc.ticketmanagement.TicketManagementProto.Type.FEATURE;
            case TASK:
                return rpc.ticketmanagement.TicketManagementProto.Type.TASK;
            case UNKNOWN:
            default:
                return rpc.ticketmanagement.TicketManagementProto.Type.TYPE_UNKNOWN;
        }
    }
}
