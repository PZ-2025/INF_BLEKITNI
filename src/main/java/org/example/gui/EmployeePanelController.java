/*
 * Classname: EmployeePanelController
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class EmployeePanelController {

    private final EmployeePanel employeePanel;
    private final Stage primaryStage;

    public EmployeePanelController(EmployeePanel employeePanel) {
        this.employeePanel = employeePanel;
        this.primaryStage = employeePanel.getPrimaryStage();
    }

    public void showEmployeePanel() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        // Lista zadań
        Label taskLabel = new Label("Lista zadań");
        TableView<String> taskTable = new TableView<>();
        taskTable.setMinHeight(120);
        taskTable.setPlaceholder(new Label("Brak przypisanych zadań."));

        Button updateStatusButton = new Button("Zaktualizuj status");

        // Powiadomienia
        Label notifLabel = new Label("Powiadomienia");
        ListView<String> notifications = new ListView<>();
        notifications.getItems().addAll("Nowe zadanie przydzielone", "Zmiana w harmonogramie");
        notifications.setPrefHeight(100);

        // Zgłoś problem i wyloguj
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Button reportProblemButton = new Button("Zgłoś problem");
        Button logoutButton = new Button("Wyloguj się");

        reportProblemButton.setOnAction(e -> showReportIssueWindow());
        logoutButton.setOnAction(e -> logout());

        buttonBox.getChildren().addAll(reportProblemButton, logoutButton);

        layout.getChildren().addAll(taskLabel, taskTable, updateStatusButton, notifLabel, notifications, buttonBox);
        employeePanel.setCenterPane(layout);
    }

    public void showReportIssueWindow() {
        Stage issueStage = new Stage();
        issueStage.setTitle("Panel pracownika - Zgłoszenie problemu");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label descLabel = new Label("Opis problemu");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(4);

        Label categoryLabel = new Label("Kategoria");
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Techniczny", "Zadanie", "Inny");

        Button submitButton = new Button("Wyślij");
        submitButton.setOnAction(e -> {
            String desc = descriptionArea.getText();
            String category = categoryBox.getValue();

            if (desc.isEmpty() || category == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Błąd");
                alert.setHeaderText("Uzupełnij wszystkie pola");
                alert.setContentText("Aby zgłosić problem, wpisz opis i wybierz kategorię.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Zgłoszono");
                alert.setHeaderText("Zgłoszenie zostało przyjęte");
                alert.setContentText("Dziękujemy za przesłanie zgłoszenia.");
                alert.showAndWait();
                issueStage.close();
            }
        });

        layout.getChildren().addAll(descLabel, descriptionArea, categoryLabel, categoryBox, submitButton);
        Scene scene = new Scene(layout, 350, 300);
        issueStage.setScene(scene);
        issueStage.show();
    }


    public void logout() {
        primaryStage.close();
        Stage loginStage = new Stage();
        try {
            new HelloApplication().start(loginStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
