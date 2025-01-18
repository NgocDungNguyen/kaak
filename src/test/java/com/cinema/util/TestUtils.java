package com.cinema.util;

import com.cinema.model.Booking;
import com.cinema.model.Screen;
import com.cinema.model.Theater;
import com.cinema.model.User;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;

public class TestUtils {

    public static void setPrivateField(Object object, String fieldName, Object fieldValue) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, fieldValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error setting private field", e);
        }
    }

    public static User createTestUser() {
        User user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        return user;
    }

    public static Theater createTestTheater() {
        Theater theater = new Theater();
        theater.setId(1);
        theater.setName("Test Theater");
        theater.setAddress("123 Test St");
        return theater;
    }

    public static Screen createTestScreen() {
        Screen screen = new Screen();
        screen.setId(1);
        screen.setMovieName("Test Movie");
        screen.setShowTime(LocalDateTime.now().plusDays(1));
        screen.setTheater(createTestTheater());
        screen.setAvailableSeats(Arrays.asList("A1", "A2", "A3", "B1", "B2", "B3"));
        return screen;
    }

    public static Booking createTestBooking() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setUser(createTestUser());
        booking.setScreen(createTestScreen());
        booking.setBookingTime(LocalDateTime.now());
        booking.setReservedSeats(Arrays.asList("A1", "A2"));
        booking.setTotalPrice(20.0);
        return booking;
    }
}