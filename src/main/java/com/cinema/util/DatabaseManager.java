package com.cinema.util;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:cinema.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC Driver not found", e);
        }
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "email TEXT NOT NULL UNIQUE," +
                    "phone_number TEXT NOT NULL)");

            // Create Theaters table
            stmt.execute("CREATE TABLE IF NOT EXISTS theaters (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "address TEXT NOT NULL)");

            // Create Screens table
            stmt.execute("CREATE TABLE IF NOT EXISTS screens (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "theater_id INTEGER NOT NULL," +
                    "movie_name TEXT NOT NULL," +
                    "show_time DATETIME NOT NULL," +
                    "available_seats TEXT NOT NULL," +
                    "FOREIGN KEY (theater_id) REFERENCES theaters (id))");

            // Create Bookings table
            stmt.execute("CREATE TABLE IF NOT EXISTS bookings (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "screen_id INTEGER NOT NULL," +
                    "booking_time DATETIME NOT NULL," +
                    "reserved_seats TEXT NOT NULL," +
                    "total_price REAL NOT NULL," +
                    "FOREIGN KEY (user_id) REFERENCES users (id)," +
                    "FOREIGN KEY (screen_id) REFERENCES screens (id))");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void insertSampleData() {
        try (Connection conn = getConnection()) {
            // Insert sample users
            String insertUser = "INSERT OR IGNORE INTO users (name, email, phone_number) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertUser)) {
                String[][] userData = {
                        {"John Doe", "john@example.com", "1234567890"},
                        {"Jane Smith", "jane@example.com", "9876543210"},
                        {"Bob Johnson", "bob@example.com", "5555555555"},
                        {"Alice Brown", "alice@example.com", "1112223333"},
                        {"Charlie Davis", "charlie@example.com", "4445556666"}
                };

                for (String[] data : userData) {
                    pstmt.setString(1, data[0]);
                    pstmt.setString(2, data[1]);
                    pstmt.setString(3, data[2]);
                    pstmt.executeUpdate();
                }
            }

            // Insert sample theaters
            String insertTheater = "INSERT OR IGNORE INTO theaters (name, address) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertTheater)) {
                String[][] theaterData = {
                        {"Cineplex", "123 Main St, Anytown, USA"},
                        {"Starlight Cinema", "456 Oak Rd, Another City, USA"},
                        {"Metropolis Movies", "789 Pine Ave, Somewhere, USA"}
                };

                for (String[] data : theaterData) {
                    pstmt.setString(1, data[0]);
                    pstmt.setString(2, data[1]);
                    pstmt.executeUpdate();
                }
            }

            // Insert sample screens
            String insertScreen = "INSERT OR IGNORE INTO screens (theater_id, movie_name, show_time, available_seats) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertScreen)) {
                Object[][] screenData = {
                        {1, "Avengers: Endgame", LocalDateTime.now().plusDays(1), "A1,A2,A3,B1,B2,B3,C1,C2,C3"},
                        {1, "The Lion King", LocalDateTime.now().plusDays(2), "A1,A2,A3,B1,B2,B3,C1,C2,C3"},
                        {2, "Joker", LocalDateTime.now().plusDays(1), "A1,A2,A3,B1,B2,B3,C1,C2,C3"},
                        {2, "Inception", LocalDateTime.now().plusDays(3), "A1,A2,A3,B1,B2,B3,C1,C2,C3"},
                        {3, "The Shawshank Redemption", LocalDateTime.now().plusDays(2), "A1,A2,A3,B1,B2,B3,C1,C2,C3"}
                };

                for (Object[] data : screenData) {
                    pstmt.setInt(1, (Integer) data[0]);
                    pstmt.setString(2, (String) data[1]);
                    pstmt.setString(3, ((LocalDateTime) data[2]).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    pstmt.setString(4, (String) data[3]);
                    pstmt.executeUpdate();
                }
            }

            // Insert sample bookings
            String insertBooking = "INSERT OR IGNORE INTO bookings (user_id, screen_id, booking_time, reserved_seats, total_price) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertBooking)) {
                Object[][] bookingData = {
                        {1, 1, LocalDateTime.now(), "A1,A2", 20.0},
                        {2, 2, LocalDateTime.now().minusHours(2), "B1,B2,B3", 30.0},
                        {3, 3, LocalDateTime.now().minusDays(1), "C1,C2", 20.0},
                        {4, 4, LocalDateTime.now().minusHours(5), "A3,B3", 20.0},
                        {5, 5, LocalDateTime.now().minusDays(2), "A1,A2,A3", 30.0}
                };

                for (Object[] data : bookingData) {
                    pstmt.setInt(1, (Integer) data[0]);
                    pstmt.setInt(2, (Integer) data[1]);
                    pstmt.setString(3, ((LocalDateTime) data[2]).toString());
                    pstmt.setString(4, (String) data[3]);
                    pstmt.setDouble(5, (Double) data[4]);
                    pstmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}