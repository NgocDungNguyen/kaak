package com.restaurant.service;

import com.restaurant.model.Order;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    List<Order> getAllOrders() throws IOException;
    Order getOrderById(String id) throws IOException;
    void addOrder(Order order) throws IOException;
    void updateOrder(Order order) throws IOException;
    void deleteOrder(String id) throws IOException;
    List<Order> searchOrders(String query, String searchBy, String sortBy, boolean ascending) throws IOException;
    List<Order> findOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws IOException;
}