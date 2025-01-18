package com.cinema;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import com.cinema.util.DatabaseManager;
import com.cinema.util.AlertUtil;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize the database
        DatabaseManager.initializeDatabase();
        DatabaseManager.insertSampleData();

        try {
            // Load the main view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_view.fxml"));
            Parent root = loader.load();

            // Set up the primary stage
            primaryStage.setTitle("Movie Ticket Reservation System");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to load main view: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}