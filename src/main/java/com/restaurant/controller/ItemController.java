package com.restaurant.controller;

import com.restaurant.model.Item;
import com.restaurant.service.ItemService;
import com.restaurant.service.ItemServiceImpl;
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

public class ItemController implements Initializable {
    @FXML private TableView<Item> itemTable;
    @FXML private TableColumn<Item, String> idColumn;
    @FXML private TableColumn<Item, String> nameColumn;
    @FXML private TableColumn<Item, String> categoryColumn;
    @FXML private TableColumn<Item, Double> priceColumn;

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private TextField preparationTimeField;
    @FXML private CheckBox availableCheckBox;
    @FXML private TextField imageUrlField;
    @FXML private TextField caloriesField;
    @FXML private Slider spicyLevelSlider;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchByComboBox;
    @FXML private ComboBox<String> sortByComboBox;
    @FXML private CheckBox ascendingCheckBox;

    private final ItemService itemService;
    private ObservableList<Item> itemList;

    public ItemController() {
        this.itemService = new ItemServiceImpl();
        this.itemList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadItems();
        setupEventHandlers();
        setupButtonStates();
        setupComboBoxes();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    private void loadItems() {
        try {
            System.out.println("Controller: Loading items");
            List<Item> items = itemService.getAllItems();
            itemList.clear();
            itemList.addAll(items);
            itemTable.setItems(itemList);
            System.out.println("Controller: Loaded " + items.size() + " items");
        } catch (Exception e) {
            System.err.println("Controller: Error loading items: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to load items: " + e.getMessage());
        }
    }

    private void setupEventHandlers() {
        itemTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                priceField.setText(String.valueOf(newSelection.getPrice()));
                categoryComboBox.setValue(newSelection.getCategory());
                descriptionArea.setText(newSelection.getDescription());
                preparationTimeField.setText(String.valueOf(newSelection.getPreparationTime()));
                availableCheckBox.setSelected(newSelection.isAvailable());
                imageUrlField.setText(newSelection.getImageUrl());
                caloriesField.setText(String.valueOf(newSelection.getCalories()));
                spicyLevelSlider.setValue(newSelection.getSpicyLevel());
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });

        nameField.textProperty().addListener((obs, old, newValue) -> validateInputs());
        priceField.textProperty().addListener((obs, old, newValue) -> validateInputs());
        categoryComboBox.valueProperty().addListener((obs, old, newValue) -> validateInputs());
    }

    private void setupButtonStates() {
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void setupComboBoxes() {
        categoryComboBox.setItems(FXCollections.observableArrayList("Main Course", "Appetizer", "Dessert", "Beverage"));
        searchByComboBox.setItems(FXCollections.observableArrayList("name", "price"));
        sortByComboBox.setItems(FXCollections.observableArrayList("name", "price"));
    }

    @FXML
    private void handleAdd() {
        try {
            Item item = new Item();
            setItemFields(item);

            System.out.println("Controller: Adding item: " + item);
            itemService.addItem(item);
            System.out.println("Controller: Item added successfully");

            loadItems();
            clearFields();
            AlertUtil.showInformation("Success", "Item added successfully!");
        } catch (Exception e) {
            System.err.println("Controller: Error adding item: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to add item: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Item selectedItem = itemTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            AlertUtil.showError("Error", "Please select an item to update");
            return;
        }

        try {
            setItemFields(selectedItem);

            System.out.println("Controller: Updating item: " + selectedItem);
            itemService.updateItem(selectedItem.getId(), selectedItem);
            System.out.println("Controller: Item updated successfully");

            loadItems();
            clearFields();
            AlertUtil.showInformation("Success", "Item updated successfully!");
        } catch (Exception e) {
            System.err.println("Controller: Error updating item: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to update item: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Item selectedItem = itemTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            AlertUtil.showError("Error", "Please select an item to delete");
            return;
        }

        if (AlertUtil.showConfirmation("Confirm Delete", "Are you sure you want to delete this item?")) {
            try {
                System.out.println("Controller: Deleting item: " + selectedItem);
                itemService.deleteItem(selectedItem.getId());
                System.out.println("Controller: Item deleted successfully");
                loadItems();
                clearFields();
                AlertUtil.showInformation("Success", "Item deleted successfully!");
            } catch (Exception e) {
                System.err.println("Controller: Error deleting item: " + e.getMessage());
                e.printStackTrace();
                AlertUtil.showError("Error", "Failed to delete item: " + e.getMessage());
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
            System.out.println("Controller: Searching items - Query: " + query + ", SearchBy: " + searchBy + ", SortBy: " + sortBy + ", Ascending: " + ascending);
            List<Item> searchResults = itemService.searchItems(query, searchBy, sortBy, ascending);
            itemList.setAll(searchResults);
            System.out.println("Controller: Search completed, found " + searchResults.size() + " results");
        } catch (IOException e) {
            System.err.println("Controller: Error searching items: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Search Error", "An error occurred while searching for items: " + e.getMessage());
        }
    }

    private void setItemFields(Item item) {
        item.setName(nameField.getText().trim());
        item.setPrice(Double.parseDouble(priceField.getText().trim()));
        item.setCategory(categoryComboBox.getValue());
        item.setDescription(descriptionArea.getText().trim());
        item.setPreparationTime(Integer.parseInt(preparationTimeField.getText().trim()));
        item.setAvailable(availableCheckBox.isSelected());
        item.setImageUrl(imageUrlField.getText().trim());
        item.setCalories(Integer.parseInt(caloriesField.getText().trim()));
        item.setSpicyLevel((int) spicyLevelSlider.getValue());
    }

    private void clearFields() {
        nameField.clear();
        priceField.clear();
        categoryComboBox.setValue(null);
        descriptionArea.clear();
        preparationTimeField.clear();
        availableCheckBox.setSelected(true);
        imageUrlField.clear();
        caloriesField.clear();
        spicyLevelSlider.setValue(0);
        itemTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void validateInputs() {
        boolean isValid = !nameField.getText().trim().isEmpty() &&
                !priceField.getText().trim().isEmpty() &&
                categoryComboBox.getValue() != null;

        addButton.setDisable(!isValid);
        if (itemTable.getSelectionModel().getSelectedItem() != null) {
            updateButton.setDisable(!isValid);
        }
    }
}