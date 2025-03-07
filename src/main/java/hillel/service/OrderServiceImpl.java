package hillel.service;

import hillel.domain.model.Order;
import hillel.domain.model.Product;
import hillel.repo.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Scope("prototype")
@Transactional
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional
    @Override
    public Order saveOrder(Order order) {
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

        return orderRepository.save(order);
    }

    @Transactional
    @Override
    public Optional<Order> updateOrder(Order order) {
        Long id = order.getId();
        Optional<Order> byId = findById(id);
        if (byId.isPresent()) {
            Order orderToUpdate = byId.get();
            List<Product> products = (order.getProducts() != null) ? order.getProducts() : new ArrayList<>();;
            if (!products.isEmpty()) {
                for (Product product : products) {
                    product.setOrder(orderToUpdate);
                }
                orderToUpdate.setProducts(products);
                orderToUpdate.setTotalCost(calculateTotalCost(products));
            }

            return Optional.of(orderRepository.save(orderToUpdate));
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    private double calculateTotalCost(List<Product> products) {
        return products.stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }
}
