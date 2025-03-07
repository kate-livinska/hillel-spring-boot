package hillel.service;

import hillel.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Optional<Order> findById(Long id);
    List<Order> findAll();
    Order saveOrder(Order order);
    Optional<Order> updateOrder(Order order);
    void deleteById(Long id);
}
