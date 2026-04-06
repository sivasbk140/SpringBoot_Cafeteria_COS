package net.breezeware.SpringBootCafeteria.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import net.breezeware.SpringBootCafeteria.user.enumeration.Role;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.breezeware.SpringBootCafeteria.order.entity.Order;

/**
 * JPA entity representing a user of the cafeteria system.
 * <p>
 * A user can have one of four roles: ADMIN, STAFF, CUSTOMER, or DELIVERY_STAFF.
 * Customers are associated with orders; delivery staff are associated with delivery details.
 * The {@code createdOn} and {@code updatedOn} timestamps are managed automatically
 * via {@code @PrePersist} and {@code @PreUpdate} hooks.
 * </p>
 *
 * @see Role
 * @see net.breezeware.SpringBootCafeteria.order.entity.Order
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = {"orders", "deliveryDetails"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @NonNull
    @Column(nullable = false, length = 100)
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @NonNull
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Password is required")
    @NonNull
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role is required")
    @NonNull
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryDetail> deliveryDetails = new ArrayList<>();

    @Column(name = "created_on", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "updated_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;




    @PrePersist
    protected void onCreate() {
        createdOn = new Date();
        updatedOn = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = new Date();
    }

    // Helper methods

    /**
     * Adds an order to this user's order list and sets the back-reference on the order.
     *
     * @param order the order to associate with this user
     */
    public void addOrder(Order order) {
        orders.add(order);
        order.setUser(this);
    }

    /**
     * Removes an order from this user's order list and clears the back-reference on the order.
     *
     * @param order the order to disassociate from this user
     */
    public void removeOrder(Order order) {
        orders.remove(order);
        order.setUser(null);
    }

    /**
     * Adds a delivery detail record to this user and sets the back-reference.
     *
     * @param deliveryDetail the delivery detail to associate with this user
     */
    public void addDeliveryDetail(DeliveryDetail deliveryDetail) {
        deliveryDetails.add(deliveryDetail);
        deliveryDetail.setUser(this);
    }

    /**
     * Removes a delivery detail record from this user and clears the back-reference.
     *
     * @param deliveryDetail the delivery detail to disassociate from this user
     */
    public void removeDeliveryDetail(DeliveryDetail deliveryDetail) {
        deliveryDetails.remove(deliveryDetail);
        deliveryDetail.setUser(null);
    }

    // Business logic

    /**
     * Checks whether this user has the ADMIN role.
     *
     * @return {@code true} if the user's role is {@link Role#ADMIN}
     */
    public boolean isAdmin() {
        return role == Role.CUSTOMER;
    }

    /**
     * Checks whether this user has the STAFF role.
     *
     * @return {@code true} if the user's role is {@link Role#STAFF}
     */
    public boolean isStaff() {
        return role == Role.STAFF;
    }

    /**
     * Checks whether this user has the CUSTOMER role.
     *
     * @return {@code true} if the user's role is {@link Role#CUSTOMER}
     */
    public boolean isCustomer() {
        return role == Role.CUSTOMER;
    }
}