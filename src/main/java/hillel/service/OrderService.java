package hillel.service;

import hillel.domain.dto.OrderDTO;
import hillel.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderDTO findById(Long id);
    List<OrderDTO> findAll();
    OrderDTO saveOrder(OrderDTO orderDTO);
    OrderDTO updateOrder(OrderDTO orderDTO);
    void deleteById(Long id);
}
