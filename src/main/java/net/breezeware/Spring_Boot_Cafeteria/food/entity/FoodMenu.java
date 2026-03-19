package net.breezeware.Spring_Boot_Cafeteria.food.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "food_menu")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = {"menuItems", "availabilities"})
public class FoodMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category is required")
    @NonNull
    @Column(nullable = false, length = 50)
    private String category;  // BREAKFAST, LUNCH, DINNER

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodMenuItemMap> menuItems = new ArrayList<>();

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailabilityMap> availabilities = new ArrayList<>();

    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @PrePersist
    protected void onCreate() {
        createdOn = LocalDateTime.now();
        updatedOn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = LocalDateTime.now();
    }

    // Helper methods
    public void addMenuItem(FoodMenuItemMap menuItem) {
        menuItems.add(menuItem);
        menuItem.setMenu(this);
    }

    public void removeMenuItem(FoodMenuItemMap menuItem) {
        menuItems.remove(menuItem);
        menuItem.setMenu(null);
    }

    public void addAvailability(AvailabilityMap availability) {
        availabilities.add(availability);
        availability.setMenu(this);
    }

    public void removeAvailability(AvailabilityMap availability) {
        availabilities.remove(availability);
        availability.setMenu(null);
    }
}