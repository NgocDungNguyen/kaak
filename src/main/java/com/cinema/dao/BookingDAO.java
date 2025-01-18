package com.cinema.dao;

import com.cinema.model.Booking;
import com.cinema.model.Screen;
import com.cinema.model.User;
import com.cinema.util.DatabaseManager;
import com.cinema.exception.DatabaseException;
import java.time.format.DateTimeFormatter;

import com.cinema.exception.NotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class BookingDAO {
    private static final Logger LOGGER = Logger.getLogger(BookingDAO.class.getName());

    private static final String INSERT_BOOKING = "INSERT INTO bookings (user_id, screen_id, booking_time, reserved_seats, total_price) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_BOOKING_BY_ID = "SELECT * FROM bookings WHERE id = ?";
    private static final String SELECT_ALL_BOOKINGS = "SELECT * FROM bookings";
    private static final String UPDATE_BOOKING = "UPDATE bookings SET user_id = ?, screen_id = ?, booking_time = ?, reserved_seats = ?, total_price = ? WHERE id = ?";
    private static final String DELETE_BOOKING = "DELETE FROM bookings WHERE id = ?";
    private static final String SEARCH_BOOKINGS = "SELECT * FROM bookings WHERE id LIKE ? OR user_id IN (SELECT id FROM users WHERE name LIKE ?) OR screen_id IN (SELECT id FROM screens WHERE movie_name LIKE ?)";

    private final UserDAO userDAO;
    private final ScreenDAO screenDAO;

    public BookingDAO() {
        this.userDAO = new UserDAO();
        this.screenDAO = new ScreenDAO();
    }

    public Booking create(Booking booking) throws DatabaseException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_BOOKING, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, booking.getUser().getId());
            stmt.setInt(2, booking.getScreen().getId());
            stmt.setString(3, booking.getBookingTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            stmt.setString(4, String.join(",", booking.getReservedSeats()));
            stmt.setDouble(5, booking.getTotalPrice());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Creating booking failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    booking.setId(generatedKeys.getInt(1));
                } else {
                    throw new DatabaseException("Creating booking failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error creating booking", e);
        }
        return booking;
    }

    public Booking findById(int id) throws DatabaseException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BOOKING_BY_ID)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractBookingFromResultSet(rs);
                } else {
                    return null; // Return null if no booking is found
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding booking by ID", e);
        }
    }

    public List<Booking> findAll() throws DatabaseException {
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_BOOKINGS)) {

            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all bookings", e);
            throw new DatabaseException("Error finding all bookings: " + e.getMessage(), e);
        }
        return bookings;
    }

    public void update(Booking booking) throws DatabaseException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_BOOKING)) {

            stmt.setInt(1, booking.getUser().getId());
            stmt.setInt(2, booking.getScreen().getId());
            stmt.setString(3, booking.getBookingTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            stmt.setString(4, String.join(",", booking.getReservedSeats()));
            stmt.setDouble(5, booking.getTotalPrice());
            stmt.setInt(6, booking.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Updating booking failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error updating booking", e);
        }
    }

    public void delete(int id) throws DatabaseException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_BOOKING)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Deleting booking failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting booking", e);
        }
    }

    public List<Booking> searchBookings(String searchTerm, String sortBy, boolean ascending) throws DatabaseException {         List<Booking> bookings = new ArrayList<>();
        String query = SEARCH_BOOKINGS + " ORDER BY " + sortBy + (ascending ? " ASC" : " DESC");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");
            stmt.setString(3, "%" + searchTerm + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(extractBookingFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error searching bookings", e);
        }
        return bookings;
    }

    private Booking extractBookingFromResultSet(ResultSet rs) throws SQLException, DatabaseException {
        try {
            Booking booking = new Booking();
            booking.setId(rs.getInt("id"));
            booking.setUser(userDAO.findById(rs.getInt("user_id")));
            booking.setScreen(screenDAO.findById(rs.getInt("screen_id")));

            // Parse the datetime string manually
            String bookingTimeStr = rs.getString("booking_time");
            booking.setBookingTime(LocalDateTime.parse(bookingTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            booking.setReservedSeats(List.of(rs.getString("reserved_seats").split(",")));
            booking.setTotalPrice(rs.getDouble("total_price"));
            return booking;
        } catch (NotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error extracting booking: " + e.getMessage(), e);
            throw new DatabaseException("Error extracting booking: " + e.getMessage(), e);
        }
    }
}