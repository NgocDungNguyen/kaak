package com.restaurant.service;

import com.restaurant.model.Customer;
import java.io.IOException;
import java.util.List;

public interface CustomerService {
    List<Customer> getAllCustomers() throws IOException;
    Customer getCustomerById(String id) throws IOException;
    void addCustomer(Customer customer) throws IOException;
    void updateCustomer(String id, Customer customer) throws IOException;
    void deleteCustomer(String id) throws IOException;
    List<Customer> searchCustomers(String query, String searchBy, String sortBy, boolean ascending) throws IOException;
}