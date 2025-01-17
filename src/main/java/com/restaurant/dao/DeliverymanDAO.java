package com.restaurant.dao;

import com.restaurant.dao.interfaces.GenericDAO;
import com.restaurant.model.Deliveryman;
import com.restaurant.util.CSVHandler;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeliverymanDAO implements GenericDAO<Deliveryman, String> {
    private static final String CSV_FILE_PATH = "data/deliverymen.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DeliverymanDAO() {
        try {
            String[] headers = {"id", "name", "phone", "email", "status", "vehicleType", "licenseNumber", "joinDate", "totalDeliveries", "rating", "available"};
            CSVHandler.validateCSVStructure(CSV_FILE_PATH, headers);
        } catch (IOException e) {
            System.err.println("Error initializing DeliverymanDAO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Deliveryman create(Deliveryman deliveryman) throws IOException {
        System.out.println("DAO: Creating deliveryman");
        deliveryman.setId(UUID.randomUUID().toString());
        List<Deliveryman> deliverymen = findAll();
        deliverymen.add(deliveryman);
        saveToCSV(deliverymen);
        System.out.println("DAO: Deliveryman created and saved");
        return deliveryman;
    }

    @Override
    public void update(Deliveryman deliveryman) throws IOException {
        System.out.println("DAO: Updating deliveryman");
        List<Deliveryman> deliverymen = findAll();
        for (int i = 0; i < deliverymen.size(); i++) {
            if (deliverymen.get(i).getId().equals(deliveryman.getId())) {
                deliverymen.set(i, deliveryman);
                break;
            }
        }
        saveToCSV(deliverymen);
        System.out.println("DAO: Deliveryman updated and saved");
    }

    @Override
    public void delete(String id) throws IOException {
        System.out.println("DAO: Deleting deliveryman");
        List<Deliveryman> deliverymen = findAll();
        deliverymen.removeIf(deliveryman -> deliveryman.getId().equals(id));
        saveToCSV(deliverymen);
        System.out.println("DAO: Deliveryman deleted and changes saved");
    }

    @Override
    public List<Deliveryman> findAll() throws IOException {
        System.out.println("DAO: Finding all deliverymen from file: " + CSV_FILE_PATH);
        List<Deliveryman> deliverymen = new ArrayList<>();
        File file = new File(CSV_FILE_PATH);

        if (!file.exists()) {
            System.out.println("DAO: Deliverymen file does not exist");
            return deliverymen;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                System.out.println("DAO: Reading deliveryman line: " + line);
                String[] data = line.split(",");
                if (data.length >= 11) {
                    Deliveryman deliveryman = new Deliveryman();
                    deliveryman.setId(data[0]);
                    deliveryman.setName(data[1]);
                    deliveryman.setPhone(data[2]);
                    deliveryman.setEmail(data[3]);
                    deliveryman.setStatus(data[4]);
                    deliveryman.setVehicleType(data[5]);
                    deliveryman.setLicenseNumber(data[6]);
                    deliveryman.setJoinDate(LocalDate.parse(data[7], DATE_FORMATTER));
                    deliveryman.setTotalDeliveries(Integer.parseInt(data[8]));
                    deliveryman.setRating(Double.parseDouble(data[9]));
                    deliveryman.setAvailable(Boolean.parseBoolean(data[10]));
                    deliverymen.add(deliveryman);
                }
            }
        }
        System.out.println("DAO: Found " + deliverymen.size() + " deliverymen");
        return deliverymen;
    }


    @Override
    public Deliveryman findById(String id) throws IOException {
        System.out.println("DAO: Finding deliveryman by ID: " + id);
        return findAll().stream()
                .filter(deliveryman -> deliveryman.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void saveToCSV(List<Deliveryman> deliverymen) throws IOException {
        System.out.println("DAO: Saving deliverymen to CSV: " + CSV_FILE_PATH);
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE_PATH))) {
            writer.println("id,name,phone,email,status,vehicleType,licenseNumber,joinDate,totalDeliveries,rating,available");
            for (Deliveryman deliveryman : deliverymen) {
                writer.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%d,%.1f,%b",
                        deliveryman.getId(),
                        deliveryman.getName(),
                        deliveryman.getPhone(),
                        deliveryman.getEmail(),
                        deliveryman.getStatus(),
                        deliveryman.getVehicleType(),
                        deliveryman.getLicenseNumber(),
                        deliveryman.getJoinDate().format(DATE_FORMATTER),
                        deliveryman.getTotalDeliveries(),
                        deliveryman.getRating(),
                        deliveryman.isAvailable()));
            }
        } catch (IOException e) {
            System.err.println("Error saving deliverymen to CSV: " + e.getMessage());
            throw e;
        }
        System.out.println("DAO: Deliverymen saved successfully");
    }
}