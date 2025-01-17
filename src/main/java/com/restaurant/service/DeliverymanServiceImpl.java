package com.restaurant.service;

import com.restaurant.dao.DeliverymanDAO;
import com.restaurant.model.Deliveryman;
import com.restaurant.util.ValidationUtil;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DeliverymanServiceImpl implements DeliverymanService {
    private final DeliverymanDAO deliverymanDAO;

    public DeliverymanServiceImpl() {
        this.deliverymanDAO = new DeliverymanDAO();
    }

    // For testing purposes
    DeliverymanServiceImpl(DeliverymanDAO deliverymanDAO) {
        this.deliverymanDAO = deliverymanDAO;
    }

    @Override
    public List<Deliveryman> getAllDeliverymen() throws SQLException {
        System.out.println("Service: Getting all deliverymen");
        return deliverymanDAO.findAll();
    }

    @Override
    public Deliveryman getDeliverymanById(String id) throws SQLException {
        System.out.println("Service: Getting deliveryman by ID: " + id);
        return deliverymanDAO.findById(id);
    }

    @Override
    public void addDeliveryman(Deliveryman deliveryman) throws SQLException {
        System.out.println("Service: Adding deliveryman");
        validateDeliveryman(deliveryman);
        deliverymanDAO.create(deliveryman);
        System.out.println("Service: Deliveryman added successfully");
    }

    @Override
    public void updateDeliveryman(String id, Deliveryman deliveryman) throws SQLException {
        System.out.println("Service: Updating deliveryman with ID: " + id);
        validateDeliveryman(deliveryman);
        deliveryman.setId(id);
        deliverymanDAO.update(deliveryman);
        System.out.println("Service: Deliveryman updated successfully");
    }

    @Override
    public void deleteDeliveryman(String id) throws SQLException {
        System.out.println("Service: Deleting deliveryman with ID: " + id);
        deliverymanDAO.delete(id);
        System.out.println("Service: Deliveryman deleted successfully");
    }

    @Override
    public List<Deliveryman> searchDeliverymen(String query, String searchBy, String sortBy, boolean ascending) throws SQLException {
        System.out.println("Service: Searching deliverymen - Query: " + query + ", SearchBy: " + searchBy + ", SortBy: " + sortBy + ", Ascending: " + ascending);
        List<Deliveryman> deliverymen = deliverymanDAO.findAll();

        // Filter
        deliverymen = deliverymen.stream()
                .filter(d -> searchBy.equals("name") ? d.getName().toLowerCase().contains(query.toLowerCase()) :
                        d.getPhone().contains(query))
                .collect(Collectors.toList());

        // Sort
        Comparator<Deliveryman> comparator = sortBy.equals("name") ?
                Comparator.comparing(Deliveryman::getName) : Comparator.comparing(Deliveryman::getPhone);

        if (!ascending) {
            comparator = comparator.reversed();
        }

        deliverymen.sort(comparator);

        System.out.println("Service: Found " + deliverymen.size() + " deliverymen matching the criteria");
        return deliverymen;
    }

    private void validateDeliveryman(Deliveryman deliveryman) {
        if (!ValidationUtil.isValidName(deliveryman.getName())) {
            throw new IllegalArgumentException("Invalid deliveryman name");
        }
        if (!ValidationUtil.isValidPhone(deliveryman.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        if (!ValidationUtil.isValidEmail(deliveryman.getEmail())) {
            throw new IllegalArgumentException("Invalid email address");
        }
        if (!ValidationUtil.isValidVehicleType(deliveryman.getVehicleType())) {
            throw new IllegalArgumentException("Invalid vehicle type");
        }
    }
}