package com.restaurant.controller;

import com.restaurant.model.*;
import com.restaurant.service.*;
import com.restaurant.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class OrderController implements Initializable {
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, String> orderIdColumn;
    @FXML private TableColumn<Order, String> customerColumn;
    @FXML private TableColumn<Order, String> deliverymanColumn;
    @FXML private TableColumn<Order, Double> totalColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TableColumn<Order, LocalDateTime> orderTimeColumn;

    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<Deliveryman> deliverymanComboBox;
    @FXML private ComboBox<Item> itemComboBox;
    @FXML private ListView<String> selectedItemsListView;
    @FXML private TextField quantityField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField deliveryAddressField;
    @FXML private TextArea specialInstructionsArea;
    @FXML private ComboBox<String> paymentMethodComboBox;
    @FXML private Label totalLabel;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchByComboBox;
    @FXML private ComboBox<String> sortByComboBox;
    @FXML private CheckBox ascendingCheckBox;

    @FXML private Button addItemButton;
    @FXML private Button removeItemButton;
    @FXML private Button createOrderButton;
    @FXML private Button updateOrderButton;
    @FXML private Button deleteOrderButton;
    @FXML private Button clearButton;

    private final OrderService orderService;
    private final CustomerService customerService;
    private final DeliverymanService deliverymanService;
    private final ItemService itemService;
    private ObservableList<Order> orderList;
    private ObservableList<String> selectedItems;

    public OrderController() {
        this.orderService = new OrderServiceImpl();
        this.customerService = new CustomerServiceImpl();
        this.deliverymanService = new DeliverymanServiceImpl();
        this.itemService = new ItemServiceImpl();
        this.orderList = FXCollections.observableArrayList();
        this.selectedItems = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadOrders();
        setupComboBoxes();
        setupEventHandlers();
        setupButtonStates();
    }

    private void setupTableColumns() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerColumn.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue().getCustomer();
            return customer != null ? javafx.beans.binding.Bindings.createStringBinding(() -> customer.getName()) : null;
        });
        deliverymanColumn.setCellValueFactory(cellData -> {
            Deliveryman deliveryman = cellData.getValue().getDeliveryman();
            return deliveryman != null ? javafx.beans.binding.Bindings.createStringBinding(() -> deliveryman.getName()) : null;
        });
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        orderTimeColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
    }

    private void loadOrders() {
        try {
            System.out.println("Controller: Loading orders");
            List<Order> orders = orderService.getAllOrders();
            orderList.clear();
            orderList.addAll(orders);
            orderTable.setItems(orderList);
            System.out.println("Controller: Loaded " + orders.size() + " orders");
        } catch (Exception e) {
            System.err.println("Controller: Error loading orders: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to load orders: " + e.getMessage());
        }
    }

    private void setupComboBoxes() {
        try {
            customerComboBox.setItems(FXCollections.observableArrayList(customerService.getAllCustomers()));
            deliverymanComboBox.setItems(FXCollections.observableArrayList(deliverymanService.getAllDeliverymen()));
            itemComboBox.setItems(FXCollections.observableArrayList(itemService.getAllItems()));
            statusComboBox.setItems(FXCollections.observableArrayList("PENDING", "PREPARING", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED"));
            paymentMethodComboBox.setItems(FXCollections.observableArrayList("CASH", "CREDIT_CARD", "DEBIT_CARD"));
            searchByComboBox.setItems(FXCollections.observableArrayList("id", "customer", "status"));
            sortByComboBox.setItems(FXCollections.observableArrayList("id", "date", "total", "customer"));
        } catch (IOException e) {
            System.err.println("Controller: Error setting up combo boxes: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to load data: " + e.getMessage());
        }
    }

    private void setupEventHandlers() {
        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
                updateOrderButton.setDisable(false);
                deleteOrderButton.setDisable(false);
            }
        });

        addItemButton.setOnAction(e -> handleAddItem());
        removeItemButton.setOnAction(e -> handleRemoveItem());
        createOrderButton.setOnAction(e -> handleCreateOrder());
        updateOrderButton.setOnAction(e -> handleUpdateOrder());
        deleteOrderButton.setOnAction(e -> handleDeleteOrder());
        clearButton.setOnAction(e -> clearFields());
    }

    private void setupButtonStates() {
        updateOrderButton.setDisable(true);
        deleteOrderButton.setDisable(true);
    }

    @FXML
    private void handleAddItem() {
        Item selectedItem = itemComboBox.getValue();
        int quantity = Integer.parseInt(quantityField.getText());
        if (selectedItem != null && quantity > 0) {
            selectedItems.add(selectedItem.getName() + " x" + quantity);
            selectedItemsListView.setItems(selectedItems);
            updateTotal();
        }
    }

    @FXML
    private void handleRemoveItem() {
        String selectedItem = selectedItemsListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            selectedItems.remove(selectedItem);
            updateTotal();
        }
    }

    @FXML
    private void handleCreateOrder() {
        try {
            Order order = new Order();
            setOrderFields(order);
            System.out.println("Controller: Creating order: " + order);
            orderService.addOrder(order);
            System.out.println("Controller: Order created successfully");
            loadOrders();
            clearFields();
            AlertUtil.showInformation("Success", "Order created successfully");
        } catch (Exception e) {
            System.err.println("Controller: Error creating order: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to create order: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateOrder() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            AlertUtil.showError("Error", "Please select an order to update");
            return;
        }

        try {
            setOrderFields(selectedOrder);
            System.out.println("Controller: Updating order: " + selectedOrder);
            orderService.updateOrder(selectedOrder);
            System.out.println("Controller: Order updated successfully");
            loadOrders();
            clearFields();
            AlertUtil.showInformation("Success", "Order updated successfully");
        } catch (Exception e) {
            System.err.println("Controller: Error updating order: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to update order: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteOrder() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            AlertUtil.showError("Error", "Please select an order to delete");
            return;
        }

        if (AlertUtil.showConfirmation("Confirm Delete", "Are you sure you want to delete this order?")) {
            try {
                System.out.println("Controller: Deleting order: " + selectedOrder);
                orderService.deleteOrder(selectedOrder.getId());
                System.out.println("Controller: Order deleted successfully");
                loadOrders();
                clearFields();
                AlertUtil.showInformation("Success", "Order deleted successfully");
            } catch (Exception e) {
                System.err.println("Controller: Error deleting order: " + e.getMessage());
                e.printStackTrace();
                AlertUtil.showError("Error", "Failed to delete order: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        String searchBy = searchByComboBox.getValue();
        String sortBy = sortByComboBox.getValue();
        boolean ascending = ascendingCheckBox.isSelected();

        try {
            System.out.println("Controller: Searching orders - Query: " + query + ", SearchBy: " + searchBy + ", SortBy: " + sortBy + ", Ascending: " + ascending);
            List<Order> searchResults = orderService.searchOrders(query, searchBy, sortBy, ascending);
            orderList.setAll(searchResults);
            System.out.println("Controller: Search completed, found " + searchResults.size() + " results");
        } catch (IOException e) {
            System.err.println("Controller: Error searching orders: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Search Error", "An error occurred while searching for orders: " + e.getMessage());
        }
    }

    private void setOrderFields(Order order) {
        order.setCustomer(customerComboBox.getValue());
        order.setDeliveryman(deliverymanComboBox.getValue());
        order.setStatus(statusComboBox.getValue());
        order.setDeliveryAddress(deliveryAddressField.getText());
        order.setSpecialInstructions(specialInstructionsArea.getText());
        order.setPaymentMethod(paymentMethodComboBox.getValue());

        order.getItems().clear();
        for (String itemString : selectedItems) {
            String[] parts = itemString.split(" x");
            String itemName = parts[0];
            int quantity = Integer.parseInt(parts[1]);
            Item item = itemComboBox.getItems().stream()
                    .filter(i -> i.getName().equals(itemName))
                    .findFirst()
                    .orElse(null);
            if (item != null) {
                order.addItem(item, quantity);
            }
        }

        order.calculateTotal();
    }

    private void populateFields(Order order) {
        customerComboBox.setValue(order.getCustomer());
        deliverymanComboBox.setValue(order.getDeliveryman());
        statusComboBox.setValue(order.getStatus());
        deliveryAddressField.setText(order.getDeliveryAddress());
        specialInstructionsArea.setText(order.getSpecialInstructions());
        paymentMethodComboBox.setValue(order.getPaymentMethod());

        selectedItems.clear();
        for (Item item : order.getItems().keySet()) {
            selectedItems.add(item.getName() + " x" + order.getItems().get(item));
        }
        selectedItemsListView.setItems(selectedItems);

        updateTotal();
    }

    private void updateTotal() {
        double total = selectedItems.stream()
                .mapToDouble(itemString -> {
                    String[] parts = itemString.split(" x");
                    String itemName = parts[0];
                    int quantity = Integer.parseInt(parts[1]);
                    Item item = itemComboBox.getItems().stream()
                            .filter(i -> i.getName().equals(itemName))
                            .findFirst()
                            .orElse(null);
                    return item != null ? item.getPrice() * quantity : 0;
                })
                .sum();
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    @FXML
    private void clearFields() {
        customerComboBox.setValue(null);
        deliverymanComboBox.setValue(null);
        statusComboBox.setValue(null);
        deliveryAddressField.clear();
        specialInstructionsArea.clear();
        paymentMethodComboBox.setValue(null);
        selectedItems.clear();
        selectedItemsListView.setItems(selectedItems);
        itemComboBox.setValue(null);
        quantityField.clear();
        totalLabel.setText("Total: $0.00");
        orderTable.getSelectionModel().clearSelection();
        updateOrderButton.setDisable(true);
        deleteOrderButton.setDisable(true);
    }
}