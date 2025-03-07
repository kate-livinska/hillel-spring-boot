package hillel.service;

import TestData.TestData;
import hillel.domain.model.Order;
import hillel.domain.model.Product;
import hillel.repo.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class OrderServiceImplTest {
    @Autowired
    private OrderService orderService;

    @MockitoBean
    private OrderRepository repository;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Test
    void findByIdTest_OK() {
        Long testId = 1L;

        when(repository.findById(1L)).thenReturn(Optional.of(TestData.ORDER1));

        Optional<Order> result = orderService.findById(testId);
        assertTrue(result.isPresent());
        assertEquals(TestData.ORDER1, result.get());
    }

    @Test
    void findAllTest_OK() {
        List<Order> testList = new ArrayList<>();
        testList.add(TestData.ORDER1);
        testList.add(TestData.ORDER2);

        when(repository.findAll()).thenReturn(testList);
        List<Order> result = orderService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(testList.size(), result.size());
    }

    @Test
    void saveOrderTest_TotalIsCountedAndDateSet() {
        Order testOrder = new Order();
        List<Product> products = TestData.generateProductList();
        testOrder.setProducts(products);

        orderService.saveOrder(testOrder);
        verify(repository).save(orderCaptor.capture());

        Order result = orderCaptor.getValue();

        assertNotNull(result);
        assertNotNull(result.getCreatedAt());
        assertEquals(1000.10, result.getTotalCost());
        assertEquals(products, result.getProducts());
    }

    @Test
    void saveOrderTest_orderSetForProducts() {
        Product testProduct = new Product();
        testProduct.setName("testProduct");
        testProduct.setPrice(100.0);
        List<Product> products = TestData.generateProductList();
        products.add(testProduct);

        Order testOrder = Order.builder()
                .id(3L)
                .createdAt(LocalDateTime.now())
                .products(products).build();

        orderService.saveOrder(testOrder);

        verify(repository).save(orderCaptor.capture());

        Order result = orderCaptor.getValue();
        List<Product> resultProducts = result.getProducts();
        Product resultProduct = resultProducts.get(0);
        Product resultProduct2 = resultProducts.get(resultProducts.size() - 1);

        assertNotNull(result);
        assertEquals(products.size(), result.getProducts().size());
        assertEquals("testProduct", resultProduct2.getName());
        assertNotNull(resultProduct2.getOrder());
        assertEquals(3L, resultProduct2.getOrder().getId());
        assertEquals(3L, resultProduct.getOrder().getId());
    }

    @Test
    void updateOrderTest_createdAtNotChanged() {
        Long testId = 1L;
        Order testOrder = new Order();
        testOrder.setId(testId);
        testOrder.setCreatedAt(LocalDateTime.of(2025, 3, 4, 15, 30, 45, 0));

        when(repository.findById(1L)).thenReturn(Optional.of(TestData.ORDER1));
        when(repository.save(orderCaptor.capture())).thenReturn(TestData.ORDER1);

        orderService.updateOrder(testOrder);
        Order result = orderCaptor.getValue();

        assertNotNull(result);
        assertNotNull(result.getCreatedAt());
        assertNotEquals(testOrder.getCreatedAt(), result.getCreatedAt());
    }

    @Test
    void updateOrderTest_createdAtIsNullNotUpdated() {
        Long testId = 1L;
        Order testOrder = new Order();
        testOrder.setId(testId);

        when(repository.findById(1L)).thenReturn(Optional.of(TestData.ORDER1));
        when(repository.save(orderCaptor.capture())).thenReturn(TestData.ORDER1);

        orderService.updateOrder(testOrder);

        Order result = orderCaptor.getValue();

        assertNotNull(result);
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void updateOrderTest_idNotFoundReturnsEmptyOptional() {
        Long testId = 1000L;
        Order testOrder = new Order();
        testOrder.setId(testId);

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Order> result = orderService.updateOrder(testOrder);

        assertFalse(result.isPresent());
    }

    @Test
    void deleteByIdTest_repoDeleteByIdIsCalled() {
        orderService.deleteById(1L);
        verify(repository).deleteById(1L);
    }
}