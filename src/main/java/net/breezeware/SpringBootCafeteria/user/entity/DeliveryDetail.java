package net.breezeware.SpringBootCafeteria.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

/**
 * JPA entity representing the delivery contact information for a delivery staff member.
 * <p>
 * Stores the email, phone number, and location associated with a user who has the
 * {@link net.breezeware.SpringBootCafeteria.user.enumeration.Role#DELIVERY_STAFF} role.
 * Timestamps are managed automatically via {@code @PrePersist} and {@code @PreUpdate}.
 * </p>
 *
 * @see User
 */
@Entity
@Table(name = "delivery_details")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = "user")
public class DeliveryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    @NonNull
    private User user;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @NonNull
    @Column(nullable = false, length = 100)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    @NonNull
    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @NotBlank(message = "Location is required")
    @Size(min = 5, max = 500, message = "Location must be between 5-500 characters")
    @NonNull
    @Column(nullable = false, length = 500)
    private String location;

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
}