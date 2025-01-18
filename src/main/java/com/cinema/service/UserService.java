package com.cinema.service;

import com.cinema.model.User;
import com.cinema.exception.*;
import java.util.List;

public interface UserService {
    User addUser(User user) throws DatabaseException, ValidationException, DuplicateEntityException;
    User getUserById(int id) throws DatabaseException, NotFoundException;
    List<User> getAllUsers() throws DatabaseException;
    void updateUser(User user) throws DatabaseException, ValidationException, NotFoundException;
    void deleteUser(int id) throws DatabaseException, NotFoundException;
    List<User> searchUsers(String searchTerm, String searchBy, String sortBy, boolean ascending) throws DatabaseException;
}