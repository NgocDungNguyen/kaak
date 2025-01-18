package com.cinema.service;

import com.cinema.dao.UserDAO;
import com.cinema.exception.DatabaseException;
import com.cinema.exception.DuplicateEntityException;
import com.cinema.exception.NotFoundException;
import com.cinema.exception.ValidationException;
import com.cinema.model.User;
import com.cinema.service.impl.UserServiceImpl;
import com.cinema.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl();
        TestUtils.setPrivateField(userService, "userDAO", userDAO);
    }

    @Test
    void testAddUser() throws DatabaseException, ValidationException, DuplicateEntityException {
        User user = TestUtils.createTestUser();

        when(userDAO.create(user)).thenReturn(user);

        User createdUser = userService.addUser(user);

        assertEquals(user, createdUser);
        verify(userDAO).create(user);
    }

    @Test
    void testAddUserWithInvalidData() {
        User invalidUser = new User();

        assertThrows(ValidationException.class, () -> userService.addUser(invalidUser));
    }

    @Test
    void testGetUserById() throws DatabaseException, NotFoundException {
        int userId = 1;
        User expectedUser = TestUtils.createTestUser();
        when(userDAO.findById(userId)).thenReturn(expectedUser);

        User actualUser = userService.getUserById(userId);

        assertEquals(expectedUser, actualUser);
        verify(userDAO).findById(userId);
    }

    @Test
    void testGetAllUsers() throws DatabaseException {
        List<User> expectedUsers = Arrays.asList(TestUtils.createTestUser(), TestUtils.createTestUser());
        when(userDAO.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getAllUsers();

        assertEquals(expectedUsers, actualUsers);
        verify(userDAO).findAll();
    }

    @Test
    void testUpdateUser() throws DatabaseException, ValidationException, NotFoundException {
        User user = TestUtils.createTestUser();
        when(userDAO.findById(user.getId())).thenReturn(user);

        userService.updateUser(user);

        verify(userDAO).update(user);
    }

    @Test
    void testDeleteUser() throws DatabaseException, NotFoundException {
        int userId = 1;
        User user = TestUtils.createTestUser();
        when(userDAO.findById(userId)).thenReturn(user);

        userService.deleteUser(userId);

        verify(userDAO).delete(userId);
    }

    @Test
    void testSearchUsers() throws DatabaseException {
        String searchTerm = "test";
        String searchBy = "name";
        String sortBy = "email";
        boolean ascending = true;

        List<User> expectedUsers = Arrays.asList(TestUtils.createTestUser(), TestUtils.createTestUser());
        when(userDAO.searchUsers(searchTerm, searchBy, sortBy, ascending)).thenReturn(expectedUsers);

        List<User> actualUsers = userService.searchUsers(searchTerm, searchBy, sortBy, ascending);

        assertEquals(expectedUsers, actualUsers);
        verify(userDAO).searchUsers(searchTerm, searchBy, sortBy, ascending);
    }
}