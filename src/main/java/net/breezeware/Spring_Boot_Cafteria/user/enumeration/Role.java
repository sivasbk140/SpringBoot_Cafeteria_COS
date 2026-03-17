package net.breezeware.Spring_Boot_Cafteria.user.enumeration;


public enum Role {
    ADMIN,
    STAFF,
    CUSTOMER,
    DELIVERY_STAFF;

    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: Invalid day value in DB → '" + role + "'");
            return null;
        }
    }
}
