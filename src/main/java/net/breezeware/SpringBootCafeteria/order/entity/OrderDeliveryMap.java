package net.breezeware.SpringBootCafeteria.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * Entity representing the delivery details associated with a customer order.
 *
 * <p>Each order has at most one OrderDeliveryMap record (one-to-one relationship).
 * It captures the recipient's name, phone, and address at the time of order,
 * and tracks which delivery staff member has been assigned to fulfill the delivery.</p>
 *
 * <p>Timestamps are managed automatically via JPA lifecycle callbacks.</p>
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The order this delivery record is associated with.
     *
     * <p>One-to-one relationship; each order can have only one delivery record.</p>
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
     * <p>Nullable; set when admin or staff assigns a delivery person.</p>
     */
    @Column(name = "delivery_staff_id")
    private Long deliveryStaffId;

    /**
     * Timestamp when this delivery record was created.
     *
     * <p>Automatically set during persistence and not updatable.</p>
     */
    @Column(name = "created_on", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    /**
     * Timestamp when this delivery record was last updated.
     */
    @Column(name = "updated_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    /**
     * Lifecycle callback executed before persisting the entity.
     *
     * <p>Initializes both createdOn and updatedOn timestamps.</p>
     */
    @PrePersist
    protected void onCreate() {
        createdOn = new Date();
        updatedOn = new Date();
    }

    /**
     * Lifecycle callback executed before updating the entity.
     *
     * <p>Refreshes the updatedOn timestamp.</p>
     */
    @PreUpdate
    protected void onUpdate() {
        updatedOn = new Date();
    }
}
