package net.breezeware.SpringBootCafeteria.food.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing the mapping between a {@link FoodMenu} and a {@link FoodItem}.
 *
 * <p>Acts as a junction table ({@code food_menu_items_map}) linking food items to menus.
 * Tracks an individual availability flag for each item within its menu context.
 * When a new mapping is persisted, {@code isAvailable} defaults to {@code true}.</p>
 *
 * @author Siva
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "food_menu_items_map")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = {"menu", "foodItem"})
public class FoodMenuItemMap {

    /**
     * Unique identifier for this menu-item mapping.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The food menu this mapping belongs to.
     *
     * <p>Many-to-one relationship with FoodMenu entity.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    @NotNull(message = "Menu is required")
    @NonNull
    private FoodMenu menu;

    /**
     * The food item linked to the menu via this mapping.
     *
     * <p>Many-to-one relationship with FoodItem entity.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    @NotNull(message = "Food item is required")
    @NonNull
    private FoodItem foodItem;

    /**
     * Indicates whether this food item is available within this menu.
     *
     * <p>Defaults to {@code true} on creation. Can be toggled independently
     * per menu without affecting the global food item record.</p>
     */
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    /**
     * Timestamp when this mapping was created.
     *
     * <p>Automatically set during persistence and not updatable.</p>
     */
    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    /**
     * Timestamp when this mapping was last updated.
     */
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    /**
     * Lifecycle callback executed before persisting the entity.
     *
     * <p>Initializes createdOn and updatedOn timestamps, and defaults
     * {@code isAvailable} to {@code true} if not explicitly set.</p>
     */
    @PrePersist
    protected void onCreate() {
        createdOn = LocalDateTime.now();
        updatedOn = LocalDateTime.now();
        if (isAvailable == null) {
            isAvailable = true;
        }
    }

    /**
     * Lifecycle callback executed before updating the entity.
     *
     * <p>Refreshes the updatedOn timestamp.</p>
     */
    @PreUpdate
    protected void onUpdate() {
        updatedOn = LocalDateTime.now();
    }


}