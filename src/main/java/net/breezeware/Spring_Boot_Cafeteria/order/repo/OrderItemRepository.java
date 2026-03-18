package net.breezeware.Spring_Boot_Cafeteria.order.repo;

import net.breezeware.Spring_Boot_Cafeteria.order.entity.Order;
import net.breezeware.Spring_Boot_Cafeteria.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
   List<OrderItem> findByOrderId(Long id);

}
