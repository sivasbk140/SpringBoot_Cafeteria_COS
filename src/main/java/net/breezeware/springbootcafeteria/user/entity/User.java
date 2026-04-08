package net.breezeware.springbootcafeteria.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import net.breezeware.springbootcafeteria.user.enumeration.Role;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.breezeware.springbootcafeteria.order.entity.Order;

/**
 * Entity representing a user of the cafeteria system.
 *
 * A user can hold one of four roles: ADMIN, STAFF, CUSTOMER, or DELIVERY_STAFF.
 * Customers are associated with orders; delivery staff are associated with delivery details.
 * Timestamps are managed automatically via JPA lifecycle hooks.
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = {"orders", "deliveryDetails"})
public class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Full name of the user.
     */
    @NotBlank(message = "Name is required")
    @NonNull
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Email address of the user. Must be unique across all users.
     */
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @NonNull
    @Column(nullable = false, unique = true, length = 100)
    @Schema(example = "",description = "")
    private String email;

    /**
     * Hashed or plain-text password for authentication.
     */
    @NotBlank(message = "Password is required")
    @NonNull
    @Column(nullable = false)
    private String password;

    /**
     * Role assigned to the user (ADMIN, STAFF, CUSTOMER, or DELIVERY_STAFF).
     *
     * Stored as a string representation of {@link Role} enum.
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role is required")
    @NonNull
    @Column(nullable = false)
    private Role role;

    /**
     * List of orders placed by this user.
     *
     * One-to-many relationship with Order entity.
     * Cascade and orphan removal are enabled.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    /**
     * List of delivery detail records associated with this user.
     *
     * One-to-many relationship with DeliveryDetail entity.
     * Relevant for users with the DELIVERY_STAFF role.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryDetail> deliveryDetails = new ArrayList<>();

    /**
     * Timestamp when the user was created.
     *
     * Automatically set during persistence and not updatable.
     */
    @Column(name = "created_on", updatable = false)

    private Instant createdOn;

    /**
     * Timestamp when the user record was last updated.
     */
    @Column(name = "updated_on")

    private Instant updatedOn;

    /**
     * Lifecycle callback executed before persisting the entity.
     *
     * Initializes both createdOn and updatedOn timestamps.
     */
    @PrePersist
    protected void onCreate() {
        createdOn =  Instant.now();
        updatedOn =  Instant.now();
    }

    /**
     * Lifecycle callback executed before updating the entity.
     *
     * Refreshes the updatedOn timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedOn =  Instant.now();
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
     * Adds a delivery detail record to this user and sets the back-reference.
     *
     * @param deliveryDetail the delivery detail to associate with this user
     */
    public void addDeliveryDetail(DeliveryDetail deliveryDetail) {
        deliveryDetails.add(deliveryDetail);
        deliveryDetail.setUser(this);
    }

}