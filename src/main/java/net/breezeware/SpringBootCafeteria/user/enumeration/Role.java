package net.breezeware.SpringBootCafeteria.user.enumeration;

/**
 * Represents the roles available to users in the cafeteria system.
 * <p>
 * Each role determines which controllers and operations the user has access to.
 * </p>
 */
public enum Role {

    /** System administrator with full access to users, food items, menus, and orders. */
    ADMIN,

    /** Cafeteria kitchen staff who can view and update order statuses. */
    STAFF,

    /** End customer who can browse menus, manage a cart, and place orders. */
    CUSTOMER,

    /** Delivery staff who can view orders assigned to them and mark them as delivered. */
    DELIVERY_STAFF;


}