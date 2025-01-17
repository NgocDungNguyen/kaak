package com.restaurant.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CSVHandler {
    private static final String DATA_DIR = "data";

    public static void validateCSVStructure(String fileName, String[] expectedHeaders) throws IOException {
        Path filePath = getFilePath(fileName);
        if (!Files.exists(filePath) || Files.size(filePath) == 0) {
            List<String> headers = Arrays.asList(expectedHeaders);
            Files.write(filePath, headers);
        } else {
            List<String> existingHeaders = Files.readAllLines(filePath).stream().findFirst().map(line -> Arrays.asList(line.split(","))).orElse(null);
            if (!Arrays.asList(expectedHeaders).equals(existingHeaders)) {
                throw new IOException("Invalid CSV structure in " + fileName);
            }
        }
    }

    public static List<String[]> readCSV(String fileName) throws IOException {
        Path filePath = getFilePath(fileName);
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try (CSVReader reader = new CSVReader(Files.newBufferedReader(filePath))) {
            return reader.readAll();
        } catch (CsvException e) {
            throw new IOException("Error reading CSV file: " + fileName, e);
        }
    }

    public static void writeCSV(String fileName, List<String[]> data) throws IOException {
        Path filePath = getFilePath(fileName);
        try (CSVWriter writer = new CSVWriter(Files.newBufferedWriter(filePath))) {
            writer.writeAll(data);
        }
    }

    private static Path getFilePath(String fileName) throws IOException {
        Path dirPath = Paths.get(DATA_DIR);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        return dirPath.resolve(fileName);
    }
}