package com.restaurant.util;

import java.sql.*;
import java.time.LocalDate;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:restaurant.db";

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

            // Create Customers table
            stmt.execute("CREATE TABLE IF NOT EXISTS customers (" +
                    "id TEXT PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "phone TEXT NOT NULL," +
                    "email TEXT NOT NULL," +
                    "address TEXT NOT NULL," +
                    "joinDate TEXT NOT NULL," +
                    "totalOrders INTEGER NOT NULL," +
                    "lastOrderDate TEXT)");

            // Create Deliverymen table
            stmt.execute("CREATE TABLE IF NOT EXISTS deliverymen (" +
                    "id TEXT PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "phone TEXT NOT NULL," +
                    "email TEXT NOT NULL," +
                    "status TEXT NOT NULL," +
                    "vehicleType TEXT NOT NULL," +
                    "licenseNumber TEXT NOT NULL," +
                    "joinDate TEXT NOT NULL," +
                    "totalDeliveries INTEGER NOT NULL," +
                    "rating REAL NOT NULL," +
                    "available BOOLEAN NOT NULL)");

            // Create Items table
            stmt.execute("CREATE TABLE IF NOT EXISTS items (" +
                    "id TEXT PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "category TEXT NOT NULL," +
                    "price REAL NOT NULL," +
                    "description TEXT NOT NULL," +
                    "preparationTime INTEGER NOT NULL," +
                    "isAvailable BOOLEAN NOT NULL," +
                    "imageUrl TEXT," +
                    "calories INTEGER NOT NULL," +
                    "spicyLevel INTEGER NOT NULL)");

            // Create Orders table
            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "id TEXT PRIMARY KEY," +
                    "customerId TEXT NOT NULL," +
                    "deliverymanId TEXT," +
                    "orderDate TEXT NOT NULL," +
                    "status TEXT NOT NULL," +
                    "totalAmount REAL NOT NULL," +
                    "deliveryAddress TEXT NOT NULL," +
                    "specialInstructions TEXT," +
                    "paymentMethod TEXT NOT NULL," +
                    "deliveryTime TEXT," +
                    "FOREIGN KEY (customerId) REFERENCES customers(id)," +
                    "FOREIGN KEY (deliverymanId) REFERENCES deliverymen(id))");

            // Create OrderItems table
            stmt.execute("CREATE TABLE IF NOT EXISTS order_items (" +
                    "orderId TEXT NOT NULL," +
                    "itemId TEXT NOT NULL," +
                    "quantity INTEGER NOT NULL," +
                    "FOREIGN KEY (orderId) REFERENCES orders(id)," +
                    "FOREIGN KEY (itemId) REFERENCES items(id)," +
                    "PRIMARY KEY (orderId, itemId))");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void insertSampleData() {
        try (Connection conn = getConnection()) {
            // Insert sample customers
            String insertCustomer = "INSERT OR IGNORE INTO customers (id, name, phone, email, address, joinDate, totalOrders, lastOrderDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertCustomer)) {
                String[][] customerData = {
                        {"CST001", "John Doe", "1234567890", "john@example.com", "123 Main St", "2023-01-01", "5", "2023-05-15"},
                        {"CST002", "Jane Smith", "2345678901", "jane@example.com", "456 Elm St", "2023-02-15", "3", "2023-05-10"},
                        {"CST003", "Bob Johnson", "3456789012", "bob@example.com", "789 Oak St", "2023-03-20", "2", "2023-05-12"},
                        {"CST004", "Alice Brown", "4567890123", "alice@example.com", "321 Pine St", "2023-04-01", "1", "2023-05-14"},
                        {"CST005", "Charlie Davis", "5678901234", "charlie@example.com", "654 Maple St", "2023-04-15", "4", "2023-05-16"},
                        {"CST006", "Eva Wilson", "6789012345", "eva@example.com", "987 Cedar St", "2023-05-01", "2", "2023-05-18"},
                        {"CST007", "Frank Miller", "7890123456", "frank@example.com", "147 Birch St", "2023-05-10", "1", "2023-05-20"},
                        {"CST008", "Grace Lee", "8901234567", "grace@example.com", "258 Walnut St", "2023-05-20", "3", "2023-05-22"},
                        {"CST009", "Henry Taylor", "9012345678", "henry@example.com", "369 Spruce St", "2023-06-01", "2", "2023-06-05"},
                        {"CST010", "Ivy Clark", "0123456789", "ivy@example.com", "741 Fir St", "2023-06-10", "1", "2023-06-15"}
                };

                for (String[] data : customerData) {
                    for (int i = 0; i < data.length; i++) {
                        pstmt.setString(i + 1, data[i]);
                    }
                    pstmt.executeUpdate();
                }
            }

            // Insert sample deliverymen
            String insertDeliveryman = "INSERT OR IGNORE INTO deliverymen (id, name, phone, email, status, vehicleType, licenseNumber, joinDate, totalDeliveries, rating, available) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertDeliveryman)) {
                String[][] deliverymanData = {
                        {"DLV001", "Mike Johnson", "1122334455", "mike@example.com", "ACTIVE", "Car", "LIC001", "2023-01-01", "50", "4.8", "true"},
                        {"DLV002", "Sarah Brown", "2233445566", "sarah@example.com", "ACTIVE", "Motorcycle", "LIC002", "2023-02-01", "40", "4.7", "true"},
                        {"DLV003", "Tom Davis", "3344556677", "tom@example.com", "INACTIVE", "Bicycle", "LIC003", "2023-03-01", "30", "4.6", "false"},
                        {"DLV004", "Emily Wilson", "4455667788", "emily@example.com", "ACTIVE", "Car", "LIC004", "2023-04-01", "45", "4.9", "true"},
                        {"DLV005", "David Lee", "5566778899", "david@example.com", "ACTIVE", "Motorcycle", "LIC005", "2023-05-01", "35", "4.5", "true"},
                        {"DLV006", "Lisa Taylor", "6677889900", "lisa@example.com", "ACTIVE", "Car", "LIC006", "2023-06-01", "25", "4.7", "true"},
                        {"DLV007", "Mark Anderson", "7788990011", "mark@example.com", "INACTIVE", "Bicycle", "LIC007", "2023-07-01", "20", "4.4", "false"},
                        {"DLV008", "Karen White", "8899001122", "karen@example.com", "ACTIVE", "Car", "LIC008", "2023-08-01", "30", "4.8", "true"},
                        {"DLV009", "Paul Harris", "9900112233", "paul@example.com", "ACTIVE", "Motorcycle", "LIC009", "2023-09-01", "40", "4.6", "true"},
                        {"DLV010", "Emma Clark", "0011223344", "emma@example.com", "ACTIVE", "Car", "LIC010", "2023-10-01", "35", "4.7", "true"}
                };

                for (String[] data : deliverymanData) {
                    for (int i = 0; i < data.length; i++) {
                        pstmt.setString(i + 1, data[i]);
                    }
                    pstmt.executeUpdate();
                }
            }

            // Insert sample items
            String insertItem = "INSERT OR IGNORE INTO items (id, name, category, price, description, preparationTime, isAvailable, imageUrl, calories, spicyLevel) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertItem)) {
                String[][] itemData = {
                        {"ITM001", "Margherita Pizza", "Pizza", "10.99", "Classic cheese and tomato pizza", "20", "true", "margherita.jpg", "800", "1"},
                        {"ITM002", "Pepperoni Pizza", "Pizza", "12.99", "Pizza with pepperoni and cheese", "25", "true", "pepperoni.jpg", "900", "2"},
                        {"ITM003", "Vegetarian Pizza", "Pizza", "11.99", "Pizza with assorted vegetables", "22", "true", "vegetarian.jpg", "750", "1"},
                        {"ITM004", "Chicken Burger", "Burger", "8.99", "Grilled chicken burger with lettuce and mayo", "15", "true", "chicken_burger.jpg", "600", "1"},
                        {"ITM005", "Beef Burger", "Burger", "9.99", "Juicy beef burger with cheese and pickles", "18", "true", "beef_burger.jpg", "700", "1"},
                        {"ITM006", "Caesar Salad", "Salad", "7.99", "Fresh romaine lettuce with Caesar dressing", "10", "true", "caesar_salad.jpg", "300", "0"},
                        {"ITM007", "Greek Salad", "Salad", "8.99", "Mixed salad with feta cheese and olives", "12", "true", "greek_salad.jpg", "350", "0"},
                        {"ITM008", "Spaghetti Bolognese", "Pasta", "13.99", "Spaghetti with rich meat sauce", "25", "true", "spaghetti_bolognese.jpg", "700", "1"},
                        {"ITM009", "Fettuccine Alfredo", "Pasta", "12.99", "Creamy fettuccine pasta", "22", "true", "fettuccine_alfredo.jpg", "800", "0"},
                        {"ITM010", "Chocolate Brownie", "Dessert", "5.99", "Warm chocolate brownie with vanilla ice cream", "15", "true", "chocolate_brownie.jpg", "400", "0"}
                };

                for (String[] data : itemData) {
                    for (int i = 0; i < data.length; i++) {
                        pstmt.setString(i + 1, data[i]);
                    }
                    pstmt.executeUpdate();
                }
            }

            // Insert sample orders
            String insertOrder = "INSERT OR IGNORE INTO orders (id, customerId, deliverymanId, orderDate, status, totalAmount, deliveryAddress, specialInstructions, paymentMethod, deliveryTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertOrder)) {
                String[][] orderData = {
                        {"ORD001", "CST001", "DLV001", "2023-05-15 14:30:00", "DELIVERED", "35.97", "123 Main St", "Ring doorbell twice", "CREDIT_CARD", "2023-05-15 15:15:00"},
                        {"ORD002", "CST002", "DLV002", "2023-05-16 12:45:00", "DELIVERED", "28.98", "456 Elm St", "Leave at door", "CASH", "2023-05-16 13:30:00"},
                        {"ORD003", "CST003", "DLV003", "2023-05-17 18:20:00", "DELIVERED", "42.97", "789 Oak St", "No onions please", "DEBIT_CARD", "2023-05-17 19:05:00"},
                        {"ORD004", "CST004", "DLV004", "2023-05-18 20:10:00", "DELIVERED", "22.98", "321 Pine St", "Extra napkins", "CREDIT_CARD", "2023-05-18 20:55:00"},
                        {"ORD005", "CST005", "DLV005", "2023-05-19 13:15:00", "DELIVERED", "31.97", "654 Maple St", "Knock on door", "CASH", "2023-05-19 14:00:00"},
                        {"ORD006", "CST006", "DLV006", "2023-05-20 19:30:00", "PREPARING", "25.98", "987 Cedar St", "Call upon arrival", "CREDIT_CARD", null},
                        {"ORD007", "CST007", "DLV007", "2023-05-21 11:45:00", "OUT_FOR_DELIVERY", "38.97", "147 Birch St", "No utensils needed", "DEBIT_CARD", null},
                        {"ORD008", "CST008", "DLV008", "2023-05-22 17:20:00", "PREPARING", "29.98", "258 Walnut St", "Extra sauce on the side", "CASH", null},
                        {"ORD009", "CST009", "DLV009", "2023-05-23 21:00:00", "PENDING", "33.97", "369 Spruce St", "Doorbell is broken, please knock", "CREDIT_CARD", null},
                        {"ORD010", "CST010", "DLV010", "2023-05-24 15:40:00", "PENDING", "27.98", "741 Fir St", "Allergic to peanuts", "DEBIT_CARD", null}
                };

                for (String[] data : orderData) {
                    for (int i = 0; i < data.length; i++) {
                        pstmt.setString(i + 1, data[i]);
                    }
                    pstmt.executeUpdate();
                }
            }

            // Insert sample order items
            String insertOrderItem = "INSERT OR IGNORE INTO order_items (orderId, itemId, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertOrderItem)) {
                String[][] orderItemData = {
                        {"ORD001", "ITM001", "2"},
                        {"ORD001", "ITM006", "1"},
                        {"ORD002", "ITM003", "1"},
                        {"ORD002", "ITM007", "1"},
                        {"ORD003", "ITM002", "2"},
                        {"ORD003", "ITM008", "1"},
                        {"ORD004", "ITM004", "2"},
                        {"ORD004", "ITM010", "1"},
                        {"ORD005", "ITM005", "1"},
                        {"ORD005", "ITM009", "1"},
                        {"ORD006", "ITM001", "1"},
                        {"ORD006", "ITM006", "1"},
                        {"ORD007", "ITM003", "2"},
                        {"ORD007", "ITM010", "2"},
                        {"ORD008", "ITM002", "1"},
                        {"ORD008", "ITM007", "1"},
                        {"ORD009", "ITM004", "2"},
                        {"ORD009", "ITM008", "1"},
                        {"ORD010", "ITM005", "1"},
                        {"ORD010", "ITM009", "1"}
                };

                for (String[] data : orderItemData) {
                    for (int i = 0; i < data.length; i++) {
                        pstmt.setString(i + 1, data[i]);
                    }
                    pstmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}