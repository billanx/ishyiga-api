package com.ishyiga.service.impl;

import com.ishyiga.entities.Order;
import com.ishyiga.exception.DatabaseException;
import com.ishyiga.repo.OrderRepository;
import com.ishyiga.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        try {
            return orderRepository.findAll(pageable);
        } catch (Exception e) {
            log.error("Error retrieving orders: {}", e.getMessage(), e);
            throw new DatabaseException("Error retrieving orders: " + e.getMessage());
        }
    }

    @Override
    public Order saveOrder(Order order) {
        try {
            return orderRepository.save(order);
        } catch (Exception e) {
            log.error("Error saving order: {}", e.getMessage(), e);
            throw new DatabaseException("Error saving order: " + e.getMessage());
        }
    }

    @Override
    public void deleteOrder(Long id) {
        try {
            orderRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error deleting order with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Error deleting order: " + e.getMessage());
        }
    }
}
