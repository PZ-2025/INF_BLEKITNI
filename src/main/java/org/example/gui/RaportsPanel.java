package org.example.gui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;

import java.time.LocalDate;

public class RaportsPanel extends VBox {

    private ComboBox<ReportType> reportTypeCombo;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private TextArea reportOutput;

    public RaportsPanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        initializeUI();
    }

    private void initializeUI() {
        Label titleLabel = new Label("Reports Panel");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        reportTypeCombo = new ComboBox<>();
        reportTypeCombo.getItems().addAll(ReportType.values());
        reportTypeCombo.setPromptText("Select report type");

        startDatePicker = new DatePicker();
        endDatePicker = new DatePicker();

        HBox dateRangeBox = new HBox(10,
                new Label("From:"), startDatePicker,
                new Label("To:"), endDatePicker);

        Button generateButton = new Button("Generate Report");
        generateButton.setOnAction(e -> generateReport());

        Button exportButton = new Button("Export to PDF");
        exportButton.setOnAction(e -> exportToPDF());

        HBox buttonsBox = new HBox(10, generateButton, exportButton);

        reportOutput = new TextArea();
        reportOutput.setEditable(false);
        reportOutput.setPrefHeight(500);

        getChildren().addAll(
                titleLabel,
                new Label("Report Type:"),
                reportTypeCombo,
                dateRangeBox,
                buttonsBox,
                new Separator(),
                reportOutput
        );
    }

    private void generateReport() {
        ReportType reportType = reportTypeCombo.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (reportType == null || startDate == null || endDate == null) {
            showAlert("Missing data", "Please select a report type and date range.");
            return;
        }

        String reportContent = createReportContent(reportType, startDate, endDate);
        reportOutput.setText(reportContent);
    }

    private String createReportContent(ReportType reportType, LocalDate startDate, LocalDate endDate) {
        return String.format(
                "Report generated: %s%nDate range: %s to %s%n%nSample report data:%n" +
                        "--------------------------------%n" +
                        "Product 1: 120 pcs%n" +
                        "Product 2: 85 pcs%n" +
                        "Product 3: 42 pcs%n",
                reportType.getDisplayName(), startDate, endDate
        );
    }

    private void exportToPDF() {
        System.out.println("Exporting report to PDF...");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public enum ReportType {
        DAILY_SALES("Daily Sales"),
        MONTHLY_SALES("Monthly Sales"),
        MOST_POPULAR_PRODUCTS("Most Popular Products"),
        STOCK_LEVEL("Stock Level"),
        PRODUCT_ROTATION("Product Rotation");

        private final String displayName;

        ReportType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
