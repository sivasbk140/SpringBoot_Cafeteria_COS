package net.breezeware.SpringBootCafeteria.order.repo;

import net.breezeware.SpringBootCafeteria.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
   List<OrderItem> findByOrderId(Long id);

}
