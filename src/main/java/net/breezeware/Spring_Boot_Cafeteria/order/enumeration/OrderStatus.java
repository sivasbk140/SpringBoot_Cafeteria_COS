package net.breezeware.Spring_Boot_Cafeteria.order.enumeration;

public enum OrderStatus {
    PLACED_ORDER,
    WAITING_FOR_DELIVERY,
    PENDING_DELIVERY,
    ORDER_DELIVERED,
    ORDER_CANCELLED;

    public static OrderStatus fromString(String status) {
        try {
            return OrderStatus.valueOf(status.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


    public boolean isCancellable() {
        return this == PLACED_ORDER;
    }

    public boolean isActive() {
        return this == PLACED_ORDER ||
                this == WAITING_FOR_DELIVERY ||
                this == PENDING_DELIVERY;
    }

    public boolean isCompleted() {
        return this == ORDER_DELIVERED;
    }

    public boolean isCancelled() {
        return this == ORDER_CANCELLED;
    }
}