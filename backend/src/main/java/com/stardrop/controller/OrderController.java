package com.stardrop.controller;

import com.stardrop.dto.OrderRequest;
import com.stardrop.model.Order;
import com.stardrop.model.User;
import com.stardrop.repository.UserRepository;
import com.stardrop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequest request,
                                              Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(orderService.createOrder(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id,
                                           Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(orderService.getOrderById(userId, id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long id,
                                              Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(orderService.cancelOrder(userId, id));
    }

    private Long getUserId(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}
