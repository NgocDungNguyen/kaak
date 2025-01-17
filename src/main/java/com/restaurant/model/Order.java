package com.restaurant.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Order {
    private String id;
    private Customer customer;
    private Deliveryman deliveryman;
    private LocalDateTime orderDate;
    private String status;
    private double totalAmount;
    private Map<Item, Integer> items;
    private String deliveryAddress;
    private String specialInstructions;
    private String paymentMethod;
    private LocalDateTime deliveryTime;

    public Order() {
        this.items = new HashMap<>();
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Deliveryman getDeliveryman() {
        return deliveryman;
    }

    public void setDeliveryman(Deliveryman deliveryman) {
        this.deliveryman = deliveryman;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Map<Item, Integer> getItems() {
        return items;
    }

    public void setItems(Map<Item, Integer> items) {
        this.items = items;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public void addItem(Item item, int quantity) {
        items.put(item, items.getOrDefault(item, 0) + quantity);
        calculateTotal();
    }

    public void removeItem(Item item) {
        items.remove(item);
        calculateTotal();
    }

    public void calculateTotal() {
        this.totalAmount = items.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", customer=" + (customer != null ? customer.getName() : "null") +
                ", deliveryman=" + (deliveryman != null ? deliveryman.getName() : "null") +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", items=" + items.size() +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", deliveryTime=" + deliveryTime +
                '}';
    }
}