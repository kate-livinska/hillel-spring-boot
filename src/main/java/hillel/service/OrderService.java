package hillel.service;

import hillel.domain.dto.OrderDTO;
import java.util.List;

public interface OrderService {
    OrderDTO findById(Long id);
    List<OrderDTO> findAll();
    OrderDTO saveOrder(OrderDTO orderDTO);
    OrderDTO updateOrder(OrderDTO orderDTO);
    void deleteById(Long id);
}
