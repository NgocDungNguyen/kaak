package com.restaurant.dao;

import com.restaurant.dao.interfaces.GenericDAO;
import com.restaurant.model.Customer;
import com.restaurant.util.CSVHandler;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerDAO implements GenericDAO<Customer, String> {
    private static final String CSV_FILE_PATH = "data/customers.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CustomerDAO() {
        try {
            String[] headers = {"id", "name", "phone", "email", "address", "joinDate", "totalOrders", "lastOrderDate"};
            CSVHandler.validateCSVStructure(CSV_FILE_PATH, headers);
        } catch (IOException e) {
            System.err.println("Error initializing CustomerDAO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Customer create(Customer customer) throws IOException {
        System.out.println("DAO: Creating customer");
        customer.setId(UUID.randomUUID().toString());
        List<Customer> customers = findAll();
        customers.add(customer);
        saveToCSV(customers);
        System.out.println("DAO: Customer created and saved");
        return customer;
    }

    @Override
    public void update(Customer customer) throws IOException {
        System.out.println("DAO: Updating customer");
        List<Customer> customers = findAll();
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId().equals(customer.getId())) {
                customers.set(i, customer);
                break;
            }
        }
        saveToCSV(customers);
        System.out.println("DAO: Customer updated and saved");
    }

    @Override
    public void delete(String id) throws IOException {
        System.out.println("DAO: Deleting customer");
        List<Customer> customers = findAll();
        customers.removeIf(customer -> customer.getId().equals(id));
        saveToCSV(customers);
        System.out.println("DAO: Customer deleted and changes saved");
    }

    @Override
    public List<Customer> findAll() throws IOException {
        System.out.println("DAO: Finding all customers from file: " + CSV_FILE_PATH);
        List<Customer> customers = new ArrayList<>();
        File file = new File(CSV_FILE_PATH);

        if (!file.exists()) {
            System.out.println("DAO: Customer file does not exist");
            return customers;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                System.out.println("DAO: Reading customer line: " + line);
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (data.length >= 8) {
                    Customer customer = new Customer();
                    customer.setId(data[0]);
                    customer.setName(data[1]);
                    customer.setPhone(data[2]);
                    customer.setEmail(data[3]);
                    customer.setAddress(data[4].replace("\"", ""));
                    customer.setJoinDate(LocalDate.parse(data[5], DATE_FORMATTER));
                    customer.setTotalOrders(Integer.parseInt(data[6]));
                    customer.setLastOrderDate(data[7].equals("null") ? null : LocalDate.parse(data[7], DATE_FORMATTER));
                    customers.add(customer);
                }
            }
        }
        System.out.println("DAO: Found " + customers.size() + " customers");
        return customers;
    }


    @Override
    public Customer findById(String id) throws IOException {
        System.out.println("DAO: Finding customer by ID: " + id);
        return findAll().stream()
                .filter(customer -> customer.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void saveToCSV(List<Customer> customers) throws IOException {
        System.out.println("DAO: Saving customers to CSV: " + CSV_FILE_PATH);
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE_PATH))) {
            writer.println("id,name,phone,email,address,joinDate,totalOrders,lastOrderDate");
            for (Customer customer : customers) {
                writer.println(String.format("%s,%s,%s,%s,%s,%s,%d,%s",
                        customer.getId(),
                        customer.getName(),
                        customer.getPhone(),
                        customer.getEmail(),
                        customer.getAddress(),
                        customer.getJoinDate().format(DATE_FORMATTER),
                        customer.getTotalOrders(),
                        customer.getLastOrderDate() != null ? customer.getLastOrderDate().format(DATE_FORMATTER) : "null"));
            }
        } catch (IOException e) {
            System.err.println("Error saving customers to CSV: " + e.getMessage());
            throw e;
        }
        System.out.println("DAO: Customers saved successfully");
    }
}