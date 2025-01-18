package com.cinema.service.impl;

import com.cinema.dao.ScreenDAO;
import com.cinema.model.Screen;
import com.cinema.service.ScreenService;
import com.cinema.exception.*;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ScreenServiceImpl implements ScreenService {
    private static final Logger LOGGER = Logger.getLogger(ScreenServiceImpl.class.getName());
    private final ScreenDAO screenDAO;

    public ScreenServiceImpl() {
        this.screenDAO = new ScreenDAO();
    }

    @Override
    public List<Screen> getAllScreens() throws DatabaseException {
        try {
            return screenDAO.findAll();
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Error getting all screens", e);
            throw new DatabaseException("Failed to retrieve screens: " + e.getMessage(), e);
        }
    }

    @Override
    public Screen getScreenById(int id) throws DatabaseException, NotFoundException {
        try {
            Screen screen = screenDAO.findById(id);
            if (screen == null) {
                throw new NotFoundException("Screen with id " + id + " not found");
            }
            return screen;
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Error getting screen by ID: " + id, e);
            throw e;
        }
    }

    @Override
    public void addScreen(Screen screen) throws DatabaseException, ValidationException {
        validateScreen(screen);
        try {
            screenDAO.create(screen);
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Error adding screen", e);
            throw new DatabaseException("Failed to add screen: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateScreen(Screen screen) throws DatabaseException, ValidationException, NotFoundException {
        validateScreen(screen);
        try {
            Screen existingScreen = screenDAO.findById(screen.getId());
            if (existingScreen == null) {
                throw new NotFoundException("Screen with id " + screen.getId() + " not found");
            }
            screenDAO.update(screen);
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Error updating screen", e);
            throw e;
        }
    }

    @Override
    public void deleteScreen(int id) throws DatabaseException, NotFoundException {
        try {
            Screen existingScreen = screenDAO.findById(id);
            if (existingScreen == null) {
                throw new NotFoundException("Screen with id " + id + " not found");
            }
            screenDAO.delete(id);
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Error deleting screen", e);
            throw e;
        }
    }

    @Override
    public List<Screen> searchScreens(String movieName, String sortBy, boolean ascending) throws DatabaseException {
        try {
            return screenDAO.searchScreens(movieName, sortBy, ascending);
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Error searching screens", e);
            throw new DatabaseException("Failed to search screens: " + e.getMessage(), e);
        }
    }

    private void validateScreen(Screen screen) throws ValidationException {
        if (screen.getMovieName() == null || screen.getMovieName().trim().isEmpty()) {
            throw new ValidationException("Movie name is required");
        }
        if (screen.getShowTime() == null) {
            throw new ValidationException("Show time is required");
        }
        if (screen.getTheater() == null) {
            throw new ValidationException("Theater is required");
        }
    }
}