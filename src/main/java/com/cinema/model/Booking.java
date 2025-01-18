package com.cinema.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    private int id;
    private User user;
    private Screen screen;
    private LocalDateTime bookingTime;
    private List<String> reservedSeats;
    private double totalPrice;

    public Booking() {
        this.reservedSeats = new ArrayList<>();
        this.bookingTime = LocalDateTime.now();
    }

    public Booking(User user, Screen screen, List<String> reservedSeats) {
        this();
        this.user = user;
        this.screen = screen;
        this.reservedSeats = reservedSeats;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public List<String> getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(List<String> reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void calculateTotalPrice(double pricePerSeat) {
        this.totalPrice = this.reservedSeats.size() * pricePerSeat;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", user=" + (user != null ? user.getName() : "null") +
                ", screen=" + (screen != null ? screen.getMovieName() : "null") +
                ", bookingTime=" + bookingTime +
                ", reservedSeats=" + reservedSeats +
                ", totalPrice=" + totalPrice +
                '}';
    }
}