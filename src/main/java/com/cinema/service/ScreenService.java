package com.cinema.service;

import com.cinema.model.Screen;
import com.cinema.exception.*;
import java.util.List;

public interface ScreenService {
    List<Screen> getAllScreens() throws DatabaseException;
    Screen getScreenById(int id) throws DatabaseException, NotFoundException;
    void addScreen(Screen screen) throws DatabaseException, ValidationException;
    void updateScreen(Screen screen) throws DatabaseException, ValidationException, NotFoundException;
    void deleteScreen(int id) throws DatabaseException, NotFoundException;
    List<Screen> searchScreens(String movieName, String sortBy, boolean ascending) throws DatabaseException;
}