package com.restaurant.dao;

import com.restaurant.dao.interfaces.GenericDAO;
import com.restaurant.model.Item;
import com.restaurant.util.CSVHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemDAO implements GenericDAO<Item, String> {
    private static final String CSV_FILE_PATH = "data/items.csv";

    public ItemDAO() {
        try {
            String[] headers = {"id", "name", "category", "price", "description", "preparationTime", "isAvailable", "imageUrl", "calories", "spicyLevel"};
            CSVHandler.validateCSVStructure(CSV_FILE_PATH, headers);
        } catch (IOException e) {
            System.err.println("Error initializing ItemDAO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Item create(Item item) throws IOException {
        System.out.println("DAO: Creating item");
        item.setId(UUID.randomUUID().toString());
        List<Item> items = findAll();
        items.add(item);
        saveToCSV(items);
        System.out.println("DAO: Item created and saved");
        return item;
    }

    @Override
    public void update(Item item) throws IOException {
        System.out.println("DAO: Updating item");
        List<Item> items = findAll();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(item.getId())) {
                items.set(i, item);
                break;
            }
        }
        saveToCSV(items);
        System.out.println("DAO: Item updated and saved");
    }

    @Override
    public void delete(String id) throws IOException {
        System.out.println("DAO: Deleting item");
        List<Item> items = findAll();
        items.removeIf(item -> item.getId().equals(id));
        saveToCSV(items);
        System.out.println("DAO: Item deleted and changes saved");
    }

    @Override
    public List<Item> findAll() throws IOException {
        System.out.println("DAO: Finding all items from file: " + CSV_FILE_PATH);
        List<Item> items = new ArrayList<>();
        File file = new File(CSV_FILE_PATH);

        if (!file.exists()) {
            System.out.println("DAO: Items file does not exist");
            return items;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                System.out.println("DAO: Reading item line: " + line);
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (data.length >= 10) {
                    Item item = new Item();
                    item.setId(data[0]);
                    item.setName(data[1]);
                    item.setCategory(data[2]);
                    item.setPrice(Double.parseDouble(data[3]));
                    item.setDescription(data[4].replace("\"", ""));
                    item.setPreparationTime(Integer.parseInt(data[5]));
                    item.setAvailable(Boolean.parseBoolean(data[6]));
                    item.setImageUrl(data[7]);
                    item.setCalories(Integer.parseInt(data[8]));
                    item.setSpicyLevel(Integer.parseInt(data[9]));
                    items.add(item);
                }
            }
        }
        System.out.println("DAO: Found " + items.size() + " items");
        return items;
    }


    @Override
    public Item findById(String id) throws IOException {
        System.out.println("DAO: Finding item by ID: " + id);
        return findAll().stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void saveToCSV(List<Item> items) throws IOException {
        System.out.println("DAO: Saving items to CSV: " + CSV_FILE_PATH);
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE_PATH))) {
            writer.println("id,name,category,price,description,preparationTime,isAvailable,imageUrl,calories,spicyLevel");
            for (Item item : items) {
                writer.println(String.format("%s,%s,%s,%.2f,%s,%d,%b,%s,%d,%d",
                        item.getId(),
                        escapeCsvField(item.getName()),
                        escapeCsvField(item.getCategory()),
                        item.getPrice(),
                        escapeCsvField(item.getDescription()),
                        item.getPreparationTime(),
                        item.isAvailable(),
                        escapeCsvField(item.getImageUrl()),
                        item.getCalories(),
                        item.getSpicyLevel()));
            }
        } catch (IOException e) {
            System.err.println("Error saving items to CSV: " + e.getMessage());
            throw e;
        }
        System.out.println("DAO: Items saved successfully");
    }

    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        field = field.replace("\"", "\"\"");
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            field = "\"" + field + "\"";
        }
        return field;
    }
}