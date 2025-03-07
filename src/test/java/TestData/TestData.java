package TestData;

import hillel.domain.dto.OrderDTO;
import hillel.domain.model.Order;
import hillel.domain.model.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestData {
    public static final Order ORDER1 = new Order(1L, 1000.10, LocalDateTime.now(), new ArrayList<>());
    public static final Order ORDER2 = new Order(2L, 200.0, LocalDateTime.now(), new ArrayList<>());
    public static final Product PRODUCT1 = new Product(1L, "Laptop", 800.00, ORDER1);
    public static final Product PRODUCT2 = new Product(2L, "Mouse", 50.10, ORDER1);
    public static final Product PRODUCT3 = new Product(3L, "Keyboard", 150.00, ORDER1);

    public static final OrderDTO ORDER_DTO = new OrderDTO(1L, 1000.10, LocalDateTime.now(), new ArrayList<>());

    public static List<Product> generateProductList() {
        List<Product> list = new ArrayList<>();
        list.add(PRODUCT1);
        list.add(PRODUCT2);
        list.add(PRODUCT3);
        return list;
    }
}
