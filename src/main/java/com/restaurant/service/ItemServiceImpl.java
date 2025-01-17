package com.restaurant.service;

import com.restaurant.dao.ItemDAO;
import com.restaurant.model.Item;
import com.restaurant.util.ValidationUtil;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ItemServiceImpl implements ItemService {
    private final ItemDAO itemDAO;

    public ItemServiceImpl() {
        this.itemDAO = new ItemDAO();
    }

    // For testing purposes
    ItemServiceImpl(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    @Override
    public List<Item> getAllItems() throws SQLException {
        System.out.println("Service: Getting all items");
        return itemDAO.findAll();
    }

    @Override
    public Item getItemById(String id) throws SQLException {
        System.out.println("Service: Getting item by ID: " + id);
        return itemDAO.findById(id);
    }

    @Override
    public void addItem(Item item) throws SQLException {
        System.out.println("Service: Adding item");
        validateItem(item);
        itemDAO.create(item);
        System.out.println("Service: Item added successfully");
    }

    @Override
    public void updateItem(String id, Item item) throws SQLException {
        System.out.println("Service: Updating item with ID: " + id);
        validateItem(item);
        item.setId(id);
        itemDAO.update(item);
        System.out.println("Service: Item updated successfully");
    }

    @Override
    public void deleteItem(String id) throws SQLException {
        System.out.println("Service: Deleting item with ID: " + id);
        itemDAO.delete(id);
        System.out.println("Service: Item deleted successfully");
    }

    @Override
    public List<Item> searchItems(String query, String searchBy, String sortBy, boolean ascending) throws SQLException {
        System.out.println("Service: Searching items - Query: " + query + ", SearchBy: " + searchBy + ", SortBy: " + sortBy + ", Ascending: " + ascending);
        List<Item> items = itemDAO.findAll();

        // Filter
        items = items.stream()
                .filter(i -> searchBy.equals("name") ? i.getName().toLowerCase().contains(query.toLowerCase()) :
                        String.valueOf(i.getPrice()).contains(query))
                .collect(Collectors.toList());

        // Sort
        Comparator<Item> comparator = sortBy.equals("name") ?
                Comparator.comparing(Item::getName) : Comparator.comparing(Item::getPrice);

        if (!ascending) {
            comparator = comparator.reversed();
        }

        items.sort(comparator);

        System.out.println("Service: Found " + items.size() + " items matching the criteria");
        return items;
    }

    @Override
    public List<Item> findByCategory(String category) throws SQLException {
        System.out.println("Service: Finding items by category: " + category);
        return itemDAO.findAll().stream()
                .filter(item -> item.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findByPriceRange(double minPrice, double maxPrice) throws SQLException {
        System.out.println("Service: Finding items by price range: " + minPrice + " - " + maxPrice);
        return itemDAO.findAll().stream()
                .filter(item -> item.getPrice() >= minPrice && item.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    private void validateItem(Item item) {
        if (!ValidationUtil.isValidName(item.getName())) {
            throw new IllegalArgumentException("Invalid item name");
        }
        if (!ValidationUtil.isValidPrice(item.getPrice())) {
            throw new IllegalArgumentException("Invalid price");
        }
        if (!ValidationUtil.isValidCategory(item.getCategory())) {
            throw new IllegalArgumentException("Invalid category");
        }
    }
}