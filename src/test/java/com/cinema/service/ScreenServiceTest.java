package com.cinema.service;

import com.cinema.dao.ScreenDAO;
import com.cinema.exception.DatabaseException;
import com.cinema.exception.NotFoundException;
import com.cinema.exception.ValidationException;
import com.cinema.model.Screen;
import com.cinema.service.impl.ScreenServiceImpl;
import com.cinema.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScreenServiceTest {

    @Mock
    private ScreenDAO screenDAO;

    private ScreenService screenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        screenService = new ScreenServiceImpl();
        TestUtils.setPrivateField(screenService, "screenDAO", screenDAO);
    }

    @Test
    void testGetAllScreens() throws DatabaseException {
        List<Screen> expectedScreens = Arrays.asList(TestUtils.createTestScreen(), TestUtils.createTestScreen());
        when(screenDAO.findAll()).thenReturn(expectedScreens);

        List<Screen> actualScreens = screenService.getAllScreens();

        assertEquals(expectedScreens, actualScreens);
        verify(screenDAO).findAll();
    }

    @Test
    void testGetScreenById() throws DatabaseException, NotFoundException {
        int screenId = 1;
        Screen expectedScreen = TestUtils.createTestScreen();
        when(screenDAO.findById(screenId)).thenReturn(expectedScreen);

        Screen actualScreen = screenService.getScreenById(screenId);

        assertEquals(expectedScreen, actualScreen);
        verify(screenDAO).findById(screenId);
    }

    @Test
    void testAddScreen() throws DatabaseException, ValidationException {
        Screen screen = TestUtils.createTestScreen();

        screenService.addScreen(screen);

        verify(screenDAO).create(screen);
    }

    @Test
    void testAddScreenWithInvalidData() {
        Screen invalidScreen = new Screen();

        assertThrows(ValidationException.class, () -> screenService.addScreen(invalidScreen));
    }

    @Test
    void testUpdateScreen() throws DatabaseException, ValidationException, NotFoundException {
        Screen screen = TestUtils.createTestScreen();
        when(screenDAO.findById(screen.getId())).thenReturn(screen);

        screenService.updateScreen(screen);

        verify(screenDAO).update(screen);
    }

    @Test
    void testDeleteScreen() throws DatabaseException, NotFoundException {
        int screenId = 1;
        Screen screen = TestUtils.createTestScreen();
        when(screenDAO.findById(screenId)).thenReturn(screen);

        screenService.deleteScreen(screenId);

        verify(screenDAO).delete(screenId);
    }

    @Test
    void testSearchScreens() throws DatabaseException {
        String movieName = "Test";
        String sortBy = "showTime";
        boolean ascending = true;

        List<Screen> expectedScreens = Arrays.asList(TestUtils.createTestScreen(), TestUtils.createTestScreen());
        when(screenDAO.searchScreens(movieName, sortBy, ascending)).thenReturn(expectedScreens);

        List<Screen> actualScreens = screenService.searchScreens(movieName, sortBy, ascending);

        assertEquals(expectedScreens, actualScreens);
        verify(screenDAO).searchScreens(movieName, sortBy, ascending);
    }
}