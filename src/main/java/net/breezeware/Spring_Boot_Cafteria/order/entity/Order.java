package net.breezeware.Spring_Boot_Cafteria.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.breezeware.Spring_Boot_Cafteria.order.enumeration.OrderStatus;
import net.breezeware.Spring_Boot_Cafteria.user.entity.User;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_table")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotBlank
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private OrderStatus status;


    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<OrderItem> items = new ArrayList<>();


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