package com.cinema.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import com.cinema.util.AlertUtil;

import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    public void initialize() {
        // You can load a default view here if you want
        // loadView("/view/user_view.fxml");
    }

    @FXML
    private void handleUsers() {
        loadView("/view/user_view.fxml");
    }

    @FXML
    private void handleTheaters() {
        loadView("/view/theater_view.fxml");
    }

    @FXML
    private void handleScreens() {
        loadView("/view/screen_view.fxml");
    }

    @FXML
    private void handleBookings() {
        loadView("/view/booking_view.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            if (mainBorderPane != null) {
                mainBorderPane.setCenter(view);
            } else {
                AlertUtil.showError("Error", "Main border pane is null. Please restart the application.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to load view: " + e.getMessage());
        }
    }
}