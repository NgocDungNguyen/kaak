package com.restaurant.service;

import com.restaurant.model.Deliveryman;
import java.io.IOException;
import java.util.List;

public interface DeliverymanService {
    List<Deliveryman> getAllDeliverymen() throws IOException;
    Deliveryman getDeliverymanById(String id) throws IOException;
    void addDeliveryman(Deliveryman deliveryman) throws IOException;
    void updateDeliveryman(String id, Deliveryman deliveryman) throws IOException;
    void deleteDeliveryman(String id) throws IOException;
    List<Deliveryman> searchDeliverymen(String query, String searchBy, String sortBy, boolean ascending) throws IOException;
}