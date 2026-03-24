package net.breezeware.Spring_Boot_Cafeteria.order.repo;

import net.breezeware.Spring_Boot_Cafeteria.order.entity.OrderDeliveryMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderDeliveryMapRepository extends JpaRepository<OrderDeliveryMap, Long> {
    Optional<OrderDeliveryMap> findByOrderId(Long orderId);
}
