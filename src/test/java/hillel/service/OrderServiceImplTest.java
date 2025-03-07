package hillel.service;

import TestData.TestData;
import hillel.domain.dto.OrderDTO;
import hillel.domain.dto.ProductDTO;
import hillel.domain.mapper.OrderMapper;
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
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class OrderServiceImplTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;

    @MockitoBean
    private OrderRepository repository;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Test
    void findByIdTest_OK() {
        Long testId = 1L;

        when(repository.findById(testId)).thenReturn(Optional.of(TestData.ORDER1));
        OrderDTO result = orderService.findById(testId);

        assertNotNull(result);
        assertEquals(TestData.ORDER1.getId(), result.getId());
    }

    @Test
    void findByIdTest_NotFoundThrowsException() {
        Long testId = 1L;
        when(repository.findById(testId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> orderService.findById(testId));
    }

    @Test
    void findAllTest_OK() {
        List<Order> testList = new ArrayList<>();
        testList.add(TestData.ORDER1);
        testList.add(TestData.ORDER2);

        when(repository.findAll()).thenReturn(testList);
        List<OrderDTO> result = orderService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(testList.size(), result.size());
    }

    @Test
    void saveOrderTest_totalIsCountedAndDateSet() {
        Order testOrder = new Order();
        List<Product> products = TestData.generateProductList();
        testOrder.setProducts(products);

        OrderDTO testDTO = orderMapper.toOrderDTO(testOrder);
        orderService.saveOrder(testDTO);
        verify(repository).save(orderCaptor.capture());

        Order result = orderCaptor.getValue();

        assertNotNull(result);
        assertNotNull(result.getCreatedAt());
        assertEquals(1000.10, result.getTotalCost());
        assertEquals(products.size(), result.getProducts().size());
    }

    @Test
    void saveOrderTest_orderSetForProducts() {
        ProductDTO productDTO1 = ProductDTO.builder().id(1L).name("Test").price(100.0).build();
        ProductDTO productDTO2 = ProductDTO.builder().id(2L).name("Test2").price(200.0).build();
        List<ProductDTO> productDTOS = new ArrayList<>();
        productDTOS.add(productDTO1);
        productDTOS.add(productDTO2);
        OrderDTO testOrderDTO = OrderDTO.builder().id(3L).products(productDTOS).build();

        orderService.saveOrder(testOrderDTO);

        verify(repository).save(orderCaptor.capture());

        Order result = orderCaptor.getValue();
        List<Product> resultProducts = result.getProducts();
        Product resultProduct = resultProducts.get(0);
        Product resultProduct2 = resultProducts.get(1);

        assertNotNull(result);
        assertEquals(2, result.getProducts().size());
        assertEquals(3L, result.getId());
        assertNotNull(resultProduct.getOrder());
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
        OrderDTO testDTO = orderMapper.toOrderDTO(testOrder);

        when(repository.findById(testId)).thenReturn(Optional.of(TestData.ORDER1));
        when(repository.save(orderCaptor.capture())).thenReturn(TestData.ORDER1);

        orderService.updateOrder(testDTO);
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
        OrderDTO testDTO = orderMapper.toOrderDTO(testOrder);

        when(repository.findById(testId)).thenReturn(Optional.of(TestData.ORDER1));
        when(repository.save(orderCaptor.capture())).thenReturn(TestData.ORDER1);

        orderService.updateOrder(testDTO);

        Order result = orderCaptor.getValue();

        assertNotNull(result);
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void updateOrderTest_idNotFoundThrowsException() {
        Long testId = 1000L;
        Order testOrder = new Order();
        testOrder.setId(testId);
        OrderDTO testDTO = orderMapper.toOrderDTO(testOrder);

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> orderService.updateOrder(testDTO));
    }

    @Test
    void deleteByIdTest_OK() {
        Long testId = 1L;
        when(repository.findById(testId)).thenReturn(Optional.of(TestData.ORDER1));
        orderService.deleteById(testId);
        verify(repository).delete(TestData.ORDER1);
    }

    @Test
    void deleteByIdTest_NotFoundThrowsException() {
        Long testId = 100L;
        when(repository.findById(testId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> orderService.deleteById(testId));
    }
}