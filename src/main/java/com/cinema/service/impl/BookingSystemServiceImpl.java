package com.cinema.service.impl;

import com.cinema.dao.BookingDAO;
import com.cinema.dao.ScreenDAO;
import com.cinema.dao.TheaterDAO;
import com.cinema.dao.UserDAO;
import com.cinema.exception.BookingException;
import com.cinema.exception.DatabaseException;
import com.cinema.exception.NotFoundException;
import com.cinema.model.Booking;
import com.cinema.model.Screen;
import com.cinema.model.Theater;
import com.cinema.model.User;
import com.cinema.service.BookingSystemService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingSystemServiceImpl implements BookingSystemService {
    private final TheaterDAO theaterDAO;
    private final ScreenDAO screenDAO;
    private final BookingDAO bookingDAO;
    private final UserDAO userDAO;

    public BookingSystemServiceImpl() {
        this.theaterDAO = new TheaterDAO();
        this.screenDAO = new ScreenDAO();
        this.bookingDAO = new BookingDAO();
        this.userDAO = new UserDAO();
    }

    @Override
    public List<Theater> getTheaters() throws DatabaseException {
        return theaterDAO.findAll();
    }

    @Override
    public List<Screen> getScreensForTheater(int theaterId) throws DatabaseException, NotFoundException {
        Theater theater = theaterDAO.findById(theaterId);
        if (theater == null) {
            throw new NotFoundException("Theater not found with id: " + theaterId);
        }
        return theater.getScreens();
    }

    @Override
    public List<Screen> getScreensForMovie(String movieName) throws DatabaseException {
        return screenDAO.searchScreens(movieName, "showTime", true);
    }

    @Override
    public List<String> getAvailableSeats(int screenId) throws DatabaseException, NotFoundException {
        Screen screen = screenDAO.findById(screenId);
        if (screen == null) {
            throw new NotFoundException("Screen not found with id: " + screenId);
        }
        return screen.getAvailableSeats();
    }

    @Override
    public Booking bookSeats(int userId, int screenId, List<String> seats) throws BookingException, DatabaseException, NotFoundException {
        User user = userDAO.findById(userId);
        Screen screen = screenDAO.findById(screenId);

        if (user == null) {
            throw new NotFoundException("User not found with id: " + userId);
        }
        if (screen == null) {
            throw new NotFoundException("Screen not found with id: " + screenId);
        }

        List<String> availableSeats = screen.getAvailableSeats();
        if (!availableSeats.containsAll(seats)) {
            throw new BookingException("Some selected seats are not available");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setScreen(screen);
        booking.setBookingTime(LocalDateTime.now());
        booking.setReservedSeats(seats);
        booking.setTotalPrice(calculateTotalPrice(screen, seats.size()));

        Booking createdBooking = bookingDAO.create(booking);

        // Update available seats
        availableSeats.removeAll(seats);
        screen.setAvailableSeats(availableSeats);
        screenDAO.update(screen);

        return createdBooking;
    }

    @Override
    public void cancelBooking(int bookingId) throws DatabaseException, NotFoundException, BookingException {
        Booking booking = bookingDAO.findById(bookingId);
        if (booking == null) {
            throw new NotFoundException("Booking not found with id: " + bookingId);
        }

        if (booking.getBookingTime().plusHours(1).isBefore(LocalDateTime.now())) {
            throw new BookingException("Cannot cancel booking after 1 hour of booking time");
        }

        Screen screen = booking.getScreen();
        List<String> availableSeats = new ArrayList<>(screen.getAvailableSeats());
        availableSeats.addAll(booking.getReservedSeats());
        screen.setAvailableSeats(availableSeats);

        screenDAO.update(screen);
        bookingDAO.delete(bookingId);
    }

    private double calculateTotalPrice(Screen screen, int numberOfSeats) {
        // This is a simplified pricing calculation. You might want to implement a more complex pricing strategy.
        return numberOfSeats * 10.0; // Assuming each seat costs $10
    }
}