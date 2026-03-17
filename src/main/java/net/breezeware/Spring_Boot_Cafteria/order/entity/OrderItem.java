package net.breezeware.Spring_Boot_Cafteria.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;



@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="order_items")
public class OrderItem
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @JoinColumn(name="order_id",nullable = false)
    private long orderId;

    @NotBlank
    @OneToMany()
    @JoinColumn(name ="   food_item_id",nullable = false)
    private long foodIemId;


  @NotBlank
  @Column(name = "price",nullable = false)
  private Double price;

  @NotBlank
  @Column(name = "quantity",nullable = false)
  private  Long quantity;


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

