package com.cinema.controller;

import com.cinema.model.Booking;
import com.cinema.model.User;
import com.cinema.model.Screen;
import com.cinema.service.BookingService;
import com.cinema.service.UserService;
import com.cinema.service.ScreenService;
import com.cinema.service.impl.BookingServiceImpl;
import com.cinema.service.impl.UserServiceImpl;
import com.cinema.service.impl.ScreenServiceImpl;
import com.cinema.util.AlertUtil;
import com.cinema.exception.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookingController {
    private static final Logger LOGGER = Logger.getLogger(BookingController.class.getName());

    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, Integer> idColumn;
    @FXML private TableColumn<Booking, String> userColumn;
    @FXML private TableColumn<Booking, String> movieColumn;
    @FXML private TableColumn<Booking, LocalDateTime> showTimeColumn;
    @FXML private TableColumn<Booking, String> seatsColumn;
    @FXML private TableColumn<Booking, Double> totalPriceColumn;

    @FXML private ComboBox<User> userComboBox;
    @FXML private ComboBox<Screen> screenComboBox;
    @FXML private TextField seatsField;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchByComboBox;
    @FXML private ComboBox<String> sortByComboBox;
    @FXML private CheckBox ascendingCheckBox;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private BookingService bookingService;
    private UserService userService;
    private ScreenService screenService;
    private ObservableList<Booking> bookingList;

    @FXML
    public void initialize() {
        bookingService = new BookingServiceImpl();
        userService = new UserServiceImpl();
        screenService = new ScreenServiceImpl();
        bookingList = FXCollections.observableArrayList();

        setupTable();
        setupComboBoxes();
        loadBookings();
        loadUsers();
        loadScreens();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getName()));
        movieColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getScreen().getMovieName()));
        showTimeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getScreen().getShowTime()));
        seatsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.join(", ", cellData.getValue().getReservedSeats())));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        bookingTable.setItems(bookingList);
    }

    private void setupComboBoxes() {
        searchByComboBox.setItems(FXCollections.observableArrayList("id", "user", "movie"));
        sortByComboBox.setItems(FXCollections.observableArrayList("id", "showTime", "totalPrice"));
    }

    private void loadBookings() {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            bookingList.setAll(bookings);
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Failed to load bookings", e);
            AlertUtil.showError("Error", "Failed to load bookings: " + e.getMessage());
        }
    }

    private void loadUsers() {
        try {
            List<User> users = userService.getAllUsers();
            userComboBox.setItems(FXCollections.observableArrayList(users));
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Failed to load users", e);
            AlertUtil.showError("Error", "Failed to load users: " + e.getMessage());
        }
    }

    private void loadScreens() {
        try {
            List<Screen> screens = screenService.getAllScreens();
            screenComboBox.setItems(FXCollections.observableArrayList(screens));
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Failed to load screens", e);
            AlertUtil.showError("Error", "Failed to load screens: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        User selectedUser = userComboBox.getValue();
        Screen selectedScreen = screenComboBox.getValue();
        String seatsInput = seatsField.getText();

        if (selectedUser == null || selectedScreen == null || seatsInput.isEmpty()) {
            AlertUtil.showError("Error", "Please fill in all fields");
            return;
        }

        List<String> seats = Arrays.asList(seatsInput.split(","));
        Booking booking = new Booking();
        booking.setUser(selectedUser);
        booking.setScreen(selectedScreen);
        booking.setReservedSeats(seats);
        booking.setBookingTime(LocalDateTime.now());

        try {
            bookingService.addBooking(booking);
            loadBookings();
            clearFields();
            AlertUtil.showInfo("Success", "Booking added successfully");
        } catch (ValidationException e) {
            AlertUtil.showError("Validation Error", e.getMessage());
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Failed to add booking", e);
            AlertUtil.showError("Error", "Failed to add booking: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Booking selectedBooking = bookingTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            AlertUtil.showError("Error", "Please select a booking to update");
            return;
        }

        User selectedUser = userComboBox.getValue();
        Screen selectedScreen = screenComboBox.getValue();
        String seatsInput = seatsField.getText();

        if (selectedUser == null || selectedScreen == null || seatsInput.isEmpty()) {
            AlertUtil.showError("Error", "Please fill in all fields");
            return;
        }

        List<String> seats = Arrays.asList(seatsInput.split(","));
        selectedBooking.setUser(selectedUser);
        selectedBooking.setScreen(selectedScreen);
        selectedBooking.setReservedSeats(seats);

        try {
            bookingService.updateBooking(selectedBooking);
            loadBookings();
            clearFields();
            AlertUtil.showInfo("Success", "Booking updated successfully");
        } catch (ValidationException e) {
            AlertUtil.showError("Validation Error", e.getMessage());
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Failed to update booking", e);
            AlertUtil.showError("Error", "Failed to update booking: " + e.getMessage());
        } catch (NotFoundException e) {
            AlertUtil.showError("Error", "Booking not found: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Booking selectedBooking = bookingTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            AlertUtil.showError("Error", "Please select a booking to delete");
            return;
        }

        try {
            bookingService.deleteBooking(selectedBooking.getId());
            loadBookings();
            clearFields();
            AlertUtil.showInfo("Success", "Booking deleted successfully");
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete booking", e);
            AlertUtil.showError("Error", "Failed to delete booking: " + e.getMessage());
        } catch (NotFoundException e) {
            AlertUtil.showError("Error", "Booking not found: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText();
        String searchBy = searchByComboBox.getValue();
        String sortBy = sortByComboBox.getValue();
        boolean ascending = ascendingCheckBox.isSelected();

        try {
            List<Booking> searchResults = bookingService.searchBookings(searchTerm, searchBy, sortBy, ascending);
            bookingList.setAll(searchResults);
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Failed to search bookings", e);
            AlertUtil.showError("Error", "Failed to search bookings: " + e.getMessage());
        }
    }

    private void clearFields() {
        userComboBox.setValue(null);
        screenComboBox.setValue(null);
        seatsField.clear();
    }
}
