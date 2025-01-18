// TheaterService.java
package com.cinema.service;

import com.cinema.model.Theater;
import com.cinema.exception.*;
import java.util.List;

public interface TheaterService {
    Theater addTheater(Theater theater) throws DatabaseException, ValidationException;
    Theater getTheaterById(int id) throws DatabaseException, NotFoundException;
    List<Theater> getAllTheaters() throws DatabaseException;
    void updateTheater(Theater theater) throws DatabaseException, ValidationException, NotFoundException;
    void deleteTheater(int id) throws DatabaseException, NotFoundException;
    List<Theater> searchTheaters(String searchTerm, String searchBy, boolean ascending) throws DatabaseException;
}