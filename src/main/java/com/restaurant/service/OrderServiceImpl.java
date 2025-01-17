package com.restaurant.service;

import com.restaurant.dao.OrderDAO;
import com.restaurant.model.Order;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OrderServiceImpl implements OrderService {
    private final OrderDAO orderDAO;

    public OrderServiceImpl() {
        this.orderDAO = new OrderDAO();
    }

    @Override
    public List<Order> getAllOrders() throws SQLException {
        System.out.println("Service: Getting all orders");
        return orderDAO.findAll();
    }

    // For testing purposes
    OrderServiceImpl(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }


    @Override
    public Order getOrderById(String id) throws SQLException {
        System.out.println("Service: Getting order by ID: " + id);
        return orderDAO.findById(id);
    }

    @Override
    public void addOrder(Order order) throws SQLException {
        System.out.println("Service: Adding order");
        orderDAO.create(order);
        System.out.println("Service: Order added successfully");
    }

    @Override
    public void updateOrder(Order order) throws SQLException {
        System.out.println("Service: Updating order with ID: " + order.getId());
        orderDAO.update(order);
        System.out.println("Service: Order updated successfully");
    }

    @Override
    public void deleteOrder(String id) throws SQLException {
        System.out.println("Service: Deleting order with ID: " + id);
        orderDAO.delete(id);
        System.out.println("Service: Order deleted successfully");
    }

    @Override
    public List<Order> searchOrders(String query, String searchBy, String sortBy, boolean ascending) throws SQLException {
        System.out.println("Service: Searching orders - Query: " + query + ", SearchBy: " + searchBy + ", SortBy: " + sortBy + ", Ascending: " + ascending);
        List<Order> orders = orderDAO.findAll();

        // Filter
        orders = orders.stream()
                .filter(o -> {
                    if (searchBy.equals("id")) {
                        return o.getId().contains(query);
                    } else if (searchBy.equals("customer")) {
                        return o.getCustomer().getName().toLowerCase().contains(query.toLowerCase());
                    } else {
                        return o.getStatus().toLowerCase().contains(query.toLowerCase());
                    }
                })
                .collect(Collectors.toList());

        // Sort
        Comparator<Order> comparator;
        switch (sortBy) {
            case "id":
                comparator = Comparator.comparing(Order::getId);
                break;
            case "date":
                comparator = Comparator.comparing(Order::getOrderDate);
                break;
            case "total":
                comparator = Comparator.comparing(Order::getTotalAmount);
                break;
            default:
                comparator = Comparator.comparing(o -> o.getCustomer().getName());
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        orders.sort(comparator);

        System.out.println("Service: Found " + orders.size() + " orders matching the criteria");
        return orders;
    }

    @Override
    public List<Order> findOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        System.out.println("Service: Finding orders by date range: " + startDate + " - " + endDate);
        return orderDAO.findAll().stream()
                .filter(order -> !order.getOrderDate().isBefore(startDate) && !order.getOrderDate().isAfter(endDate))
                .collect(Collectors.toList());
    }
}