package hillel.domain.mapper;

import hillel.domain.dto.OrderDTO;
import hillel.domain.model.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = ProductMapper.class, componentModel = "spring")
public interface OrderMapper {
    OrderDTO toOrderDTO(Order order);
    Order toOrder(OrderDTO orderDTO);
    List<OrderDTO> toOrderDTOList(List<Order> orders);
    List<Order> toOrderList(List<OrderDTO> orderDTOs);
}
