package net.breezeware.Spring_Boot_Cafeteria.order.service;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.Spring_Boot_Cafeteria.order.repo.OrderItemRepository;
import net.breezeware.Spring_Boot_Cafeteria.order.repo.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerOrderService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;






}
