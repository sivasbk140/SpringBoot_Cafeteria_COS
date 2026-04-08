package net.breezeware.springbootcafeteria.order.dao;

import net.breezeware.springbootcafeteria.order.entity.OrderDeliveryMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderDeliveryMapRepository extends JpaRepository<OrderDeliveryMap, Long> {
    Optional<OrderDeliveryMap> findByOrderId(Long orderId);
    List<OrderDeliveryMap> findByDeliveryStaffId(Long deliveryStaffId);
}
