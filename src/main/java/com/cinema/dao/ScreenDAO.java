package com.cinema.dao;

import com.cinema.model.Screen;
import com.cinema.model.Theater;
import com.cinema.util.DatabaseManager;
import com.cinema.exception.DatabaseException;
import java.time.format.DateTimeFormatter;

import com.cinema.exception.NotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ScreenDAO {
    private static final Logger LOGGER = Logger.getLogger(ScreenDAO.class.getName());

    private static final String INSERT_SCREEN = "INSERT INTO screens (movie_name, show_time, available_seats, theater_id) VALUES (?, ?, ?, ?)";

    private static final String SELECT_SCREEN_BY_ID =
            "SELECT s.*, t.name as theater_name, t.address as theater_address " +
                    "FROM screens s " +
                    "JOIN theaters t ON s.theater_id = t.id " +
                    "WHERE s.id = ?";
    private static final String SELECT_ALL_SCREENS =
            "SELECT s.*, t.name as theater_name, t.address as theater_address " +
                    "FROM screens s " +
                    "JOIN theaters t ON s.theater_id = t.id";

    private static final String UPDATE_SCREEN = "UPDATE screens SET movie_name = ?, show_time = ?, available_seats = ?, theater_id = ? WHERE id = ?";
    private static final String DELETE_SCREEN = "DELETE FROM screens WHERE id = ?";
    private static final String SEARCH_SCREENS = "SELECT s.*, t.name as theater_name, t.address as theater_address FROM screens s JOIN theaters t ON s.theater_id = t.id WHERE s.movie_name LIKE ?";

    private final TheaterDAO theaterDAO;

    public ScreenDAO() {
        this.theaterDAO = new TheaterDAO();
    }

    public Screen create(Screen screen) throws DatabaseException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SCREEN, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, screen.getMovieName());
            stmt.setString(2, screen.getShowTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            stmt.setString(3, String.join(",", screen.getAvailableSeats()));
            stmt.setInt(4, screen.getTheater().getId());


            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Creating screen failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    screen.setId(generatedKeys.getInt(1));
                } else {
                    throw new DatabaseException("Creating screen failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error creating screen", e);
        }
        return screen;
    }

    public Screen findById(int id) throws DatabaseException, NotFoundException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_SCREEN_BY_ID)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractScreenFromResultSet(rs);
                } else {
                    throw new NotFoundException("Screen with id " + id + " not found.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding screen by ID: " + id, e);
            throw new DatabaseException("Error finding screen by ID: " + e.getMessage(), e);
        }
    }


    public List<Screen> findAll() throws DatabaseException {
        List<Screen> screens = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_SCREENS);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                screens.add(extractScreenFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all screens", e);
            throw new DatabaseException("Error finding all screens: " + e.getMessage(), e);
        }
        return screens;
    }

    public void update(Screen screen) throws DatabaseException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SCREEN)) {

            stmt.setString(1, screen.getMovieName());
            stmt.setString(2, screen.getShowTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            stmt.setString(3, String.join(",", screen.getAvailableSeats()));
            stmt.setInt(4, screen.getTheater().getId());
            stmt.setInt(5, screen.getId());


            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Updating screen failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error updating screen", e);
        }
    }

    public void delete(int id) throws DatabaseException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SCREEN)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Deleting screen failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting screen", e);
        }
    }

    public List<Screen> searchScreens(String movieName, String sortBy, boolean ascending) throws DatabaseException {
        List<Screen> screens = new ArrayList<>();
        String query = SEARCH_SCREENS + " ORDER BY " + sortBy + (ascending ? " ASC" : " DESC");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + movieName + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    screens.add(extractScreenFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error searching screens", e);
        }
        return screens;
    }


    private Screen extractScreenFromResultSet(ResultSet rs) throws SQLException {
        Screen screen = new Screen();
        screen.setId(rs.getInt("id"));
        screen.setMovieName(rs.getString("movie_name"));

        String showTimeStr = rs.getString("show_time");
        screen.setShowTime(LocalDateTime.parse(showTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        screen.setAvailableSeats(Arrays.asList(rs.getString("available_seats").split(",")));

        Theater theater = new Theater();
        theater.setId(rs.getInt("theater_id"));
        theater.setName(rs.getString("theater_name"));
        theater.setAddress(rs.getString("theater_address"));
        screen.setTheater(theater);

        return screen;
    }
}