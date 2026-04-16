package net.breezeware.springbootcafeteria.order.entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.Instant;

/**
 * Entity representing the delivery details associated with a customer order.
 *
 *Each order has at most one OrderDeliveryMap record (one-to-one relationship).
 * It captures the recipient's name, phone, and address at the time of order,
 * and tracks which delivery staff member has been assigned to fulfill the delivery.
 *
 *Timestamps are managed automatically via JPA lifecycle callbacks.
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "order_delivery_map")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class OrderDeliveryMap {

    /**
     * Unique identifier for this delivery record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_delivery_map_seq")
    @SequenceGenerator(name = "order_delivery_map_seq", sequenceName = "order_delivery_map_seq", allocationSize = 1)
    private Long id;

    /**
     * The order this delivery record is associated with.
     *
     *One-to-one relationship; each order can have only one delivery record.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    @NonNull
    private Order order;

    /**
     * Recipient's full name for delivery.
     */
    @Column(nullable = false)
    @NonNull
    private String name;

    /**
     * Recipient's contact phone number for delivery.
     */
    @Column(nullable = false)
    @NonNull
    private String phone;

    /**
     * Delivery address for the order.
     */
    @Column(nullable = false)
    @NonNull
    private String address;

    /**
     * ID of the delivery staff member assigned to deliver this order.
     *
     *Nullable; set when admin or staff assigns a delivery person.
     */
    @Column(name = "delivery_staff_id")
    private Long deliveryStaffId;

    /**
     * Timestamp when this delivery record was created.
     *
     *Automatically set during persistence and not updatable.
     */
    @Column(name = "created_on", updatable = false)

    private Instant createdOn;

    /**
     * Timestamp when this delivery record was last updated.
     */
    @Column(name = "updated_on")

    private Instant updatedOn;

    /**
     * Lifecycle callback executed before persisting the entity.
     *
     *Initializes both createdOn and updatedOn timestamps.
     */
    @PrePersist
    protected void onCreate() {
        createdOn = Instant.now();
        updatedOn = Instant.now();
    }

    /**
     * Lifecycle callback executed before updating the entity.
     *
     *Refreshes the updatedOn timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedOn = Instant.now();
    }
}
