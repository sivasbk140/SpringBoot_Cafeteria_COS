package net.breezeware.SpringBootCafeteria.food.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA entity representing the many-to-many mapping between a {@link FoodMenu} and a {@link FoodItem}.
 * <p>
 * Acts as a junction table ({@code food_menu_items_map}) that links food items to menus
 * and tracks the individual availability flag for each item within a menu.
 * When a new mapping is persisted, {@code isAvailable} defaults to {@code true}.
 * </p>
 *
 * @see FoodMenu
 * @see FoodItem
 */
@Entity
@Table(name = "food_menu_items_map")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = {"menu", "foodItem"})
public class FoodMenuItemMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    @NotNull(message = "Menu is required")
    @NonNull
    private FoodMenu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    @NotNull(message = "Food item is required")
    @NonNull
    private FoodItem foodItem;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @PrePersist
    protected void onCreate() {
        createdOn = LocalDateTime.now();
        updatedOn = LocalDateTime.now();
        if (isAvailable == null) {
            isAvailable = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = LocalDateTime.now();
    }


}