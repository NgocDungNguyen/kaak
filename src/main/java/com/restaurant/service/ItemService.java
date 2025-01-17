package com.restaurant.service;

import com.restaurant.model.Item;
import java.io.IOException;
import java.util.List;

public interface ItemService {
    List<Item> getAllItems() throws IOException;
    Item getItemById(String id) throws IOException;
    void addItem(Item item) throws IOException;
    void updateItem(String id, Item item) throws IOException;
    void deleteItem(String id) throws IOException;
    List<Item> searchItems(String query, String searchBy, String sortBy, boolean ascending) throws IOException;
    List<Item> findByCategory(String category) throws IOException;
    List<Item> findByPriceRange(double minPrice, double maxPrice) throws IOException;
}