package com.restaurant.service;

import com.restaurant.dao.CustomerDAO;
import com.restaurant.model.Customer;
import com.restaurant.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerDAO customerDAO;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerService = new CustomerServiceImpl(customerDAO);
    }

    @Test
    void testGetAllCustomers() throws SQLException {
        List<Customer> expectedCustomers = Arrays.asList(TestUtils.createSampleCustomer(), TestUtils.createSampleCustomer());
        when(customerDAO.findAll()).thenReturn(expectedCustomers);

        List<Customer> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
        verify(customerDAO).findAll();
    }

    @Test
    void testGetCustomerById() throws SQLException {
        Customer expectedCustomer = TestUtils.createSampleCustomer();
        when(customerDAO.findById("CST001")).thenReturn(expectedCustomer);

        Customer result = customerService.getCustomerById("CST001");

        assertNotNull(result);
        assertEquals("CST001", result.getId());
        verify(customerDAO).findById("CST001");
    }

    @Test
    void testAddCustomer() throws SQLException {
        Customer customerToAdd = TestUtils.createSampleCustomer();

        doNothing().when(customerDAO).create(any(Customer.class));

        customerService.addCustomer(customerToAdd);

        verify(customerDAO).create(customerToAdd);
    }

    @Test
    void testUpdateCustomer() throws SQLException {
        Customer customerToUpdate = TestUtils.createSampleCustomer();
        customerToUpdate.setName("Updated Name");

        doNothing().when(customerDAO).update(any(Customer.class));

        customerService.updateCustomer("CST001", customerToUpdate);

        verify(customerDAO).update(customerToUpdate);
    }

    @Test
    void testDeleteCustomer() throws SQLException {
        customerService.deleteCustomer("CST001");

        verify(customerDAO).delete("CST001");
    }

    @Test
    void testSearchCustomers() throws SQLException {
        List<Customer> allCustomers = Arrays.asList(
                TestUtils.createSampleCustomer(),
                TestUtils.createSampleCustomer()
        );
        allCustomers.get(1).setName("Jane Doe");
        when(customerDAO.findAll()).thenReturn(allCustomers);

        List<Customer> result = customerService.searchCustomers("John", "name", "name", true);

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
    }
}