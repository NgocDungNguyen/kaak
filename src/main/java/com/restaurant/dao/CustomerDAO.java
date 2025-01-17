package com.restaurant.dao;

import com.restaurant.dao.interfaces.GenericDAO;
import com.restaurant.model.Customer;
import com.restaurant.util.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomerDAO implements GenericDAO<Customer, String> {

    @Override
    public Customer create(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (id, name, phone, email, address, joinDate, totalOrders, lastOrderDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            customer.setId(generateUniqueId());
            pstmt.setString(1, customer.getId());
            pstmt.setString(2, customer.getName());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getEmail());
            pstmt.setString(5, customer.getAddress());
            pstmt.setString(6, customer.getJoinDate().toString());
            pstmt.setInt(7, customer.getTotalOrders());
            pstmt.setString(8, customer.getLastOrderDate() != null ? customer.getLastOrderDate().toString() : null);

            pstmt.executeUpdate();
        }
        return customer;
    }

    @Override
    public void update(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET name = ?, phone = ?, email = ?, address = ?, joinDate = ?, totalOrders = ?, lastOrderDate = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getPhone());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getAddress());
            pstmt.setString(5, customer.getJoinDate().toString());
            pstmt.setInt(6, customer.getTotalOrders());
            pstmt.setString(7, customer.getLastOrderDate() != null ? customer.getLastOrderDate().toString() : null);
            pstmt.setString(8, customer.getId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM customers WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Customer> findAll() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getString("id"));
                customer.setName(rs.getString("name"));
                customer.setPhone(rs.getString("phone"));
                customer.setEmail(rs.getString("email"));
                customer.setAddress(rs.getString("address"));
                customer.setJoinDate(LocalDate.parse(rs.getString("joinDate")));
                customer.setTotalOrders(rs.getInt("totalOrders"));
                String lastOrderDate = rs.getString("lastOrderDate");
                customer.setLastOrderDate(lastOrderDate != null ? LocalDate.parse(lastOrderDate) : null);
                customers.add(customer);
            }
        }
        return customers;
    }

    @Override
    public Customer findById(String id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getString("id"));
                customer.setName(rs.getString("name"));
                customer.setPhone(rs.getString("phone"));
                customer.setEmail(rs.getString("email"));
                customer.setAddress(rs.getString("address"));
                customer.setJoinDate(LocalDate.parse(rs.getString("joinDate")));
                customer.setTotalOrders(rs.getInt("totalOrders"));
                String lastOrderDate = rs.getString("lastOrderDate");
                customer.setLastOrderDate(lastOrderDate != null ? LocalDate.parse(lastOrderDate) : null);
                return customer;
            }
        }
        return null;
    }

    private String generateUniqueId() throws SQLException {
        Random random = new Random();
        String id;
        do {
            id = "CST" + String.format("%03d", random.nextInt(1000));
        } while (findById(id) != null);
        return id;
    }
}