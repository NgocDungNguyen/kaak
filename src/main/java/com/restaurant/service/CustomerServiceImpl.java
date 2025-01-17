package com.restaurant.service;

import com.restaurant.dao.CustomerDAO;
import com.restaurant.model.Customer;
import com.restaurant.util.ValidationUtil;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerServiceImpl implements CustomerService {
    private final CustomerDAO customerDAO;

    public CustomerServiceImpl() {
        this.customerDAO = new CustomerDAO();
    }

    @Override
    public List<Customer> getAllCustomers() throws SQLException {
        System.out.println("Service: Getting all customers");
        return customerDAO.findAll();
    }

    @Override
    public Customer getCustomerById(String id) throws SQLException {
        System.out.println("Service: Getting customer by ID: " + id);
        return customerDAO.findById(id);
    }

    @Override
    public void addCustomer(Customer customer) throws SQLException {
        System.out.println("Service: Adding customer");
        validateCustomer(customer);
        customerDAO.create(customer);
        System.out.println("Service: Customer added successfully");
    }

    @Override
    public void updateCustomer(String id, Customer customer) throws SQLException {
        System.out.println("Service: Updating customer with ID: " + id);
        validateCustomer(customer);
        customer.setId(id);
        customerDAO.update(customer);
        System.out.println("Service: Customer updated successfully");
    }

    @Override
    public void deleteCustomer(String id) throws SQLException {
        System.out.println("Service: Deleting customer with ID: " + id);
        customerDAO.delete(id);
        System.out.println("Service: Customer deleted successfully");
    }

    @Override
    public List<Customer> searchCustomers(String query, String searchBy, String sortBy, boolean ascending) throws SQLException {
        System.out.println("Service: Searching customers - Query: " + query + ", SearchBy: " + searchBy + ", SortBy: " + sortBy + ", Ascending: " + ascending);
        List<Customer> customers = customerDAO.findAll();

        // Filter
        customers = customers.stream()
                .filter(c -> searchBy.equals("name") ? c.getName().toLowerCase().contains(query.toLowerCase()) :
                        c.getPhone().contains(query))
                .collect(Collectors.toList());

        // Sort
        Comparator<Customer> comparator = sortBy.equals("name") ?
                Comparator.comparing(Customer::getName) : Comparator.comparing(Customer::getPhone);

        if (!ascending) {
            comparator = comparator.reversed();
        }

        customers.sort(comparator);

        System.out.println("Service: Found " + customers.size() + " customers matching the criteria");
        return customers;
    }

    private void validateCustomer(Customer customer) {
        if (!ValidationUtil.isValidName(customer.getName())) {
            throw new IllegalArgumentException("Invalid customer name");
        }
        if (!ValidationUtil.isValidPhone(customer.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        if (!ValidationUtil.isValidEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Invalid email address");
        }
        if (!ValidationUtil.isValidAddress(customer.getAddress())) {
            throw new IllegalArgumentException("Invalid address");
        }
    }
}