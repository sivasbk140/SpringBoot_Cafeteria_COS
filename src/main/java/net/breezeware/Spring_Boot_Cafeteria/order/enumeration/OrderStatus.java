package net.breezeware.Spring_Boot_Cafeteria.order.enumeration;

public enum OrderStatus {
    PLACED_ORDER,
    ORDER_CONFIRMED,
    ORDER_PREPARING,
    ASSIGNED_DELIVERY_STAFF,
    ORDER_DELIVERED,
    ORDER_CANCELLED;

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case PLACED_ORDER -> next == ORDER_CONFIRMED || next == ORDER_CANCELLED;
            case ORDER_CONFIRMED -> next == ORDER_PREPARING || next == ORDER_CANCELLED;
            case ORDER_PREPARING -> next == ASSIGNED_DELIVERY_STAFF || next == ORDER_CANCELLED;
            case ASSIGNED_DELIVERY_STAFF -> next == ORDER_DELIVERED || next == ORDER_CANCELLED;
            case ORDER_DELIVERED, ORDER_CANCELLED -> false;
        };
    }

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

}