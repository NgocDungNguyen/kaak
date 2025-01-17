package com.restaurant.dao;

import com.restaurant.dao.interfaces.GenericDAO;
import com.restaurant.model.Order;
import com.restaurant.model.Item;
import com.restaurant.model.Customer;
import com.restaurant.model.Deliveryman;
import com.restaurant.util.CSVHandler;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OrderDAO implements GenericDAO<Order, String> {
    private static final String ORDERS_CSV_PATH = "data/orders.csv";
    private static final String ORDER_ITEMS_CSV_PATH = "data/order_items.csv";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CustomerDAO customerDAO;
    private final DeliverymanDAO deliverymanDAO;
    private final ItemDAO itemDAO;

    public OrderDAO() {
        this.customerDAO = new CustomerDAO();
        this.deliverymanDAO = new DeliverymanDAO();
        this.itemDAO = new ItemDAO();
        try {
            String[] orderHeaders = {"id", "customerId", "deliverymanId", "orderDate", "status", "totalAmount", "deliveryAddress", "specialInstructions", "paymentMethod", "deliveryTime"};
            CSVHandler.validateCSVStructure(ORDERS_CSV_PATH, orderHeaders);

            String[] orderItemHeaders = {"orderId", "itemId", "quantity"};
            CSVHandler.validateCSVStructure(ORDER_ITEMS_CSV_PATH, orderItemHeaders);
        } catch (IOException e) {
            System.err.println("Error initializing OrderDAO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Order create(Order order) throws IOException {
        System.out.println("DAO: Creating order");
        order.setId(UUID.randomUUID().toString());
        List<Order> orders = findAll();
        orders.add(order);
        saveOrders(orders);
        saveOrderItems(order);
        System.out.println("DAO: Order created and saved");
        return order;
    }

    @Override
    public void update(Order order) throws IOException {
        System.out.println("DAO: Updating order");
        List<Order> orders = findAll();
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId().equals(order.getId())) {
                orders.set(i, order);
                break;
            }
        }
        saveOrders(orders);
        saveOrderItems(order);
        System.out.println("DAO: Order updated and saved");
    }

    @Override
    public void delete(String id) throws IOException {
        System.out.println("DAO: Deleting order");
        List<Order> orders = findAll();
        orders.removeIf(order -> order.getId().equals(id));
        saveOrders(orders);
        deleteOrderItems(id);
        System.out.println("DAO: Order deleted and changes saved");
    }

    @Override
    public List<Order> findAll() throws IOException {
        System.out.println("DAO: Finding all orders");
        List<Order> orders = new ArrayList<>();
        File file = new File(ORDERS_CSV_PATH);

        if (!file.exists() || !file.isFile()) {
            System.out.println("DAO: Orders file does not exist");
            return orders;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return orders;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Order order = parseOrderFromCsv(line);
                    loadOrderItems(order);
                    orders.add(order);
                } catch (Exception e) {
                    System.err.println("Error parsing order: " + e.getMessage());
                }
            }
        }
        System.out.println("DAO: Found " + orders.size() + " orders");
        return orders;
    }

    @Override
    public Order findById(String id) throws IOException {
        System.out.println("DAO: Finding order by ID: " + id);
        return findAll().stream()
                .filter(order -> order.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private Order parseOrderFromCsv(String line) throws IOException {
        String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (data.length < 10) {
            throw new IOException("Invalid CSV line format: insufficient columns");
        }

        try {
            Order order = new Order();
            order.setId(data[0]);
            order.setCustomer(customerDAO.findById(data[1]));
            order.setDeliveryman(data[2].equals("null") ? null : deliverymanDAO.findById(data[2]));
            order.setOrderDate(LocalDateTime.parse(data[3], DATE_TIME_FORMATTER));
            order.setStatus(data[4]);
            order.setTotalAmount(Double.parseDouble(data[5]));
            order.setDeliveryAddress(data[6].replace("\"", ""));
            order.setSpecialInstructions(data[7].replace("\"", ""));
            order.setPaymentMethod(data[8]);
            order.setDeliveryTime(data[9].equals("null") ? null : LocalDateTime.parse(data[9], DATE_TIME_FORMATTER));
            return order;
        } catch (Exception e) {
            throw new IOException("Error parsing order: " + e.getMessage());
        }
    }

    private void saveOrders(List<Order> orders) throws IOException {
        System.out.println("DAO: Saving orders to CSV: " + ORDERS_CSV_PATH);
        try (PrintWriter writer = new PrintWriter(new FileWriter(ORDERS_CSV_PATH))) {
            writer.println("id,customerId,deliverymanId,orderDate,status,totalAmount,deliveryAddress,specialInstructions,paymentMethod,deliveryTime");
            for (Order order : orders) {
                writer.println(String.format("%s,%s,%s,%s,%s,%.2f,\"%s\",\"%s\",%s,%s",
                        order.getId(),
                        order.getCustomer().getId(),
                        order.getDeliveryman() != null ? order.getDeliveryman().getId() : "null",
                        order.getOrderDate().format(DATE_TIME_FORMATTER),
                        order.getStatus(),
                        order.getTotalAmount(),
                        order.getDeliveryAddress(),
                        order.getSpecialInstructions(),
                        order.getPaymentMethod(),
                        order.getDeliveryTime() != null ? order.getDeliveryTime().format(DATE_TIME_FORMATTER) : "null"
                ));
            }
        } catch (IOException e) {
            System.err.println("Error saving orders to CSV: " + e.getMessage());
            throw e;
        }
        System.out.println("DAO: Orders saved successfully");
    }

    private void saveOrderItems(Order order) throws IOException {
        System.out.println("DAO: Saving order items for order: " + order.getId());
        Map<String, List<String>> existingOrderItems = readOrderItems();
        existingOrderItems.remove(order.getId());

        List<String> orderItemLines = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry : order.getItems().entrySet()) {
            orderItemLines.add(String.format("%s,%d",
                    entry.getKey().getId(),
                    entry.getValue()
            ));
        }
        existingOrderItems.put(order.getId(), orderItemLines);

        writeOrderItems(existingOrderItems);
        System.out.println("DAO: Order items saved successfully");
    }

    private void deleteOrderItems(String orderId) throws IOException {
        System.out.println("DAO: Deleting order items for order: " + orderId);
        Map<String, List<String>> existingOrderItems = readOrderItems();
        existingOrderItems.remove(orderId);
        writeOrderItems(existingOrderItems);
        System.out.println("DAO: Order items deleted successfully");
    }

    private Map<String, List<String>> readOrderItems() throws IOException {
        System.out.println("DAO: Reading order items from CSV: " + ORDER_ITEMS_CSV_PATH);
        Map<String, List<String>> orderItems = new HashMap<>();
        File file = new File(ORDER_ITEMS_CSV_PATH);

        if (!file.exists()) {
            System.out.println("DAO: Order items file does not exist");
            return orderItems;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String orderId = data[0];
                orderItems.computeIfAbsent(orderId, k -> new ArrayList<>())
                        .add(String.join(",", Arrays.copyOfRange(data, 1, data.length)));
            }
        } catch (IOException e) {
            System.err.println("Error reading order items: " + e.getMessage());
            throw e;
        }
        System.out.println("DAO: Order items read successfully");
        return orderItems;
    }

    private void writeOrderItems(Map<String, List<String>> orderItems) throws IOException {
        System.out.println("DAO: Writing order items to CSV: " + ORDER_ITEMS_CSV_PATH);
        try (PrintWriter writer = new PrintWriter(new FileWriter(ORDER_ITEMS_CSV_PATH))) {
            writer.println("orderId,itemId,quantity");
            for (Map.Entry<String, List<String>> entry : orderItems.entrySet()) {
                String orderId = entry.getKey();
                for (String itemLine : entry.getValue()) {
                    writer.println(orderId + "," + itemLine);
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing order items to CSV: " + e.getMessage());
            throw e;
        }
        System.out.println("DAO: Order items written successfully");
    }

    private void loadOrderItems(Order order) throws IOException {
        System.out.println("DAO: Loading order items for order: " + order.getId());
        Map<String, List<String>> allOrderItems = readOrderItems();
        List<String> orderItemLines = allOrderItems.get(order.getId());

        if (orderItemLines != null) {
            for (String line : orderItemLines) {
                String[] data = line.split(",");
                Item item = itemDAO.findById(data[0]);
                int quantity = Integer.parseInt(data[1]);
                order.addItem(item, quantity);
            }
        }
        System.out.println("DAO: Order items loaded successfully");
    }
}