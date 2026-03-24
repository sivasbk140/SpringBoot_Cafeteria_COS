package net.breezeware.Spring_Boot_Cafeteria.order.enumeration;

public enum OrderStatus {
    PLACED_ORDER,
    ORDER_CONFIRMED,
    ORDER_PREPARING,
    ASSIGNED_DELIVERY_STAFF,
    ORDER_DELIVERED,
    ORDER_CANCELLED;

    public static OrderStatus fromString(String status) {
        try {
            return OrderStatus.valueOf(status.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // Customer can only cancel at early stages
    public boolean isCancellable() {
        return this == PLACED_ORDER || this == ORDER_CONFIRMED;
    }

    // Admin/Staff can cancel from any active status in emergencies
    public boolean isForceCancellable() {
        return this != ORDER_DELIVERED && this != ORDER_CANCELLED;
    }

    public boolean isActive() {
        return this == PLACED_ORDER ||
                this == ORDER_CONFIRMED ||
                this == ORDER_PREPARING ||
                this == ASSIGNED_DELIVERY_STAFF;
    }

    public boolean isCompleted() {
        return this == ORDER_DELIVERED;
    }

    public boolean isCancelled() {
        return this == ORDER_CANCELLED;
    }
}