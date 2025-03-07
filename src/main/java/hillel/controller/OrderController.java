package hillel.controller;

import hillel.domain.dto.OrderDTO;
import hillel.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDTO> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.saveOrder(orderDTO));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDTO> updateOrder(@RequestBody OrderDTO orderDTO) {
        if (orderDTO.getId() == null) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(orderService.updateOrder(orderDTO));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable("id") Long id) {
        orderService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
