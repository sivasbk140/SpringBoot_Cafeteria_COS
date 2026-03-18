package net.breezeware.Spring_Boot_Cafeteria.user.entity;

public enum Role {
    ADMIN,
    STAFF,
    CUSTOMER,
    DELIVERYSTAFF;

    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}