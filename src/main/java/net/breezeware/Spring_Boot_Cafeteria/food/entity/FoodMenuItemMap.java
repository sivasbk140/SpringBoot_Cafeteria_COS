package net.breezeware.Spring_Boot_Cafeteria.food.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

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
    private net.breezeware.Spring_Boot_Cafeteria.food.entity.FoodMenu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    @NotNull(message = "Food item is required")
    @NonNull
    private net.breezeware.Spring_Boot_Cafeteria.food.entity.FoodItem foodItem;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

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
        if (isAvailable == null) {
            isAvailable = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = new Date();
    }

    // Business logic
    public void toggleAvailability() {
        this.isAvailable = !this.isAvailable;
    }

    public void makeAvailable() {
        this.isAvailable = true;
    }

    public void makeUnavailable() {
        this.isAvailable = false;
    }
}