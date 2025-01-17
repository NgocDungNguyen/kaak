package com.restaurant.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class CSVHandler {

    public static void checkFileAccess(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("File created: " + filePath);
            } catch (IOException e) {
                System.out.println("Failed to create file: " + filePath);
                e.printStackTrace();
                return;
            }
        }

        System.out.println("File exists: " + filePath);
        System.out.println("Can read: " + file.canRead());
        System.out.println("Can write: " + file.canWrite());
        System.out.println("Is directory: " + file.isDirectory());
        System.out.println("Absolute path: " + file.getAbsolutePath());
    }

    public static void writeToCSV(String filePath, List<String[]> data) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeAll(data);
        }
    }

    public static List<String[]> readFromCSV(String filePath) throws IOException, CsvValidationException {
        List<String[]> records = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                records.add(line);
            }
        }
        return records;
    }

    public static void exportToCSV(String filePath, String[] headers, List<String[]> data) throws IOException {
        List<String[]> allData = new ArrayList<>();
        allData.add(headers);
        allData.addAll(data);
        writeToCSV(filePath, allData);
    }

    public static List<String[]> importFromCSV(String filePath, boolean skipHeader) throws IOException, CsvValidationException {
        List<String[]> records = readFromCSV(filePath);
        if (skipHeader && !records.isEmpty()) {
            records.remove(0);
        }
        return records;
    }

    public static void validateCSVStructure(String filePath, String[] expectedHeaders) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            // Create new file with headers
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(String.join(",", expectedHeaders));
            }
            return;
        }

        // Validate existing file headers
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("CSV file is empty: " + filePath);
            }

            String[] actualHeaders = headerLine.split(",");
            if (!Arrays.equals(actualHeaders, expectedHeaders)) {
                throw new IOException("CSV file has invalid structure: " + filePath);
            }
        }
    }
}