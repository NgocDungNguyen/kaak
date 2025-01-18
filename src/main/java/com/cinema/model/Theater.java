package com.cinema.model;

import java.util.ArrayList;
import java.util.List;

public class Theater {
    private int id;
    private String name;
    private String address;
    private List<Screen> screens;

    public Theater() {
        this.screens = new ArrayList<>();
    }

    public Theater(String name, String address) {
        this();
        this.name = name;
        this.address = address;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Screen> getScreens() {
        return screens;
    }

    public void setScreens(List<Screen> screens) {
        this.screens = screens;
    }

    public void addScreen(Screen screen) {
        this.screens.add(screen);
        screen.setTheater(this);
    }

    @Override
    public String toString() {
        return "Theater{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}