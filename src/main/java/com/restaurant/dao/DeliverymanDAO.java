package com.restaurant.dao;

import com.restaurant.dao.interfaces.GenericDAO;
import com.restaurant.model.Deliveryman;
import com.restaurant.util.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DeliverymanDAO implements GenericDAO<Deliveryman, String> {

    @Override
    public Deliveryman create(Deliveryman deliveryman) throws SQLException {
        String sql = "INSERT INTO deliverymen (id, name, phone, email, status, vehicleType, licenseNumber, joinDate, totalDeliveries, rating, available) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            deliveryman.setId(generateUniqueId());
            pstmt.setString(1, deliveryman.getId());
            pstmt.setString(2, deliveryman.getName());
            pstmt.setString(3, deliveryman.getPhone());
            pstmt.setString(4, deliveryman.getEmail());
            pstmt.setString(5, deliveryman.getStatus());
            pstmt.setString(6, deliveryman.getVehicleType());
            pstmt.setString(7, deliveryman.getLicenseNumber());
            pstmt.setString(8, deliveryman.getJoinDate().toString());
            pstmt.setInt(9, deliveryman.getTotalDeliveries());
            pstmt.setDouble(10, deliveryman.getRating());
            pstmt.setBoolean(11, deliveryman.isAvailable());

            pstmt.executeUpdate();
        }
        return deliveryman;
    }

    @Override
    public void update(Deliveryman deliveryman) throws SQLException {
        String sql = "UPDATE deliverymen SET name = ?, phone = ?, email = ?, status = ?, vehicleType = ?, licenseNumber = ?, joinDate = ?, totalDeliveries = ?, rating = ?, available = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, deliveryman.getName());
            pstmt.setString(2, deliveryman.getPhone());
            pstmt.setString(3, deliveryman.getEmail());
            pstmt.setString(4, deliveryman.getStatus());
            pstmt.setString(5, deliveryman.getVehicleType());
            pstmt.setString(6, deliveryman.getLicenseNumber());
            pstmt.setString(7, deliveryman.getJoinDate().toString());
            pstmt.setInt(8, deliveryman.getTotalDeliveries());
            pstmt.setDouble(9, deliveryman.getRating());
            pstmt.setBoolean(10, deliveryman.isAvailable());
            pstmt.setString(11, deliveryman.getId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM deliverymen WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Deliveryman> findAll() throws SQLException {
        List<Deliveryman> deliverymen = new ArrayList<>();
        String sql = "SELECT * FROM deliverymen";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Deliveryman deliveryman = new Deliveryman();
                deliveryman.setId(rs.getString("id"));
                deliveryman.setName(rs.getString("name"));
                deliveryman.setPhone(rs.getString("phone"));
                deliveryman.setEmail(rs.getString("email"));
                deliveryman.setStatus(rs.getString("status"));
                deliveryman.setVehicleType(rs.getString("vehicleType"));
                deliveryman.setLicenseNumber(rs.getString("licenseNumber"));
                deliveryman.setJoinDate(LocalDate.parse(rs.getString("joinDate")));
                deliveryman.setTotalDeliveries(rs.getInt("totalDeliveries"));
                deliveryman.setRating(rs.getDouble("rating"));
                deliveryman.setAvailable(rs.getBoolean("available"));
                deliverymen.add(deliveryman);
            }
        }
        return deliverymen;
    }

    @Override
    public Deliveryman findById(String id) throws SQLException {
        String sql = "SELECT * FROM deliverymen WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Deliveryman deliveryman = new Deliveryman();
                deliveryman.setId(rs.getString("id"));
                deliveryman.setName(rs.getString("name"));
                deliveryman.setPhone(rs.getString("phone"));
                deliveryman.setEmail(rs.getString("email"));
                deliveryman.setStatus(rs.getString("status"));
                deliveryman.setVehicleType(rs.getString("vehicleType"));
                deliveryman.setLicenseNumber(rs.getString("licenseNumber"));
                deliveryman.setJoinDate(LocalDate.parse(rs.getString("joinDate")));
                deliveryman.setTotalDeliveries(rs.getInt("totalDeliveries"));
                deliveryman.setRating(rs.getDouble("rating"));
                deliveryman.setAvailable(rs.getBoolean("available"));
                return deliveryman;
            }
        }
        return null;
    }

    private String generateUniqueId() throws SQLException {
        Random random = new Random();
        String id;
        do {
            id = "DLV" + String.format("%03d", random.nextInt(1000));
        } while (findById(id) != null);
        return id;
    }
}