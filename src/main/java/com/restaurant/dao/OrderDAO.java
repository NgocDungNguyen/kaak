package com.restaurant.dao;

import com.restaurant.dao.interfaces.GenericDAO;
import com.restaurant.model.Order;
import com.restaurant.model.Item;
import com.restaurant.util.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OrderDAO implements GenericDAO<Order, String> {
    private static final String CSV_FILE = "orders.csv";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CustomerDAO customerDAO;
    private final DeliverymanDAO deliverymanDAO;
    private final ItemDAO itemDAO;

    public OrderDAO() {
        this.customerDAO = new CustomerDAO();
        this.deliverymanDAO = new DeliverymanDAO();
        this.itemDAO = new ItemDAO();
    }

    @Override
    public Order create(Order order) throws SQLException {
        String sql = "INSERT INTO orders (id, customerId, deliverymanId, orderDate, status, totalAmount, deliveryAddress, specialInstructions, paymentMethod, deliveryTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            order.setId(generateUniqueId());
            pstmt.setString(1, order.getId());
            pstmt.setString(2, order.getCustomer().getId());
            pstmt.setString(3, order.getDeliveryman() != null ? order.getDeliveryman().getId() : null);
            pstmt.setString(4, order.getOrderDate().format(DATE_TIME_FORMATTER));
            pstmt.setString(5, order.getStatus());
            pstmt.setDouble(6, order.getTotalAmount());
            pstmt.setString(7, order.getDeliveryAddress());
            pstmt.setString(8, order.getSpecialInstructions());
            pstmt.setString(9, order.getPaymentMethod());
            pstmt.setString(10, order.getDeliveryTime() != null ? order.getDeliveryTime().format(DATE_TIME_FORMATTER) : null);

            pstmt.executeUpdate();

            // Insert order items
            insertOrderItems(order);
        }
        return order;
    }

    @Override
    public void update(Order order) throws SQLException {
        String sql = "UPDATE orders SET customerId = ?, deliverymanId = ?, orderDate = ?, status = ?, totalAmount = ?, deliveryAddress = ?, specialInstructions = ?, paymentMethod = ?, deliveryTime = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, order.getCustomer().getId());
            pstmt.setString(2, order.getDeliveryman() != null ? order.getDeliveryman().getId() : null);
            pstmt.setString(3, order.getOrderDate().format(DATE_TIME_FORMATTER));
            pstmt.setString(4, order.getStatus());
            pstmt.setDouble(5, order.getTotalAmount());
            pstmt.setString(6, order.getDeliveryAddress());
            pstmt.setString(7, order.getSpecialInstructions());
            pstmt.setString(8, order.getPaymentMethod());
            pstmt.setString(9, order.getDeliveryTime() != null ? order.getDeliveryTime().format(DATE_TIME_FORMATTER) : null);
            pstmt.setString(10, order.getId());

            pstmt.executeUpdate();

            // Update order items
            deleteOrderItems(order.getId());
            insertOrderItems(order);
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        deleteOrderItems(id);

        String sql = "DELETE FROM orders WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Order> findAll() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                orders.add(order);
            }
        }
        return orders;
    }

    @Override
    public Order findById(String id) throws SQLException {
        String sql = "SELECT * FROM orders WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractOrderFromResultSet(rs);
            }
        }
        return null;
    }

    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getString("id"));
        order.setCustomer(customerDAO.findById(rs.getString("customerId")));
        String deliverymanId = rs.getString("deliverymanId");
        if (deliverymanId != null) {
            order.setDeliveryman(deliverymanDAO.findById(deliverymanId));
        }
        order.setOrderDate(LocalDateTime.parse(rs.getString("orderDate"), DATE_TIME_FORMATTER));
        order.setStatus(rs.getString("status"));
        order.setTotalAmount(rs.getDouble("totalAmount"));
        order.setDeliveryAddress(rs.getString("deliveryAddress"));
        order.setSpecialInstructions(rs.getString("specialInstructions"));
        order.setPaymentMethod(rs.getString("paymentMethod"));
        String deliveryTime = rs.getString("deliveryTime");
        if (deliveryTime != null) {
            order.setDeliveryTime(LocalDateTime.parse(deliveryTime, DATE_TIME_FORMATTER));
        }

        // Load order items
        order.setItems(getOrderItems(order.getId()));

        return order;
    }

    private void insertOrderItems(Order order) throws SQLException {
        String sql = "INSERT INTO order_items (orderId, itemId, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Map.Entry<Item, Integer> entry : order.getItems().entrySet()) {
                pstmt.setString(1, order.getId());
                pstmt.setString(2, entry.getKey().getId());
                pstmt.setInt(3, entry.getValue());
                pstmt.executeUpdate();
            }
        }
    }

    private void deleteOrderItems(String orderId) throws SQLException {
        String sql = "DELETE FROM order_items WHERE orderId = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, orderId);
            pstmt.executeUpdate();
        }
    }

    private Map<Item, Integer> getOrderItems(String orderId) throws SQLException {
        Map<Item, Integer> items = new HashMap<>();
        String sql = "SELECT * FROM order_items WHERE orderId = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String itemId = rs.getString("itemId");
                int quantity = rs.getInt("quantity");
                Item item = itemDAO.findById(itemId);
                items.put(item, quantity);
            }
        }
        return items;
    }

    private String generateUniqueId() throws SQLException {
        Random random = new Random();
        String id;
        do {
            id = "ORD" + String.format("%03d", random.nextInt(1000));
        } while (findById(id) != null);
        return id;
    }
}