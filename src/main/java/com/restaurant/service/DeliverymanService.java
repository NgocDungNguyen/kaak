package com.restaurant.service;

import com.restaurant.model.Deliveryman;
import java.sql.SQLException;
import java.util.List;

public interface DeliverymanService {
    List<Deliveryman> getAllDeliverymen() throws SQLException;
    Deliveryman getDeliverymanById(String id) throws SQLException;
    void addDeliveryman(Deliveryman deliveryman) throws SQLException;
    void updateDeliveryman(String id, Deliveryman deliveryman) throws SQLException;
    void deleteDeliveryman(String id) throws SQLException;
    List<Deliveryman> searchDeliverymen(String query, String searchBy, String sortBy, boolean ascending) throws SQLException;
}