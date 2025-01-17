package com.restaurant;

import com.restaurant.util.CSVHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        initializeDataDirectory();
        copyCSVFilesFromResources();
        checkFileAccess();

        Parent root = FXMLLoader.load(getClass().getResource("/view/main_view.fxml"));
        primaryStage.setTitle("Restaurant Management System");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    private void initializeDataDirectory() throws IOException {
        File dataDir = new File("data");
        if (!dataDir.exists() && !dataDir.mkdir()) {
            throw new IOException("Failed to create data directory");
        }
    }

    private void copyCSVFilesFromResources() throws IOException {
        String[] fileNames = {"customers.csv", "deliverymen.csv", "items.csv", "orders.csv", "order_items.csv"};
        for (String fileName : fileNames) {
            InputStream inputStream = getClass().getResourceAsStream("/data/" + fileName);
            if (inputStream == null) {
                System.err.println("Resource not found: /data/" + fileName);
                continue;
            }
            Path targetPath = Paths.get("data", fileName);
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copied " + fileName + " to " + targetPath.toAbsolutePath());

            // Print the contents of the copied file
            try (BufferedReader reader = new BufferedReader(new FileReader(targetPath.toFile()))) {
                String line;
                System.out.println("Contents of " + fileName + ":");
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        }
    }

    private void checkFileAccess() {
        String[] files = {"customers.csv", "deliverymen.csv", "items.csv", "orders.csv", "order_items.csv"};
        for (String file : files) {
            File f = new File("data/" + file);
            System.out.println("File: " + f.getAbsolutePath());
            System.out.println("Exists: " + f.exists());
            System.out.println("Can read: " + f.canRead());
            System.out.println("Can write: " + f.canWrite());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}