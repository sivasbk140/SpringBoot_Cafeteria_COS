package net.breezeware.springbootcafeteria.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;

/**
 * Entity representing the delivery contact details for a delivery staff member.
 *
 * Stores the email, phone number, and location associated with a user who holds the
 * {@link net.breezeware.springbootcafeteria.user.enumeration.Role#DELIVERY_STAFF} role.
 * Timestamps are managed automatically via JPA lifecycle hooks.
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "delivery_details")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = "user")
public class DeliveryDetail {

    /**
     * Unique identifier for this delivery detail record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "delivery_detail_seq")
    @SequenceGenerator(name = "delivery_detail_seq", sequenceName = "delivery_detail_seq", allocationSize = 1)
    private Long id;

    /**
     * The delivery staff user this record belongs to.
     *
     * Many-to-one relationship with User entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    @NonNull
    private User user;

    /**
     * Contact email address of the delivery staff member.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @NonNull
    @Column(nullable = false, length = 100)
    private String email;

    /**
     * 10-digit contact phone number of the delivery staff member.
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    @NonNull
    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    /**
     * Physical location or address of the delivery staff member.
     */
    @NotBlank(message = "Location is required")
    @Size(min = 5, max = 500, message = "Location must be between 5-500 characters")
    @NonNull
    @Column(nullable = false, length = 500)
    private String location;

    /**
     * Timestamp when this delivery detail was created.
     *
     * Automatically set during persistence and not updatable.
     */
    @Column(name = "created_on", updatable = false)

    private Instant createdOn;

    /**
     * Timestamp when this delivery detail was last updated.
     */
    @Column(name = "updated_on")

    private Instant updatedOn;

    /**
     * Lifecycle callback executed before persisting the entity.
     *
     * Initializes both createdOn and updatedOn timestamps.
     *
     */
    @PrePersist
    protected void onCreate() {
        createdOn = Instant.now();
        updatedOn = Instant.now();
    }

    /**
     * Lifecycle callback executed before updating the entity.
     *
     * Refreshes the updatedOn timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedOn = Instant.now();
    }
}