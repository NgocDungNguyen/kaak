package com.restaurant.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private void handleCustomers() {
        loadView("/view/customer_view.fxml");
    }

    @FXML
    private void handleOrders() {
        loadView("/view/order_view.fxml");
    }

    @FXML
    private void handleItems() {
        loadView("/view/item_view.fxml");
    }

    @FXML
    private void handleDeliverymen() {
        loadView("/view/deliveryman_view.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            if (mainBorderPane != null) {
                mainBorderPane.setCenter(view);
            } else {
                System.err.println("mainBorderPane is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + e.getMessage());
        }
    }
}