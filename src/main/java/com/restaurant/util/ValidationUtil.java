package com.restaurant.util;

public class ValidationUtil {
    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2 && name.matches("[\\p{L}\\s]+");
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(emailRegex);
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }

    public static boolean isValidAddress(String address) {
        return address != null && address.trim().length() >= 5;
    }

    public static boolean isValidVehicle(String vehicle) {
        return vehicle != null && vehicle.trim().length() >= 2;
    }

    public static boolean isValidPrice(double price) {
        return price > 0;
    }

    public static boolean isValidCategory(String category) {
        return category != null && !category.trim().isEmpty();
    }
}