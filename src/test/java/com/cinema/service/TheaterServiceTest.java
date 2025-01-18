package com.cinema.service;

import com.cinema.dao.TheaterDAO;
import com.cinema.exception.DatabaseException;
import com.cinema.exception.NotFoundException;
import com.cinema.exception.ValidationException;
import com.cinema.model.Theater;
import com.cinema.service.impl.TheaterServiceImpl;
import com.cinema.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TheaterServiceTest {

    @Mock
    private TheaterDAO theaterDAO;

    private TheaterService theaterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        theaterService = new TheaterServiceImpl();
        TestUtils.setPrivateField(theaterService, "theaterDAO", theaterDAO);
    }

    @Test
    void testAddTheater() throws DatabaseException, ValidationException {
        Theater theater = TestUtils.createTestTheater();

        when(theaterDAO.create(theater)).thenReturn(theater);

        Theater createdTheater = theaterService.addTheater(theater);

        assertEquals(theater, createdTheater);
        verify(theaterDAO).create(theater);
    }

    @Test
    void testAddTheaterWithInvalidData() {
        Theater invalidTheater = new Theater();

        assertThrows(ValidationException.class, () -> theaterService.addTheater(invalidTheater));
    }

    @Test
    void testGetTheaterById() throws DatabaseException, NotFoundException {
        int theaterId = 1;
        Theater expectedTheater = TestUtils.createTestTheater();
        when(theaterDAO.findById(theaterId)).thenReturn(expectedTheater);

        Theater actualTheater = theaterService.getTheaterById(theaterId);

        assertEquals(expectedTheater, actualTheater);
        verify(theaterDAO).findById(theaterId);
    }

    @Test
    void testGetAllTheaters() throws DatabaseException {
        List<Theater> expectedTheaters = Arrays.asList(TestUtils.createTestTheater(), TestUtils.createTestTheater());
        when(theaterDAO.findAll()).thenReturn(expectedTheaters);

        List<Theater> actualTheaters = theaterService.getAllTheaters();

        assertEquals(expectedTheaters, actualTheaters);
        verify(theaterDAO).findAll();
    }

    @Test
    void testUpdateTheater() throws DatabaseException, ValidationException, NotFoundException {
        Theater theater = TestUtils.createTestTheater();

        theaterService.updateTheater(theater);

        verify(theaterDAO).update(theater);
    }

    @Test
    void testDeleteTheater() throws DatabaseException, NotFoundException {
        int theaterId = 1;

        theaterService.deleteTheater(theaterId);

        verify(theaterDAO).delete(theaterId);
    }

    @Test
    void testSearchTheaters() throws DatabaseException {
        String searchTerm = "test";
        String searchBy = "name";
        boolean ascending = true;

        List<Theater> expectedTheaters = Arrays.asList(TestUtils.createTestTheater(), TestUtils.createTestTheater());
        when(theaterDAO.searchTheaters(searchTerm, searchBy, ascending)).thenReturn(expectedTheaters);

        List<Theater> actualTheaters = theaterService.searchTheaters(searchTerm, searchBy, ascending);

        assertEquals(expectedTheaters, actualTheaters);
        verify(theaterDAO).searchTheaters(searchTerm, searchBy, ascending);
    }
}