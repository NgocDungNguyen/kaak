package com.cinema.service;

import com.cinema.model.*;
import com.cinema.exception.*;
import java.util.List;

public interface BookingSystemService {
    List<Theater> getTheaters() throws DatabaseException;
    List<Screen> getScreensForTheater(int theaterId) throws DatabaseException, NotFoundException;
    List<Screen> getScreensForMovie(String movieName) throws DatabaseException;
    List<String> getAvailableSeats(int screenId) throws DatabaseException, NotFoundException;
    Booking bookSeats(int userId, int screenId, List<String> seats) throws BookingException, DatabaseException, NotFoundException;
    void cancelBooking(int bookingId) throws DatabaseException, NotFoundException, BookingException;
}