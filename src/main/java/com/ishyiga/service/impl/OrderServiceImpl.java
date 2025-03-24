
package com.ishyiga.service.impl;

import com.ishyiga.entities.Order;
import com.ishyiga.repo.OrderRepository;
import com.ishyiga.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    public List<Order> getAllOrders() { return orderRepository.findAll(); }
    public Order saveOrder(Order order) { return orderRepository.save(order); }
    public void deleteOrder(Long id) { orderRepository.deleteById(id); }
}
