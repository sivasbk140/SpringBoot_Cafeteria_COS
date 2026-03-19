package net.breezeware.Spring_Boot_Cafeteria.food.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
@Slf4j
@Entity
@Table(name = "food_item")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @NonNull
    @Column(nullable = false, length = 100)
    private String name;

    @Positive(message = "Price must be positive")
    @NonNull
    @Column(nullable = false)
    private Double price;

    @Min(value = 0, message = "Quantity cannot be negative")
    @NonNull
    @Column(nullable = false)
    private Integer quantity;

    @NonNull
    @Column(length = 50)
    private String category;

    @Column(length = 500)
    private String description;

    @Column(name = "created_on", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "updated_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    public FoodItem(String name, Double price, Integer quantity, String category, String description) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.description = description;
    }

    @PrePersist
    protected void onCreate() {
        createdOn = new Date();
        updatedOn = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = new Date();
    }

    // Business logic
    public boolean isAvailable() {
        return quantity > 0;
    }

    public boolean hasStock(int requestedQuantity) {
        return quantity >= requestedQuantity;
    }

    public void reduceStock(int amount) {
        if (amount > quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        this.quantity -= amount;
    }

    public void restoreStock(int amount) {
        this.quantity += amount;
    }

}