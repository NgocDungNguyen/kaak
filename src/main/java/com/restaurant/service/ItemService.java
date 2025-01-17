package com.restaurant.service;

import com.restaurant.model.Item;
import java.sql.SQLException;
import java.util.List;

public interface ItemService {
    List<Item> getAllItems() throws SQLException;
    Item getItemById(String id) throws SQLException;
    void addItem(Item item) throws SQLException;
    void updateItem(String id, Item item) throws SQLException;
    void deleteItem(String id) throws SQLException;
    List<Item> searchItems(String query, String searchBy, String sortBy, boolean ascending) throws SQLException;
    List<Item> findByCategory(String category) throws SQLException;
    List<Item> findByPriceRange(double minPrice, double maxPrice) throws SQLException;
}