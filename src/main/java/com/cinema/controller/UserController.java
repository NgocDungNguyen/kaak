package com.cinema.controller;

import com.cinema.model.User;
import com.cinema.service.UserService;
import com.cinema.service.impl.UserServiceImpl;
import com.cinema.util.AlertUtil;
import com.cinema.exception.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class UserController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> phoneColumn;

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchByComboBox;
    @FXML private ComboBox<String> sortByComboBox;
    @FXML private CheckBox ascendingCheckBox;

    @FXML private Button addButton;
    @FXML private Button updateButton;

    private UserService userService;
    private ObservableList<User> userList;

    @FXML
    public void initialize() {
        userService = new UserServiceImpl();
        userList = FXCollections.observableArrayList();

        setupTable();
        setupComboBoxes();
        loadUsers();
        setupSelectionListener();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        userTable.setItems(userList);
    }

    private void setupComboBoxes() {
        searchByComboBox.setItems(FXCollections.observableArrayList("name", "email", "phone"));
        sortByComboBox.setItems(FXCollections.observableArrayList("name", "email", "phone"));
    }

    private void loadUsers() {
        try {
            List<User> users = userService.getAllUsers();
            userList.setAll(users);
        } catch (DatabaseException e) {
            AlertUtil.showError("Error", "Failed to load users: " + e.getMessage());
        }
    }

    private void setupSelectionListener() {
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                emailField.setText(newSelection.getEmail());
                phoneField.setText(newSelection.getPhoneNumber());
                updateButton.setDisable(false);
            } else {
                clearFields();
                updateButton.setDisable(true);
            }
        });
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            AlertUtil.showError("Error", "Please fill in all fields");
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNumber(phone);

        try {
            userService.addUser(user);
            loadUsers();
            clearFields();
            AlertUtil.showInfo("Success", "User added successfully");
        } catch (ValidationException e) {
            AlertUtil.showError("Validation Error", e.getMessage());
        } catch (DatabaseException e) {
            AlertUtil.showError("Error", "Failed to add user: " + e.getMessage());
        } catch (DuplicateEntityException e) {
            AlertUtil.showError("Error", "User with this email already exists");
        }
    }

    @FXML
    private void handleUpdate() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertUtil.showError("Error", "Please select a user to update");
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            AlertUtil.showError("Error", "Please fill in all fields");
            return;
        }

        selectedUser.setName(name);
        selectedUser.setEmail(email);
        selectedUser.setPhoneNumber(phone);

        try {
            userService.updateUser(selectedUser);
            loadUsers();
            clearFields();
            AlertUtil.showInfo("Success", "User updated successfully");
        } catch (ValidationException e) {
            AlertUtil.showError("Validation Error", e.getMessage());
        } catch (DatabaseException e) {
            AlertUtil.showError("Error", "Failed to update user: " + e.getMessage());
        } catch (NotFoundException e) {
            AlertUtil.showError("Error", "User not found: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        String searchBy = searchByComboBox.getValue();
        String sortBy = sortByComboBox.getValue();
        boolean ascending = ascendingCheckBox.isSelected();

        try {
            List<User> searchResults = userService.searchUsers(searchTerm, searchBy, sortBy, ascending);
            userList.setAll(searchResults);
        } catch (DatabaseException e) {
            AlertUtil.showError("Error", "Failed to search users: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        userTable.getSelectionModel().clearSelection();
    }
}