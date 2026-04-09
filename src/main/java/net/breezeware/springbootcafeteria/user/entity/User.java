package net.breezeware.springbootcafeteria.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import net.breezeware.springbootcafeteria.user.enumeration.Role;

import java.util.ArrayList;
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
    @Schema (description = "name",example = "siva")
    private String name;

    /**
     * Email address of the user. Must be unique across all users.
     */
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @NonNull
    @Column(nullable = false, unique = true, length = 100)
    @Schema (description = "emailId",example = "siva@gmail.com")
    private String email;

    /**
     * Hashed or plain-text password for authentication.
     */
    @NotBlank(message = "Password is required")
    @NonNull
    @Column(nullable = false)
    @Schema (description = "password",example = "siva@123")
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
    @Schema (description = "role of the user")
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

}