// TheaterServiceImpl.java
package com.cinema.service.impl;

import com.cinema.dao.TheaterDAO;
import com.cinema.model.Theater;
import com.cinema.service.TheaterService;
import com.cinema.exception.*;
import java.util.List;

public class TheaterServiceImpl implements TheaterService {
    private final TheaterDAO theaterDAO;

    public TheaterServiceImpl() {
        this.theaterDAO = new TheaterDAO();
    }

    @Override
    public Theater addTheater(Theater theater) throws DatabaseException, ValidationException {
        validateTheater(theater);
        return theaterDAO.create(theater);
    }

    @Override
    public Theater getTheaterById(int id) throws DatabaseException, NotFoundException {
        return theaterDAO.findById(id);
    }

    @Override
    public List<Theater> getAllTheaters() throws DatabaseException {
        return theaterDAO.findAll();
    }

    @Override
    public void updateTheater(Theater theater) throws DatabaseException, ValidationException, NotFoundException {
        validateTheater(theater);
        theaterDAO.update(theater);
    }

    @Override
    public void deleteTheater(int id) throws DatabaseException, NotFoundException {
        theaterDAO.delete(id);
    }

    @Override
    public List<Theater> searchTheaters(String searchTerm, String searchBy, boolean ascending) throws DatabaseException {
        return theaterDAO.searchTheaters(searchTerm, searchBy, ascending);
    }

    private void validateTheater(Theater theater) throws ValidationException {
        if (theater.getName() == null || theater.getName().trim().isEmpty()) {
            throw new ValidationException("Theater name is required");
        }
        if (theater.getAddress() == null || theater.getAddress().trim().isEmpty()) {
            throw new ValidationException("Theater address is required");
        }
    }
}