package com.cinema.service;

import com.cinema.dao.BookingDAO;
import com.cinema.exception.DatabaseException;
import com.cinema.exception.NotFoundException;
import com.cinema.exception.ValidationException;
import com.cinema.model.Booking;
import com.cinema.model.Screen;
import com.cinema.model.User;
import com.cinema.service.impl.BookingServiceImpl;
import com.cinema.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private BookingDAO bookingDAO;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingService = new BookingServiceImpl();
        TestUtils.setPrivateField(bookingService, "bookingDAO", bookingDAO);
    }

    @Test
    void testGetAllBookings() throws DatabaseException {
        List<Booking> expectedBookings = Arrays.asList(TestUtils.createTestBooking(), TestUtils.createTestBooking());
        when(bookingDAO.findAll()).thenReturn(expectedBookings);

        List<Booking> actualBookings = bookingService.getAllBookings();

        assertEquals(expectedBookings, actualBookings);
        verify(bookingDAO).findAll();
    }

    @Test
    void testGetBookingById() throws DatabaseException, NotFoundException {
        int bookingId = 1;
        Booking expectedBooking = TestUtils.createTestBooking();
        when(bookingDAO.findById(bookingId)).thenReturn(expectedBooking);

        Booking actualBooking = bookingService.getBookingById(bookingId);

        assertEquals(expectedBooking, actualBooking);
        verify(bookingDAO).findById(bookingId);
    }

    @Test
    void testAddBooking() throws DatabaseException, ValidationException {
        Booking booking = TestUtils.createTestBooking();

        bookingService.addBooking(booking);

        verify(bookingDAO).create(booking);
    }

    @Test
    void testAddBookingWithInvalidData() {
        Booking invalidBooking = new Booking();

        assertThrows(ValidationException.class, () -> bookingService.addBooking(invalidBooking));
    }

    @Test
    void testUpdateBooking() throws DatabaseException, ValidationException, NotFoundException {
        Booking booking = TestUtils.createTestBooking();
        when(bookingDAO.findById(booking.getId())).thenReturn(booking);

        bookingService.updateBooking(booking);

        verify(bookingDAO).update(booking);
    }


    @Test
    void testDeleteBooking() throws DatabaseException, NotFoundException {
        int bookingId = 1;
        Booking booking = TestUtils.createTestBooking();
        when(bookingDAO.findById(bookingId)).thenReturn(booking);

        bookingService.deleteBooking(bookingId);

        verify(bookingDAO).delete(bookingId);
    }

    @Test
    void testSearchBookings() throws DatabaseException {
        String searchTerm = "test";
        String searchBy = "user";
        String sortBy = "date";
        boolean ascending = true;

        List<Booking> expectedBookings = Arrays.asList(TestUtils.createTestBooking(), TestUtils.createTestBooking());
        when(bookingDAO.searchBookings(searchTerm, sortBy, ascending)).thenReturn(expectedBookings);

        List<Booking> actualBookings = bookingService.searchBookings(searchTerm, searchBy, sortBy, ascending);

        assertEquals(expectedBookings, actualBookings);
        verify(bookingDAO).searchBookings(searchTerm, sortBy, ascending);
    }
}
