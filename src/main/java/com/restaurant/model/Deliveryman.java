package com.restaurant.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Deliveryman {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String status;
    private String vehicleType;
    private String licenseNumber;
    private LocalDate joinDate;
    private int totalDeliveries;
    private double rating;
    private List<Order> orders;
    private boolean available;

    public Deliveryman() {
        this.orders = new ArrayList<>();
        this.joinDate = LocalDate.now();
        this.totalDeliveries = 0;
        this.rating = 0.0;
        this.available = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public int getTotalDeliveries() {
        return totalDeliveries;
    }

    public void setTotalDeliveries(int totalDeliveries) {
        this.totalDeliveries = totalDeliveries;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void addOrder(Order order) {
        this.orders.add(order);
        this.totalDeliveries++;
    }

    @Override
    public String toString() {
        return "Deliveryman{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", joinDate=" + joinDate +
                ", totalDeliveries=" + totalDeliveries +
                ", rating=" + rating +
                ", available=" + available +
                '}';
    }
}