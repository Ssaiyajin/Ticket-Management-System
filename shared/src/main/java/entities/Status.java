package entities;

import java.io.Serializable;

/**
 * Enumeration to describe the Status of a {@link Ticket} or
 * {@link TransferTicket}.
 *
 * Possible Values:
 * <ul>
 * <li>{@code NEW}</li>
 * <li>{@code OPEN} (alias / semantic equivalent of NEW)</li>
 * <li>{@code ACCEPTED}</li>
 * <li>{@code IN_PROGRESS}</li>
 * <li>{@code RESOLVED}</li>
 * <li>{@code REJECTED}</li>
 * <li>{@code CLOSED}</li>
 * </ul>
 */
public enum Status implements Serializable {
    NEW,
    OPEN,           // kept for UI/backwards-compatibility checks
    ACCEPTED,
    IN_PROGRESS,
    RESOLVED,
    REJECTED,
    CLOSED;

    private static final long serialVersionUID = 1L;

    /**
     * Case-insensitive parse. Returns null for null/unknown input.
     * Accepts old/new names like "open" and "new".
     */
    public static Status fromString(String s) {
        if (s == null) return null;
        String key = s.trim().toUpperCase();
        // Accept "OPEN" as its own constant; historically NEW was used as alias.
        try {
            return Status.valueOf(key);
        } catch (IllegalArgumentException e) {
            // Unknown token
            return null;
        }
    }

    /**
     * Convert from a protobuf-generated enum (or any Enum) by name.
     * Safe when you don't want a compile-time dependency on the generated type.
     */
    public static Status fromProtoEnum(Enum<?> protoEnum) {
        if (protoEnum == null) return null;
        try {
            return Status.valueOf(protoEnum.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Convert this enum to a target enum class (e.g. the generated proto enum).
     * Returns null if protoEnumClass is null or doesn't contain a matching constant.
     */
    public <E extends Enum<E>> E toProtoEnum(Class<E> protoEnumClass) {
        if (protoEnumClass == null) return null;
        try {
            return Enum.valueOf(protoEnumClass, this.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Whether this status is terminal (no further state transitions expected).
     */
    public boolean isTerminal() {
        return this == CLOSED || this == REJECTED;
    }

    @Override
    public String toString() {
        return name();
    }
}
