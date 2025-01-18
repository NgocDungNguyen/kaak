package com.cinema.service.impl;

import com.cinema.dao.UserDAO;
import com.cinema.model.User;
import com.cinema.service.UserService;
import com.cinema.exception.*;
import java.util.List;
import java.sql.SQLIntegrityConstraintViolationException;

public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;

    public UserServiceImpl() {
        this.userDAO = new UserDAO();
    }

    @Override
    public User addUser(User user) throws DatabaseException, ValidationException, DuplicateEntityException {
        validateUser(user);
        try {
            return userDAO.create(user);
        } catch (DatabaseException e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new DuplicateEntityException("A user with this email already exists.");
            }
            throw e;
        }
    }

    @Override
    public User getUserById(int id) throws DatabaseException, NotFoundException {
        User user = userDAO.findById(id);
        if (user == null) {
            throw new NotFoundException("User not found with id: " + id);
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() throws DatabaseException {
        return userDAO.findAll();
    }

    @Override
    public void updateUser(User user) throws DatabaseException, ValidationException, NotFoundException {
        validateUser(user);
        if (userDAO.findById(user.getId()) == null) {
            throw new NotFoundException("User not found with id: " + user.getId());
        }
        userDAO.update(user);
    }

    @Override
    public void deleteUser(int id) throws DatabaseException, NotFoundException {
        if (userDAO.findById(id) == null) {
            throw new NotFoundException("User not found with id: " + id);
        }
        userDAO.delete(id);
    }

    @Override
    public List<User> searchUsers(String searchTerm, String searchBy, String sortBy, boolean ascending) throws DatabaseException {
        return userDAO.searchUsers(searchTerm, searchBy, sortBy, ascending);
    }

    private void validateUser(User user) throws ValidationException {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new ValidationException("User name is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new ValidationException("User email is required");
        }
        if (user.getPhoneNumber() == null || user.getPhoneNumber().trim().isEmpty()) {
            throw new ValidationException("User phone number is required");
        }
    }
}