package com.restaurant.controller;

import com.restaurant.model.Deliveryman;
import com.restaurant.service.DeliverymanService;
import com.restaurant.service.DeliverymanServiceImpl;
import com.restaurant.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DeliverymanController implements Initializable {
    @FXML private TableView<Deliveryman> deliverymanTable;
    @FXML private TableColumn<Deliveryman, String> idColumn;
    @FXML private TableColumn<Deliveryman, String> nameColumn;
    @FXML private TableColumn<Deliveryman, String> phoneColumn;
    @FXML private TableColumn<Deliveryman, String> vehicleTypeColumn;

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField vehicleTypeField;
    @FXML private TextField emailField;
    @FXML private TextField licenseNumberField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchByComboBox;
    @FXML private ComboBox<String> sortByComboBox;
    @FXML private CheckBox ascendingCheckBox;

    private final DeliverymanService deliverymanService;
    private ObservableList<Deliveryman> deliverymanList;

    public DeliverymanController() {
        this.deliverymanService = new DeliverymanServiceImpl();
        this.deliverymanList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadDeliverymen();
        setupEventHandlers();
        setupButtonStates();
        setupComboBoxes();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        vehicleTypeColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleType"));
    }

    private void loadDeliverymen() {
        try {
            System.out.println("Controller: Loading deliverymen");
            List<Deliveryman> deliverymen = deliverymanService.getAllDeliverymen();
            deliverymanList.clear();
            deliverymanList.addAll(deliverymen);
            deliverymanTable.setItems(deliverymanList);
            System.out.println("Controller: Loaded " + deliverymen.size() + " deliverymen");
        } catch (Exception e) {
            System.err.println("Controller: Error loading deliverymen: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to load deliverymen: " + e.getMessage());
        }
    }

    private void setupEventHandlers() {
        deliverymanTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                phoneField.setText(newSelection.getPhone());
                vehicleTypeField.setText(newSelection.getVehicleType());
                emailField.setText(newSelection.getEmail());
                licenseNumberField.setText(newSelection.getLicenseNumber());
                statusComboBox.setValue(newSelection.getStatus());
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });

        nameField.textProperty().addListener((obs, old, newValue) -> validateInputs());
        phoneField.textProperty().addListener((obs, old, newValue) -> validateInputs());
        vehicleTypeField.textProperty().addListener((obs, old, newValue) -> validateInputs());
        emailField.textProperty().addListener((obs, old, newValue) -> validateInputs());
        licenseNumberField.textProperty().addListener((obs, old, newValue) -> validateInputs());
        statusComboBox.valueProperty().addListener((obs, old, newValue) -> validateInputs());
    }

    private void setupButtonStates() {
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void setupComboBoxes() {
        statusComboBox.setItems(FXCollections.observableArrayList("ACTIVE", "INACTIVE", "ON_DELIVERY"));
        searchByComboBox.setItems(FXCollections.observableArrayList("name", "phone"));
        sortByComboBox.setItems(FXCollections.observableArrayList("name", "phone"));
    }

    @FXML
    private void handleAdd() {
        try {
            Deliveryman deliveryman = new Deliveryman();
            deliveryman.setName(nameField.getText().trim());
            deliveryman.setPhone(phoneField.getText().trim());
            deliveryman.setVehicleType(vehicleTypeField.getText().trim());
            deliveryman.setEmail(emailField.getText().trim());
            deliveryman.setLicenseNumber(licenseNumberField.getText().trim());
            deliveryman.setStatus(statusComboBox.getValue());

            System.out.println("Controller: Adding deliveryman: " + deliveryman);
            deliverymanService.addDeliveryman(deliveryman);
            System.out.println("Controller: Deliveryman added successfully");

            loadDeliverymen();
            clearFields();
            AlertUtil.showInformation("Success", "Deliveryman added successfully!");
        } catch (Exception e) {
            System.err.println("Controller: Error adding deliveryman: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to add deliveryman: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Deliveryman selectedDeliveryman = deliverymanTable.getSelectionModel().getSelectedItem();
        if (selectedDeliveryman == null) {
            AlertUtil.showError("Error", "Please select a deliveryman to update");
            return;
        }

        try {
            selectedDeliveryman.setName(nameField.getText().trim());
            selectedDeliveryman.setPhone(phoneField.getText().trim());
            selectedDeliveryman.setVehicleType(vehicleTypeField.getText().trim());
            selectedDeliveryman.setEmail(emailField.getText().trim());
            selectedDeliveryman.setLicenseNumber(licenseNumberField.getText().trim());
            selectedDeliveryman.setStatus(statusComboBox.getValue());

            System.out.println("Controller: Updating deliveryman: " + selectedDeliveryman);
            deliverymanService.updateDeliveryman(selectedDeliveryman.getId(), selectedDeliveryman);
            System.out.println("Controller: Deliveryman updated successfully");

            loadDeliverymen();
            clearFields();
            AlertUtil.showInformation("Success", "Deliveryman updated successfully!");
        } catch (Exception e) {
            System.err.println("Controller: Error updating deliveryman: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to update deliveryman: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Deliveryman selectedDeliveryman = deliverymanTable.getSelectionModel().getSelectedItem();
        if (selectedDeliveryman == null) {
            AlertUtil.showError("Error", "Please select a deliveryman to delete");
            return;
        }

        if (AlertUtil.showConfirmation("Confirm Delete",
                "Are you sure you want to delete this deliveryman?")) {
            try {
                System.out.println("Controller: Deleting deliveryman: " + selectedDeliveryman);
                deliverymanService.deleteDeliveryman(selectedDeliveryman.getId());
                System.out.println("Controller: Deliveryman deleted successfully");
                loadDeliverymen();
                clearFields();
                AlertUtil.showInformation("Success", "Deliveryman deleted successfully!");
            } catch (Exception e) {
                System.err.println("Controller: Error deleting deliveryman: " + e.getMessage());
                e.printStackTrace();
                AlertUtil.showError("Error", "Failed to delete deliveryman: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        String searchBy = searchByComboBox.getValue();
        String sortBy = sortByComboBox.getValue();
        boolean ascending = ascendingCheckBox.isSelected();

        try {
            System.out.println("Controller: Searching deliverymen - Query: " + query + ", SearchBy: " + searchBy + ", SortBy: " + sortBy + ", Ascending: " + ascending);
            List<Deliveryman> searchResults = deliverymanService.searchDeliverymen(query, searchBy, sortBy, ascending);
            deliverymanList.setAll(searchResults);
            System.out.println("Controller: Search completed, found " + searchResults.size() + " results");
        } catch (IOException e) {
            System.err.println("Controller: Error searching deliverymen: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Search Error", "An error occurred while searching for deliverymen: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        phoneField.clear();
        vehicleTypeField.clear();
        emailField.clear();
        licenseNumberField.clear();
        statusComboBox.setValue(null);
        deliverymanTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void validateInputs() {
        boolean isValid = !nameField.getText().trim().isEmpty() &&
                !phoneField.getText().trim().isEmpty() &&
                !vehicleTypeField.getText().trim().isEmpty() &&
                !emailField.getText().trim().isEmpty() &&
                !licenseNumberField.getText().trim().isEmpty() &&
                statusComboBox.getValue() != null;

        addButton.setDisable(!isValid);
        if (deliverymanTable.getSelectionModel().getSelectedItem() != null) {
            updateButton.setDisable(!isValid);
        }
    }
}