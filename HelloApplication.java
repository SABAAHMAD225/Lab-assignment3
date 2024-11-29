package com.example.demo1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HelloApplication extends Application {

    private final Map<String, String> dataMap = new HashMap<>();
    private static final String FILE_NAME = "data.txt";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Data Storage App");

        // UI Elements
        TextField nameField = new TextField();
        TextField idField = new TextField();
        DatePicker dobPicker = new DatePicker();

        ComboBox<String> provinceComboBox = new ComboBox<>();
        provinceComboBox.getItems().addAll("Punjab", "Sindh", "Khyber Pakhtunkhwa", "Balochistan", "Islamabad", "Azad Kashmir", "Gilgit-Baltistan");

        ToggleGroup genderGroup = new ToggleGroup();
        RadioButton maleButton = new RadioButton("Male");
        RadioButton femaleButton = new RadioButton("Female");
        maleButton.setToggleGroup(genderGroup);
        femaleButton.setToggleGroup(genderGroup);

        Button saveButton = new Button("Save");
        Button findButton = new Button("Find");
        Button closeButton = new Button("Close");

        // Layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.addRow(0, new Label("Full Name:"), nameField);
        grid.addRow(1, new Label("ID:"), idField);
        grid.addRow(2, new Label("Gender:"), new HBox(10, maleButton, femaleButton));
        grid.addRow(3, new Label("Province:"), provinceComboBox);
        grid.addRow(4, new Label("Date of Birth:"), dobPicker);
        grid.addRow(5, saveButton, findButton);
        grid.add(closeButton, 1, 6);

        loadDataFromFile();

        // Button actions
        saveButton.setOnAction(e -> saveData(nameField, idField, genderGroup, provinceComboBox, dobPicker));
        findButton.setOnAction(e -> findData());
        closeButton.setOnAction(e -> primaryStage.close());

        // Scene
        primaryStage.setScene(new Scene(grid, 400, 300));
        primaryStage.show();
    }

    private void saveData(TextField nameField, TextField idField, ToggleGroup genderGroup, ComboBox<String> provinceComboBox, DatePicker dobPicker) {
        String id = idField.getText();
        if (id.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "ID is required to save data.");
            return;
        }

        RadioButton selectedGender = (RadioButton) genderGroup.getSelectedToggle();
        String gender = selectedGender != null ? selectedGender.getText() : "";
        String province = provinceComboBox.getValue() != null ? provinceComboBox.getValue() : "";
        String data = String.join(";", nameField.getText(), id, gender, province,
                dobPicker.getValue() != null ? dobPicker.getValue().toString() : "");

        dataMap.put(id, data);
        saveDataToFile();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Data saved successfully.");
        nameField.clear();
        idField.clear();
        genderGroup.selectToggle(null);
        provinceComboBox.setValue(null);
        dobPicker.setValue(null);
    }

    private void findData() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Find Data");
        dialog.setHeaderText("Find Data by ID");
        dialog.setContentText("Enter ID:");

        dialog.showAndWait().ifPresent(id -> {
            String data = dataMap.get(id);
            if (data != null) {
                String[] parts = data.split(";");
                showAlert(Alert.AlertType.INFORMATION, "Data Found", String.format(
                        "Full Name: %s\nID: %s\nGender: %s\nProvince: %s\nDate of Birth: %s",
                        parts[0], parts[1], parts[2], parts[3], parts[4]
                ));
            } else {
                showAlert(Alert.AlertType.ERROR, "Not Found", "No data found for the given ID.");
            }
        });
    }

    private void saveDataToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String data : dataMap.values()) {
                writer.write(data);
                writer.newLine();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save data to file.");
        }
    }

    private void loadDataFromFile() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length == 5) {
                        dataMap.put(parts[1], line);
                    }
                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load data from file.");
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}