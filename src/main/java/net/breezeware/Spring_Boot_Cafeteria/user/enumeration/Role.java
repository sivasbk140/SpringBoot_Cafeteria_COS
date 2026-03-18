package net.breezeware.Spring_Boot_Cafeteria.user.enumeration;

public enum Role {
    ADMIN,
    STAFF,
    CUSTOMER,
    DELIVERY_STAFF;

    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}