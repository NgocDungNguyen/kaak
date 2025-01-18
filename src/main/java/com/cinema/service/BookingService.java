package com.cinema.service;

import com.cinema.model.Booking;
import com.cinema.exception.*;
import java.util.List;

public interface BookingService {
    List<Booking> getAllBookings() throws DatabaseException;
    Booking getBookingById(int id) throws DatabaseException, NotFoundException;
    void addBooking(Booking booking) throws DatabaseException, ValidationException;
    void updateBooking(Booking booking) throws DatabaseException, ValidationException, NotFoundException;
    void deleteBooking(int id) throws DatabaseException, NotFoundException;
    List<Booking> searchBookings(String searchTerm, String searchBy, String sortBy, boolean ascending) throws DatabaseException;
}