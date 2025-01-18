package com.cinema.controller;

import com.cinema.model.Theater;
import com.cinema.service.TheaterService;
import com.cinema.service.impl.TheaterServiceImpl;
import com.cinema.util.AlertUtil;
import com.cinema.exception.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class TheaterController {

    @FXML private TableView<Theater> theaterTable;
    @FXML private TableColumn<Theater, Integer> idColumn;
    @FXML private TableColumn<Theater, String> nameColumn;
    @FXML private TableColumn<Theater, String> addressColumn;

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchByComboBox;
    @FXML private CheckBox ascendingCheckBox;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private TheaterService theaterService;
    private ObservableList<Theater> theaterList;

    @FXML
    public void initialize() {
        theaterService = new TheaterServiceImpl();
        theaterList = FXCollections.observableArrayList();

        setupTable();
        setupComboBoxes();
        loadTheaters();
        setupSelectionListener();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        theaterTable.setItems(theaterList);
    }

    private void setupComboBoxes() {
        searchByComboBox.setItems(FXCollections.observableArrayList("name", "address"));
    }

    private void loadTheaters() {
        try {
            List<Theater> theaters = theaterService.getAllTheaters();
            theaterList.setAll(theaters);
        } catch (DatabaseException e) {
            AlertUtil.showError("Error", "Failed to load theaters: " + e.getMessage());
        }
    }

    private void setupSelectionListener() {
        theaterTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                addressField.setText(newSelection.getAddress());
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                clearFields();
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty() || address.isEmpty()) {
            AlertUtil.showError("Error", "Please fill in all fields");
            return;
        }

        Theater theater = new Theater();
        theater.setName(name);
        theater.setAddress(address);

        try {
            theaterService.addTheater(theater);
            loadTheaters();
            clearFields();
            AlertUtil.showInfo("Success", "Theater added successfully");
        } catch (ValidationException e) {
            AlertUtil.showError("Validation Error", e.getMessage());
        } catch (DatabaseException e) {
            AlertUtil.showError("Error", "Failed to add theater: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Theater selectedTheater = theaterTable.getSelectionModel().getSelectedItem();
        if (selectedTheater == null) {
            AlertUtil.showError("Error", "Please select a theater to update");
            return;
        }

        String name = nameField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty() || address.isEmpty()) {
            AlertUtil.showError("Error", "Please fill in all fields");
            return;
        }

        selectedTheater.setName(name);
        selectedTheater.setAddress(address);

        try {
            theaterService.updateTheater(selectedTheater);
            loadTheaters();
            clearFields();
            AlertUtil.showInfo("Success", "Theater updated successfully");
        } catch (ValidationException e) {
            AlertUtil.showError("Validation Error", e.getMessage());
        } catch (DatabaseException e) {
            AlertUtil.showError("Error", "Failed to update theater: " + e.getMessage());
        } catch (NotFoundException e) {
            AlertUtil.showError("Error", "Theater not found: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Theater selectedTheater = theaterTable.getSelectionModel().getSelectedItem();
        if (selectedTheater == null) {
            AlertUtil.showError("Error", "Please select a theater to delete");
            return;
        }

        try {
            theaterService.deleteTheater(selectedTheater.getId());
            loadTheaters();
            clearFields();
            AlertUtil.showInfo("Success", "Theater deleted successfully");
        } catch (DatabaseException e) {
            AlertUtil.showError("Error", "Failed to delete theater: " + e.getMessage());
        } catch (NotFoundException e) {
            AlertUtil.showError("Error", "Theater not found: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        String searchBy = searchByComboBox.getValue();
        boolean ascending = ascendingCheckBox.isSelected();

        try {
            List<Theater> searchResults = theaterService.searchTheaters(searchTerm, searchBy, ascending);
            theaterList.setAll(searchResults);
        } catch (DatabaseException e) {
            AlertUtil.showError("Error", "Failed to search theaters: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        addressField.clear();
        theaterTable.getSelectionModel().clearSelection();
    }
}
