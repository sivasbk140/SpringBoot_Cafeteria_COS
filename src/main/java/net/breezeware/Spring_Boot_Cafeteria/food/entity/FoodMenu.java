package net.breezeware.Spring_Boot_Cafeteria.food.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
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