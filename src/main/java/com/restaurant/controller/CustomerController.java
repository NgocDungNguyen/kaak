package com.restaurant.controller;

import com.restaurant.model.Customer;
import com.restaurant.service.CustomerService;
import com.restaurant.service.CustomerServiceImpl;
import com.restaurant.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> idColumn;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, String> addressColumn;

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchByComboBox;
    @FXML private ComboBox<String> sortByComboBox;
    @FXML private CheckBox ascendingCheckBox;

    private final CustomerService customerService;
    private ObservableList<Customer> customerList;

    public CustomerController() {
        this.customerService = new CustomerServiceImpl();
        this.customerList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadCustomers();
        setupEventHandlers();
        setupButtonStates();
        setupComboBoxes();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
    }

    private void loadCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            customerList.clear();
            customerList.addAll(customers);
            customerTable.setItems(customerList);
        } catch (SQLException e) {
            AlertUtil.showError("Error", "Failed to load customers: " + e.getMessage());
        }
    }

    private void setupEventHandlers() {
        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                phoneField.setText(newSelection.getPhone());
                emailField.setText(newSelection.getEmail());
                addressField.setText(newSelection.getAddress());
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });

        nameField.textProperty().addListener((obs, old, newValue) -> validateInputs());
        phoneField.textProperty().addListener((obs, old, newValue) -> validateInputs());
        emailField.textProperty().addListener((obs, old, newValue) -> validateInputs());
        addressField.textProperty().addListener((obs, old, newValue) -> validateInputs());
    }

    private void setupButtonStates() {
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void setupComboBoxes() {
        searchByComboBox.setItems(FXCollections.observableArrayList("name", "phone"));
        sortByComboBox.setItems(FXCollections.observableArrayList("name", "phone"));
    }

    @FXML
    private void handleAdd() {
        Customer customer = new Customer();
        customer.setName(nameField.getText().trim());
        customer.setPhone(phoneField.getText().trim());
        customer.setEmail(emailField.getText().trim());
        customer.setAddress(addressField.getText().trim());
        customer.setJoinDate(LocalDate.now());
        customer.setTotalOrders(0);

        try {
            customerService.addCustomer(customer);
            loadCustomers();
            clearFields();
            AlertUtil.showInformation("Success", "Customer added successfully!");
        } catch (SQLException e) {
            AlertUtil.showError("Error", "Failed to add customer: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            AlertUtil.showError("Error", "Please select a customer to update");
            return;
        }

        selectedCustomer.setName(nameField.getText().trim());
        selectedCustomer.setPhone(phoneField.getText().trim());
        selectedCustomer.setEmail(emailField.getText().trim());
        selectedCustomer.setAddress(addressField.getText().trim());

        try {
            customerService.updateCustomer(selectedCustomer.getId(), selectedCustomer);
            loadCustomers();
            clearFields();
            AlertUtil.showInformation("Success", "Customer updated successfully!");
        } catch (SQLException e) {
            AlertUtil.showError("Error", "Failed to update customer: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            AlertUtil.showError("Error", "Please select a customer to delete");
            return;
        }

        if (AlertUtil.showConfirmation("Confirm Delete", "Are you sure you want to delete this customer?")) {
            try {
                customerService.deleteCustomer(selectedCustomer.getId());
                loadCustomers();
                clearFields();
                AlertUtil.showInformation("Success", "Customer deleted successfully!");
            } catch (SQLException e) {
                AlertUtil.showError("Error", "Failed to delete customer: " + e.getMessage());
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
            List<Customer> searchResults = customerService.searchCustomers(query, searchBy, sortBy, ascending);
            customerList.setAll(searchResults);
        } catch (SQLException e) {
            AlertUtil.showError("Search Error", "An error occurred while searching for customers: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        addressField.clear();
        customerTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void validateInputs() {
        boolean isValid = !nameField.getText().trim().isEmpty() &&
                !phoneField.getText().trim().isEmpty() &&
                !emailField.getText().trim().isEmpty() &&
                !addressField.getText().trim().isEmpty();

        addButton.setDisable(!isValid);
        if (customerTable.getSelectionModel().getSelectedItem() != null) {
            updateButton.setDisable(!isValid);
        }
    }
}