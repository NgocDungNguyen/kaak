package com.restaurant.service;

import com.restaurant.model.Order;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    List<Order> getAllOrders() throws SQLException;
    Order getOrderById(String id) throws SQLException;
    void addOrder(Order order) throws SQLException;
    void updateOrder(Order order) throws SQLException;
    void deleteOrder(String id) throws SQLException;
    List<Order> searchOrders(String query, String searchBy, String sortBy, boolean ascending) throws SQLException;
    List<Order> findOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException;
}