// OrderItem.java
package com.restaurant.model;

public class OrderItem {
    private String id;
    private String orderId;
    private String itemId;
    private int quantity;
    private double price;
    private String notes;

    // Constructors
    public OrderItem() {}

    public OrderItem(String itemId, int quantity, double price) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}