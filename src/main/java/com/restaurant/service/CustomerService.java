package com.restaurant.service;

import com.restaurant.model.Customer;
import java.sql.SQLException;
import java.util.List;

public interface CustomerService {
    List<Customer> getAllCustomers() throws SQLException;
    Customer getCustomerById(String id) throws SQLException;
    void addCustomer(Customer customer) throws SQLException;
    void updateCustomer(String id, Customer customer) throws SQLException;
    void deleteCustomer(String id) throws SQLException;
    List<Customer> searchCustomers(String query, String searchBy, String sortBy, boolean ascending) throws SQLException;
}