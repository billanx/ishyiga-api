
package com.ishyiga.service;

import com.ishyiga.entities.Order;

import java.util.List;


public interface OrderService {

    List<Order> getAllOrders();
    Order saveOrder(Order order);
    void deleteOrder(Long id);
}
