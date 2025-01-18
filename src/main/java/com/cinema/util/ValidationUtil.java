package com.cinema.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && PHONE_PATTERN.matcher(phoneNumber).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 100;
    }

    public static boolean isValidAddress(String address) {
        return address != null && !address.trim().isEmpty() && address.length() <= 200;
    }

    public static boolean isValidMovieName(String movieName) {
        return movieName != null && !movieName.trim().isEmpty() && movieName.length() <= 100;
    }

    public static boolean isValidSeatNumber(String seatNumber) {
        return seatNumber != null && seatNumber.matches("^[A-Z]\\d{1,2}$");
    }
}