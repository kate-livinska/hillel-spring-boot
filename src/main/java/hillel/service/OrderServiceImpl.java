package hillel.service;

import hillel.domain.dto.OrderDTO;
import hillel.domain.mapper.OrderMapper;
import hillel.domain.model.Order;
import hillel.domain.model.Product;
import hillel.repo.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Scope("prototype")
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private static final String ERROR = "Order with id: %d not found";

    @Override
    public OrderDTO findById(Long id) {
        try {
            Optional<Order> byId = orderRepository.findById(id);
            Order order = byId.orElseThrow(NoSuchElementException::new);
            return orderMapper.toOrderDTO(order);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format(ERROR, id));
        }
    }

    @Override
    public List<OrderDTO> findAll() {
        List<Order> all = orderRepository.findAll();
        return orderMapper.toOrderDTOList(all);
    }

    @Transactional
    @Override
    public OrderDTO saveOrder(OrderDTO orderDTO) {
        Order order = orderMapper.toOrder(orderDTO);
        List<Product> products = order.getProducts();
        if (products != null) {
            for (Product product : products) {
                product.setOrder(order);
            }
            order.setTotalCost(calculateTotalCost(products));
        }

        if (order.getCreatedAt() == null) {
            order.setCreatedAt(LocalDateTime.now());
        }

        Order save = orderRepository.save(order);
        return orderMapper.toOrderDTO(save);
    }

    @Transactional
    @Override
    public OrderDTO updateOrder(OrderDTO order) {
        Long id = order.getId();
        Order orderWithUpdates = orderMapper.toOrder(order);
        try {
            Optional<Order> byId = orderRepository.findById(id);

            Order orderToUpdate = byId.orElseThrow(NoSuchElementException::new);
            List<Product> products =
                    (orderWithUpdates.getProducts() != null) ? orderWithUpdates.getProducts() : new ArrayList<>();
            if (!products.isEmpty()) {
                for (Product product : products) {
                    product.setOrder(orderToUpdate);
                }
                orderToUpdate.setProducts(products);
                orderToUpdate.setTotalCost(calculateTotalCost(products));
            }
            Order saved = orderRepository.save(orderToUpdate);
            return orderMapper.toOrderDTO(saved);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format(ERROR, id));
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            Optional<Order> byId = orderRepository.findById(id);
            Order order = byId.orElseThrow(NoSuchElementException::new);
            orderRepository.delete(order);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format(ERROR, id));
        }
    }

    private double calculateTotalCost(List<Product> products) {
        return products.stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }
}
