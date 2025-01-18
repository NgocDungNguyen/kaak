package com.cinema.service.impl;

import com.cinema.dao.BookingDAO;
import com.cinema.model.Booking;
import com.cinema.service.BookingService;
import com.cinema.exception.*;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class BookingServiceImpl implements BookingService {
    private static final Logger LOGGER = Logger.getLogger(BookingServiceImpl.class.getName());
    private final BookingDAO bookingDAO;

    public BookingServiceImpl() {
        this.bookingDAO = new BookingDAO();
    }

    @Override
    public List<Booking> getAllBookings() throws DatabaseException {
        try {
            return bookingDAO.findAll();
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Error getting all bookings", e);
            throw new DatabaseException("Failed to retrieve bookings: " + e.getMessage(), e);
        }
    }

    @Override
    public Booking getBookingById(int id) throws DatabaseException, NotFoundException {
        try {
            Booking booking = bookingDAO.findById(id);
            if (booking == null) {
                throw new NotFoundException("Booking with id " + id + " not found");
            }
            return booking;
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Error getting booking by ID: " + id, e);
            throw e;
        }
    }

    @Override
    public void addBooking(Booking booking) throws DatabaseException, ValidationException {
        validateBooking(booking);
        try {
            bookingDAO.create(booking);
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Error adding booking", e);
            throw new DatabaseException("Failed to add booking: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateBooking(Booking booking) throws DatabaseException, ValidationException, NotFoundException {
        validateBooking(booking);
        try {
            Booking existingBooking = bookingDAO.findById(booking.getId());
            if (existingBooking == null) {
                throw new NotFoundException("Booking with id " + booking.getId() + " not found");
            }
            bookingDAO.update(booking);
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Error updating booking", e);
            throw e;
        }
    }

    @Override
    public void deleteBooking(int id) throws DatabaseException, NotFoundException {
        try {
            Booking existingBooking = bookingDAO.findById(id);
            if (existingBooking == null) {
                throw new NotFoundException("Booking with id " + id + " not found");
            }
            bookingDAO.delete(id);
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Error deleting booking", e);
            throw e;
        }
    }

    @Override
    public List<Booking> searchBookings(String searchTerm, String searchBy, String sortBy, boolean ascending) throws DatabaseException {
        try {
            return bookingDAO.searchBookings(searchTerm, sortBy, ascending);
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Error searching bookings", e);
            throw new DatabaseException("Failed to search bookings: " + e.getMessage(), e);
        }
    }

    private void validateBooking(Booking booking) throws ValidationException {
        if (booking.getUser() == null) {
            throw new ValidationException("User is required");
        }
        if (booking.getScreen() == null) {
            throw new ValidationException("Screen is required");
        }
        if (booking.getReservedSeats() == null || booking.getReservedSeats().isEmpty()) {
            throw new ValidationException("At least one seat must be reserved");
        }
    }
}