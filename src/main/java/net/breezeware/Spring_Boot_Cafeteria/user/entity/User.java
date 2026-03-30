package net.breezeware.Spring_Boot_Cafeteria.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import  net.breezeware.Spring_Boot_Cafeteria.user.enumeration.Role;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.breezeware.Spring_Boot_Cafeteria.order.entity.Order;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = {"orders", "deliveryDetails"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @NonNull
    @Column(nullable = false, length = 100)
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @NonNull
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Password is required")
    @NonNull
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role is required")
    @NonNull
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<net.breezeware.Spring_Boot_Cafeteria.order.entity.Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryDetail> deliveryDetails = new ArrayList<>();

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
    public void addOrder(Order order) {
        orders.add(order);
        order.setUser(this);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        order.setUser(null);
    }

    public void addDeliveryDetail(DeliveryDetail deliveryDetail) {
        deliveryDetails.add(deliveryDetail);
        deliveryDetail.setUser(this);
    }

    public void removeDeliveryDetail(DeliveryDetail deliveryDetail) {
        deliveryDetails.remove(deliveryDetail);
        deliveryDetail.setUser(null);
    }

    // Business logic
    public boolean isAdmin() {
        return role == Role.CUSTOMER;
    }

    public boolean isStaff() {
        return role == Role.STAFF;
    }

    public boolean isCustomer() {
        return role == Role.CUSTOMER;
    }
}