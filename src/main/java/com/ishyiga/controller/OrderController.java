
package com.ishyiga.controller;

import com.ishyiga.entities.Order;
import com.ishyiga.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA','BANK')")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    public Order createOrder(@RequestBody Order order) {
        return orderService.saveOrder(order);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
