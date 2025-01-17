package com.restaurant.dao;

import com.restaurant.dao.interfaces.GenericDAO;
import com.restaurant.model.Item;
import com.restaurant.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemDAO implements GenericDAO<Item, String> {

    @Override
    public Item create(Item item) throws SQLException {
        String sql = "INSERT INTO items (id, name, category, price, description, preparationTime, isAvailable, imageUrl, calories, spicyLevel) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            item.setId(generateUniqueId());
            pstmt.setString(1, item.getId());
            pstmt.setString(2, item.getName());
            pstmt.setString(3, item.getCategory());
            pstmt.setDouble(4, item.getPrice());
            pstmt.setString(5, item.getDescription());
            pstmt.setInt(6, item.getPreparationTime());
            pstmt.setBoolean(7, item.isAvailable());
            pstmt.setString(8, item.getImageUrl());
            pstmt.setInt(9, item.getCalories());
            pstmt.setInt(10, item.getSpicyLevel());

            pstmt.executeUpdate();
        }
        return item;
    }

    @Override
    public void update(Item item) throws SQLException {
        String sql = "UPDATE items SET name = ?, category = ?, price = ?, description = ?, preparationTime = ?, isAvailable = ?, imageUrl = ?, calories = ?, spicyLevel = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getCategory());
            pstmt.setDouble(3, item.getPrice());
            pstmt.setString(4, item.getDescription());
            pstmt.setInt(5, item.getPreparationTime());
            pstmt.setBoolean(6, item.isAvailable());
            pstmt.setString(7, item.getImageUrl());
            pstmt.setInt(8, item.getCalories());
            pstmt.setInt(9, item.getSpicyLevel());
            pstmt.setString(10, item.getId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM items WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Item> findAll() throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getString("id"));
                item.setName(rs.getString("name"));
                item.setCategory(rs.getString("category"));
                item.setPrice(rs.getDouble("price"));
                item.setDescription(rs.getString("description"));
                item.setPreparationTime(rs.getInt("preparationTime"));
                item.setAvailable(rs.getBoolean("isAvailable"));
                item.setImageUrl(rs.getString("imageUrl"));
                item.setCalories(rs.getInt("calories"));
                item.setSpicyLevel(rs.getInt("spicyLevel"));
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public Item findById(String id) throws SQLException {
        String sql = "SELECT * FROM items WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Item item = new Item();
                item.setId(rs.getString("id"));
                item.setName(rs.getString("name"));
                item.setCategory(rs.getString("category"));
                item.setPrice(rs.getDouble("price"));
                item.setDescription(rs.getString("description"));
                item.setPreparationTime(rs.getInt("preparationTime"));
                item.setAvailable(rs.getBoolean("isAvailable"));
                item.setImageUrl(rs.getString("imageUrl"));
                item.setCalories(rs.getInt("calories"));
                item.setSpicyLevel(rs.getInt("spicyLevel"));
                return item;
            }
        }
        return null;
    }

    private String generateUniqueId() throws SQLException {
        Random random = new Random();
        String id;
        do {
            id = "ITM" + String.format("%03d", random.nextInt(1000));
        } while (findById(id) != null);
        return id;
    }
}