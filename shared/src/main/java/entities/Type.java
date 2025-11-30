package entities;

import java.io.Serializable;

/**
 * Enumeration to describe the Type of a {@link Ticket} or
 * {@link TransferTicket}.
 *
 * Possible Values:
 * <ul>
 * <li>{@code TASK}</li>
 * <li>{@code ENHANCEMENT}</li>
 * <li>{@code BUG}</li>
 * <li>{@code QUESTION}</li>
 * </ul>
 */
public enum Type implements Serializable {

    TASK, ENHANCEMENT, BUG, QUESTION;

    private static final long serialVersionUID = 1L;

    /**
     * Case-insensitive parse. Returns null for null/unknown input.
     */
    public static Type fromString(String s) {
        if (s == null) return null;
        try {
            return Type.valueOf(s.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Convert from a protobuf-generated enum (or any Enum) by name.
     * Use when you cannot or don't want to reference the generated proto enum
     * type at compile time.
     *
     * Example:
     *   // protoEnum is rpc.ticketmanagement.TicketManagementProto.Type.BUG
     *   Type t = Type.fromProtoEnum(protoEnum);
     */
    public static Type fromProtoEnum(Enum<?> protoEnum) {
        if (protoEnum == null) return null;
        try {
            return Type.valueOf(protoEnum.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Convert this enum to a target enum class (e.g. the generated proto enum).
     * Returns null if target class is null or doesn't contain a matching constant.
     *
     * Example:
     *   rpc.ticketmanagement.TicketManagementProto.Type proto =
     *       myType.toProtoEnum(rpc.ticketmanagement.TicketManagementProto.Type.class);
     */
    public <E extends Enum<E>> E toProtoEnum(Class<E> protoEnumClass) {
        if (protoEnumClass == null) return null;
        try {
            return Enum.valueOf(protoEnumClass, this.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return name();
    }
}
