package entities;

import java.io.Serializable;

/**
 * Enumeration to describe the Priority of a {@link Ticket} or
 * {@link TransferTicket}.
 * 
 * Possible Values:
 * <ul>
 * <li>{@code CRITICAL}</li>
 * <li>{@code MAJOR}</li>
 * <li>{@code MINOR}</li>
 * </ul>
 * 
 */
public enum Priority implements Serializable {
    CRITICAL, MAJOR, MINOR;

    private static final long serialVersionUID = 1L;

    /**
     * Case-insensitive parse. Returns null for null/unknown input.
     */
    public static Priority fromString(String s) {
        if (s == null) return null;
        try {
            return Priority.valueOf(s.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Convert from a protobuf-generated enum (or any Enum) by name.
     * Safe when you don't want a compile-time dependency on the generated type.
     */
    public static Priority fromProtoEnum(Enum<?> protoEnum) {
        if (protoEnum == null) return null;
        try {
            return Priority.valueOf(protoEnum.name());
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

    @Override
    public String toString() {
        return name();
    }
}
