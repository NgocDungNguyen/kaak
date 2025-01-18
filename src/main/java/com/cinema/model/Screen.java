package com.cinema.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Screen {
    private int id;
    private String movieName;
    private LocalDateTime showTime;
    private List<String> availableSeats;
    private Theater theater;

    public Screen() {
        this.availableSeats = new ArrayList<>();
    }

    public Screen(String movieName, LocalDateTime showTime, Theater theater) {
        this();
        this.movieName = movieName;
        this.showTime = showTime;
        this.theater = theater;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public LocalDateTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalDateTime showTime) {
        this.showTime = showTime;
    }

    public List<String> getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(List<String> availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Theater getTheater() {
        return theater;
    }

    public void setTheater(Theater theater) {
        this.theater = theater;
    }

    @Override
    public String toString() {
        return "Screen{" +
                "id=" + id +
                ", movieName='" + movieName + '\'' +
                ", showTime=" + showTime +
                ", theater=" + (theater != null ? theater.getName() : "null") +
                '}';
    }
}