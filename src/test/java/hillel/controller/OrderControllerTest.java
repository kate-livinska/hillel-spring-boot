package hillel.controller;

import TestData.TestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import hillel.domain.dto.OrderDTO;
import hillel.service.OrderService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;


@WebMvcTest(OrderController.class)
@ExtendWith(SpringExtension.class)
class OrderControllerTest {
    @Autowired
    OrderController orderController;

    @MockitoBean
    OrderService orderService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Test
    void findByIdTest_returnsOK() throws Exception {
        Long id = 1L;
        when(orderService.findById(id)).thenReturn(TestData.ORDER_DTO);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void findByIdTest_returnsNotFound() throws Exception {
        when(orderService.findById(anyLong())).thenThrow(new NoSuchElementException(""));

        mockMvc.perform(get("/orders/100"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllTest_returnsOK() throws Exception {
        when(orderService.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createOrderTest_returnsCreated() throws Exception {
        when(orderService.saveOrder(any())).thenReturn(TestData.ORDER_DTO);

        String reqBody = objectMapper.writeValueAsString(TestData.ORDER_DTO);

        mockMvc.perform(
                        post("/orders")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateOrderTest_returnsOK() throws Exception {
        when(orderService.updateOrder(any())).thenReturn(TestData.ORDER_DTO);

        String reqBody = objectMapper.writeValueAsString(TestData.ORDER_DTO);

        mockMvc.perform(
                        put("/orders")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateOrderTest_nullReturnsBadRequest() throws Exception {
        OrderDTO testDTO = new OrderDTO();

        String reqBody = objectMapper.writeValueAsString(testDTO);

        mockMvc.perform(
                        put("/orders")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrderTest_returnsNotFound() throws Exception {
        OrderDTO testDTO = new OrderDTO();
        testDTO.setId(100L);

        String reqBody = objectMapper.writeValueAsString(testDTO);

        doThrow(new NoSuchElementException("")).when(orderService).updateOrder(any(OrderDTO.class));

        mockMvc.perform(
                        put("/orders")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOrder_OK() throws Exception {
        doNothing().when(orderService).deleteById(anyLong());

        mockMvc.perform(
                        delete("/orders/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteOrder_returnsNotFound() throws Exception {
        doThrow(new NoSuchElementException("")).when(orderService).deleteById(anyLong());

        mockMvc.perform(delete("/orders/100"))
                .andExpect(status().isNotFound());
    }
}