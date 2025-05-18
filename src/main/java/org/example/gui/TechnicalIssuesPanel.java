/*
 * Classname: TechnicalIssuesPanel
 * Version information: 1.1
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.gui;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.Objects;

public class TechnicalIssuesPanel extends VBox {

    private TextField issueTitleField;
    private ComboBox<String> issueTypeCombo;
    private ComboBox<String> priorityCombo;
    private TextArea issueDescriptionArea;
    private ListView<String> issuesList;
    private ObservableList<String> submittedIssues;


    public TechnicalIssuesPanel() {
        setSpacing(10);
        setPadding(new Insets(10));
        initializeUI();
    }

    private void initializeUI() {
        Label titleLabel = new Label("Technical Issues Reporting");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        issueTitleField = new TextField();
        issueTitleField.setPromptText("Issue title");

        issueTypeCombo = new ComboBox<>();
        issueTypeCombo.getItems().addAll(
                "Computer Hardware",
                "Software",
                "Cash Register",
                "Network/WiFi",
                "Other"
        );
        issueTypeCombo.setPromptText("Issue category");

        priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll(
                "Low",
                "Medium",
                "High",
                "Critical"
        );
        priorityCombo.setPromptText("Priority");

        issueDescriptionArea = new TextArea();
        issueDescriptionArea.setPromptText("Describe the issue in detail...");
        issueDescriptionArea.setPrefHeight(200);

        Button submitButton = new Button("Submit Issue");
        submitButton.setOnAction(e -> submitTechnicalIssue());

        submittedIssues = FXCollections.observableArrayList();
        issuesList = new ListView<>(submittedIssues);
        issuesList.setPrefHeight(200);

        Button refreshButton = new Button("Refresh List");
        refreshButton.setOnAction(e -> refreshIssuesList());

        HBox buttonsBox = new HBox(10, submitButton, refreshButton);

        getChildren().addAll(
                titleLabel,
                new Label("Title:"),
                issueTitleField,
                new Label("Category:"),
                issueTypeCombo,
                new Label("Priority:"),
                priorityCombo,
                new Label("Description:"),
                issueDescriptionArea,
                buttonsBox,
                new Separator(),
                new Label("Your submissions:"),
                issuesList
        );
    }

    private void submitTechnicalIssue() {
        String title = issueTitleField.getText();
        String type = issueTypeCombo.getValue();
        String priority = priorityCombo.getValue();
        String description = issueDescriptionArea.getText();

        if (isEmpty(title) || isEmpty(type) || isEmpty(priority) || isEmpty(description)) {
            showWarning("Missing Data", "Please fill in all required fields.");
            return;
        }

        String issueEntry = String.format("[%s] %s - %s (%s)",
                LocalDate.now(), title, type, priority);

        submittedIssues.add(issueEntry);
        clearForm();

        showInfo("Issue Submitted", "Your issue has been reported to the IT department.");
    }

    private void refreshIssuesList() {
        System.out.println("Refreshing submitted issues list...");
        // Here you can load issues from DB/server if needed
    }

    private void clearForm() {
        issueTitleField.clear();
        issueTypeCombo.getSelectionModel().clearSelection();
        priorityCombo.getSelectionModel().clearSelection();
        issueDescriptionArea.clear();
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message);
    }

    private void showInfo(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
