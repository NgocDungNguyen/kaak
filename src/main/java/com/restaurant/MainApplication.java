package com.restaurant;

import com.restaurant.util.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainApplication extends Application {
    private static final String DB_FILE = "restaurant.db";

    @Override
    public void start(Stage primaryStage) throws Exception {
        initializeDatabase();
        printDatabaseContents();

        Parent root = FXMLLoader.load(getClass().getResource("/view/main_view.fxml"));
        primaryStage.setTitle("Restaurant Management System");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    private void initializeDatabase() {
        File dbFile = new File(DB_FILE);
        if (!dbFile.exists()) {
            DatabaseManager.initializeDatabase();
            DatabaseManager.insertSampleData();
        }
    }

    private void printDatabaseContents() {
        String[] tables = {"customers", "deliverymen", "items", "orders", "order_items"};
        for (String table : tables) {
            System.out.println("Contents of " + table + " table:");
            try (Connection conn = DatabaseManager.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM " + table)) {

                int columnCount = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    StringBuilder row = new StringBuilder();
                    for (int i = 1; i <= columnCount; i++) {
                        row.append(rs.getString(i)).append(", ");
                    }
                    System.out.println(row.substring(0, row.length() - 2));
                }
            } catch (SQLException e) {
                System.err.println("Error reading " + table + " table: " + e.getMessage());
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}