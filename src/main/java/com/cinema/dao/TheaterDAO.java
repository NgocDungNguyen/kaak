package com.cinema.dao;

import com.cinema.model.Theater;
import com.cinema.util.DatabaseManager;
import com.cinema.exception.DatabaseException;
import com.cinema.exception.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TheaterDAO {

    private static final String INSERT_THEATER = "INSERT INTO theaters (name, address) VALUES (?, ?)";
    private static final String SELECT_THEATER_BY_ID = "SELECT * FROM theaters WHERE id = ?";
    private static final String SELECT_ALL_THEATERS = "SELECT * FROM theaters";
    private static final String UPDATE_THEATER = "UPDATE theaters SET name = ?, address = ? WHERE id = ?";
    private static final String DELETE_THEATER = "DELETE FROM theaters WHERE id = ?";
    private static final String SEARCH_THEATERS = "SELECT * FROM theaters WHERE name LIKE ? OR address LIKE ?";

    public Theater create(Theater theater) throws DatabaseException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_THEATER, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, theater.getName());
            stmt.setString(2, theater.getAddress());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Creating theater failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    theater.setId(generatedKeys.getInt(1));
                } else {
                    throw new DatabaseException("Creating theater failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error creating theater", e);
        }
        return theater;
    }

    public Theater findById(int id) throws DatabaseException, NotFoundException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_THEATER_BY_ID)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractTheaterFromResultSet(rs);
                } else {
                    throw new NotFoundException("Theater with id " + id + " not found.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding theater by ID", e);
        }
    }

    public List<Theater> findAll() throws DatabaseException {
        List<Theater> theaters = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_THEATERS)) {

            while (rs.next()) {
                theaters.add(extractTheaterFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding all theaters", e);
        }
        return theaters;
    }

    public void update(Theater theater) throws DatabaseException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_THEATER)) {

            stmt.setString(1, theater.getName());
            stmt.setString(2, theater.getAddress());
            stmt.setInt(3, theater.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Updating theater failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error updating theater", e);
        }
    }

    public void delete(int id) throws DatabaseException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_THEATER)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Deleting theater failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting theater", e);
        }
    }

    public List<Theater> searchTheaters(String searchTerm, String searchBy, boolean ascending) throws DatabaseException {
        List<Theater> theaters = new ArrayList<>();
        String query = SEARCH_THEATERS + " ORDER BY " + searchBy + (ascending ? " ASC" : " DESC");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    theaters.add(extractTheaterFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error searching theaters", e);
        }
        return theaters;
    }

    private Theater extractTheaterFromResultSet(ResultSet rs) throws SQLException {
        Theater theater = new Theater();
        theater.setId(rs.getInt("id"));
        theater.setName(rs.getString("name"));
        theater.setAddress(rs.getString("address"));
        return theater;
    }
}