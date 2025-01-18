package com.cinema.controller;

import com.cinema.model.Screen;
import com.cinema.model.Theater;
import com.cinema.service.ScreenService;
import com.cinema.service.TheaterService;
import com.cinema.service.impl.ScreenServiceImpl;
import com.cinema.service.impl.TheaterServiceImpl;
import com.cinema.util.AlertUtil;
import com.cinema.exception.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScreenController {
    private static final Logger LOGGER = Logger.getLogger(ScreenController.class.getName());

    @FXML private TableView<Screen> screenTable;
    @FXML private TableColumn<Screen, Integer> idColumn;
    @FXML private TableColumn<Screen, String> movieNameColumn;
    @FXML private TableColumn<Screen, LocalDateTime> showTimeColumn;
    @FXML private TableColumn<Screen, String> theaterColumn;

    @FXML private TextField movieNameField;
    @FXML private DatePicker showDatePicker;
    @FXML private TextField showTimeField;
    @FXML private ComboBox<Theater> theaterComboBox;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortByComboBox;
    @FXML private CheckBox ascendingCheckBox;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private ScreenService screenService;
    private TheaterService theaterService;
    private ObservableList<Screen> screenList;

    @FXML
    public void initialize() {
        screenService = new ScreenServiceImpl();
        theaterService = new TheaterServiceImpl();
        screenList = FXCollections.observableArrayList();

        setupTable();
        setupComboBoxes();
        loadScreens();
        loadTheaters();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        movieNameColumn.setCellValueFactory(new PropertyValueFactory<>("movieName"));
        showTimeColumn.setCellValueFactory(new PropertyValueFactory<>("showTime"));
        theaterColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTheater().getName()));
        screenTable.setItems(screenList);
    }

    private void setupComboBoxes() {
        sortByComboBox.setItems(FXCollections.observableArrayList("movieName", "showTime"));
    }

    private void loadScreens() {
        try {
            List<Screen> screens = screenService.getAllScreens();
            screenList.setAll(screens);
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Failed to load screens", e);
            AlertUtil.showError("Error", "Failed to load screens: " + e.getMessage());
        }
    }

    private void loadTheaters() {
        try {
            List<Theater> theaters = theaterService.getAllTheaters();
            theaterComboBox.setItems(FXCollections.observableArrayList(theaters));
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Failed to load theaters", e);
            AlertUtil.showError("Error", "Failed to load theaters: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        String movieName = movieNameField.getText();
        LocalDate showDate = showDatePicker.getValue();
        String showTimeString = showTimeField.getText();
        Theater selectedTheater = theaterComboBox.getValue();

        if (movieName.isEmpty() || showDate == null || showTimeString.isEmpty() || selectedTheater == null) {
            AlertUtil.showError("Error", "Please fill in all fields");
            return;
        }

        try {
            LocalTime showTime = LocalTime.parse(showTimeString);
            LocalDateTime showDateTime = LocalDateTime.of(showDate, showTime);

            Screen screen = new Screen();
            screen.setMovieName(movieName);
            screen.setShowTime(showDateTime);
            screen.setTheater(selectedTheater);

            screenService.addScreen(screen);
            loadScreens();
            clearFields();
            AlertUtil.showInfo("Success", "Screen added successfully");
        } catch (ValidationException e) {
            AlertUtil.showError("Validation Error", e.getMessage());
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Failed to add screen", e);
            AlertUtil.showError("Error", "Failed to add screen: " + e.getMessage());
        } catch (Exception e) {
            AlertUtil.showError("Error", "Invalid time format. Please use HH:mm");
        }
    }

    @FXML
    private void handleUpdate() {
        Screen selectedScreen = screenTable.getSelectionModel().getSelectedItem();
        if (selectedScreen == null) {
            AlertUtil.showError("Error", "Please select a screen to update");
            return;
        }

        String movieName = movieNameField.getText();
        LocalDate showDate = showDatePicker.getValue();
        String showTimeString = showTimeField.getText();
        Theater selectedTheater = theaterComboBox.getValue();

        if (movieName.isEmpty() || showDate == null || showTimeString.isEmpty() || selectedTheater == null) {
            AlertUtil.showError("Error", "Please fill in all fields");
            return;
        }

        try {
            LocalTime showTime = LocalTime.parse(showTimeString);
            LocalDateTime showDateTime = LocalDateTime.of(showDate, showTime);

            selectedScreen.setMovieName(movieName);
            selectedScreen.setShowTime(showDateTime);
            selectedScreen.setTheater(selectedTheater);

            screenService.updateScreen(selectedScreen);
            loadScreens();
            clearFields();
            AlertUtil.showInfo("Success", "Screen updated successfully");
        } catch (ValidationException e) {
            AlertUtil.showError("Validation Error", e.getMessage());
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Failed to update screen", e);
            AlertUtil.showError("Error", "Failed to update screen: " + e.getMessage());
        } catch (NotFoundException e) {
            AlertUtil.showError("Error", "Screen not found: " + e.getMessage());
        } catch (Exception e) {
            AlertUtil.showError("Error", "Invalid time format. Please use HH:mm");
        }
    }

    @FXML
    private void handleDelete() {
        Screen selectedScreen = screenTable.getSelectionModel().getSelectedItem();
        if (selectedScreen == null) {
            AlertUtil.showError("Error", "Please select a screen to delete");
            return;
        }

        try {
            screenService.deleteScreen(selectedScreen.getId());
            loadScreens();
            clearFields();
            AlertUtil.showInfo("Success", "Screen deleted successfully");
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete screen", e);
            AlertUtil.showError("Error", "Failed to delete screen: " + e.getMessage());
        } catch (NotFoundException e) {
            AlertUtil.showError("Error", "Screen not found: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String movieName = searchField.getText();
        String sortBy = sortByComboBox.getValue();
        boolean ascending = ascendingCheckBox.isSelected();

        try {
            List<Screen> searchResults = screenService.searchScreens(movieName, sortBy, ascending);
            screenList.setAll(searchResults);
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Failed to search screens", e);
            AlertUtil.showError("Error", "Failed to search screens: " + e.getMessage());
        }
    }

    private void clearFields() {
        movieNameField.clear();
        showDatePicker.setValue(null);
        showTimeField.clear();
        theaterComboBox.setValue(null);
    }
}