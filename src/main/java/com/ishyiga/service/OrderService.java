package com.ishyiga.service;

import com.ishyiga.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<Order> getAllOrders(Pageable pageable);
    Order saveOrder(Order order);
    void deleteOrder(Long id);
}
