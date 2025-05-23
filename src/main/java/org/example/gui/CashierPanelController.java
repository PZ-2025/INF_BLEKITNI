/*
 * Classname: CashierPanelController
 * Version information: 1.7
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


package org.example.gui;

import javafx.animation.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import org.example.database.*;
import org.example.sys.*;
import org.example.database.ReportRepository;
import org.example.sys.Report;
import org.example.sys.PeriodType;
import org.hibernate.Session;
import pdf.SalesReportGenerator;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.*;
import java.util.List;

public class CashierPanelController {

    private final CashierPanel cashierPanel;
    private final ReportRepository reportRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private boolean reportGeneratedInCurrentSession = false;


    // Ścieżka do katalogu z raportami
    private static final String REPORTS_DIRECTORY = "reports";

    public CashierPanelController(CashierPanel cashierPanel) {
        this.cashierPanel = cashierPanel;
        this.reportRepository = new ReportRepository();
        this.transactionRepository = new TransactionRepository();
        this.userRepository = new UserRepository();

        // Utworzenie katalogu na raporty, jeśli nie istnieje
        File reportsDir = new File(REPORTS_DIRECTORY);
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
    }

    // Ekran sprzedaży
    public void showSalesScreen() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Button newTransactionButton = cashierPanel.createStyledButton("Rozpocznij nową transakcję");
        newTransactionButton.setOnAction(e -> startNewTransaction());
        layout.getChildren().add(newTransactionButton);
        cashierPanel.setCenterPane(layout);
    }

    private void startNewTransaction() {
        Stage dialog = createStyledDialog("Nowa transakcja");
        dialog.setMinWidth(800);
        dialog.setMinHeight(600);

        ObservableList<TransactionItem> cartItems = FXCollections.observableArrayList();
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));

        VBox productSearchBox = new VBox(10);
        Label searchLabel = new Label("Wyszukaj produkt:");
        TextField searchField = createStyledTextField("Wpisz nazwę produktu...");
        TableView<Product> productTable = createProductTableWithSearch(searchField);

        HBox quantityBox = new HBox(10);
        Label quantityLabel = new Label("Ilość:");
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, 1);
        quantitySpinner.setEditable(true);
        quantitySpinner.setPrefWidth(100);
        Button addToCartButton = cashierPanel.createStyledButton("Dodaj do koszyka");

        quantityBox.getChildren().addAll(quantityLabel, quantitySpinner, addToCartButton);
        quantityBox.setAlignment(Pos.CENTER_LEFT);

        productSearchBox.getChildren().addAll(searchLabel, searchField, productTable, quantityBox);

        VBox cartBox = new VBox(10);
        Label cartLabel = new Label("Koszyk:");
        TableView<TransactionItem> cartTable = createCartTable();
        cartTable.setItems(cartItems);

        HBox totalBox = new HBox(10);
        Label totalLabel = new Label("Suma:");
        Label totalPriceLabel = new Label("0.00 zł");
        totalPriceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        totalBox.getChildren().addAll(totalLabel, totalPriceLabel);
        totalBox.setAlignment(Pos.CENTER_RIGHT);

        HBox buttonBox = new HBox(10);
        Button confirmButton = cashierPanel.createStyledButton("Zatwierdź transakcję", "#27AE60");
        Button cancelButton = cashierPanel.createStyledButton("Anuluj", "#E74C3C");
        buttonBox.getChildren().addAll(confirmButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        cartBox.getChildren().addAll(cartLabel, cartTable, totalBox, buttonBox);
        addToCartButton.setOnAction(e -> {
            Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                int quantity = quantitySpinner.getValue();
                int availableQuantity = getAvailableQuantity(selectedProduct);

                if (availableQuantity < quantity) {
                    showNotification("Błąd", "Niewystarczająca ilość produktu. Dostępne: " + availableQuantity);
                    return;
                }

                boolean found = false;
                for (TransactionItem item : cartItems) {
                    if (item.getProduct().getId() == selectedProduct.getId()) {
                        int newQuantity = item.getQuantity() + quantity;
                        if (newQuantity > availableQuantity) {
                            showNotification("Błąd", "Niewystarczająca ilość produktu. Dostępne: " + availableQuantity);
                            return;
                        }
                        item.setQuantity(newQuantity);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    cartItems.add(new TransactionItem(selectedProduct, quantity));
                }

                cartTable.refresh();
                updateTotalPrice(cartItems, totalPriceLabel);
            }
        });

        confirmButton.setOnAction(e -> {
            if (cartItems.isEmpty()) {
                showNotification("Błąd", "Koszyk jest pusty. Dodaj produkty do koszyka.");
                return;
            }
            saveTransaction(cartItems, dialog);
        });

        cancelButton.setOnAction(e -> dialog.close());

        mainLayout.setLeft(productSearchBox);
        mainLayout.setRight(cartBox);

        Scene scene = new Scene(mainLayout);
        dialog.setScene(scene);
        dialog.show();
    }

    public void showSalesReportsPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Nagłówek
        Label titleLabel = new Label("Raporty sprzedaży");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Tabela raportów
        TableView<Report> tableView = createReportTable();

        // Przyciski akcji
        HBox buttons = new HBox(10);
        Button newReportButton = cashierPanel.createStyledButton("Nowy raport", "#27AE60");
        Button refreshButton = cashierPanel.createStyledButton("Odśwież", "#3498DB");

        newReportButton.setOnAction(e -> showReportDialog());
        refreshButton.setOnAction(e -> refreshReportTable(tableView));

        buttons.getChildren().addAll(newReportButton, refreshButton);
        buttons.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(titleLabel, tableView, buttons);
        cashierPanel.setCenterPane(layout);

        // Załadowanie danych do tabeli
        refreshReportTable(tableView);
    }

    private void refreshReportTable(TableView<Report> tableView) {
        List<Report> reports = reportRepository.getAllReports();
        tableView.setItems(FXCollections.observableArrayList(reports));
    }

    private LocalDate[] calculateReportDates(PeriodType periodType, LocalDate selectedDate) {
        return switch (periodType) {
            case DAILY -> new LocalDate[]{selectedDate, selectedDate};
            case MONTHLY -> {
                LocalDate start = selectedDate.withDayOfMonth(1);
                yield new LocalDate[]{start, start.plusMonths(1).minusDays(1)};
            }
            case YEARLY -> {
                LocalDate start = selectedDate.withDayOfYear(1);
                yield new LocalDate[]{start, start.plusYears(1).minusDays(1)};
            }
        };
    }

    private void showReportDialog() {
        Stage dialog = createStyledDialog("Generowanie raportu sprzedaży");

        // Wybór typu raportu
        Label typeLabel = new Label("Typ raportu:");
        ComboBox<String> typeBox = createStyledComboBox(
                PeriodType.DAILY.getDisplayName(),
                PeriodType.MONTHLY.getDisplayName(),
                PeriodType.YEARLY.getDisplayName()
        );
        // Domyślnie wybierz raport dzienny
        typeBox.setValue(PeriodType.DAILY.getDisplayName());

        // Wybór daty
        Label dateLabel = new Label("Data raportu:");
        DatePicker datePicker = createStyledDatePicker();
        datePicker.setValue(LocalDate.now());

        // Wybór kategorii (opcjonalnie)
        Label categoryLabel = new Label("Kategorie produktów (opcjonalnie):");
        ListView<String> categoryListView = new ListView<>();
        categoryListView.setPrefHeight(150);
        categoryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Pobranie dostępnych kategorii
        ProductRepository productRepo = new ProductRepository();
        List<String> categories = productRepo.getCategories();
        categoryListView.setItems(FXCollections.observableArrayList(categories));
        productRepo.close();

        // Przyciski
        Button generateBtn = cashierPanel.createStyledButton("Generuj raport", "#2980B9");
        Button cancelBtn = cashierPanel.createStyledButton("Anuluj", "#E74C3C");

        HBox buttonBox = new HBox(10, generateBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        generateBtn.setOnAction(e -> {
            String reportTypeStr = typeBox.getValue();
            LocalDate selectedDate = datePicker.getValue();
            List<String> selectedCategories = new ArrayList<>(categoryListView.getSelectionModel().getSelectedItems());

            if (selectedDate == null) {
                showNotification("Błąd", "Wybierz datę raportu.");
                return;
            }

            try {
                // Wyświetl informację o rozpoczęciu generowania raportu
                System.out.println("Rozpoczynam generowanie raportu typu: " + reportTypeStr);

                PeriodType periodType = getPeriodTypeFromString(reportTypeStr);
                LocalDate[] dates = calculateReportDates(periodType, selectedDate);

                System.out.println("Generowanie raportu dla okresu: " + dates[0] + " do " + dates[1]);

                // Sprawdź, czy istnieją transakcje w wybranym okresie
                Date d1 = Date.from(dates[0].atStartOfDay(ZoneId.systemDefault()).toInstant());
                Date d2 = Date.from(dates[1].atTime(23,59,59).atZone(ZoneId.systemDefault()).toInstant());
                List<Transaction> transactions = transactionRepository.getTransactionsBetweenDates(d1, d2);

                if (transactions.isEmpty()) {
                    // Wyświetl komunikat o braku danych
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Brak danych");
                    alert.setHeaderText("Brak transakcji w wybranym okresie");
                    alert.setContentText("Nie znaleziono żadnych transakcji w okresie od " +
                            dates[0].format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                            " do " +
                            dates[1].format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                            ".\n\nNie można wygenerować raportu bez danych.");

                    alert.showAndWait();

                    // Mimo braku raportu, oznaczamy flagę jako true, aby umożliwić zamknięcie aplikacji
                    reportGeneratedInCurrentSession = true;

                    // Zapisujemy informację o próbie wygenerowania raportu
                    saveEmptyReportInfo(periodType, dates[0], dates[1]);

                    System.out.println("Próba wygenerowania raportu bez danych, flaga ustawiona na: " + reportGeneratedInCurrentSession);

                    dialog.close();
                    return;
                }

                String reportPath = generateSalesReport(
                        periodType,
                        dates[0], // startDate
                        dates[1], // endDate
                        selectedCategories
                );

                System.out.println("Raport wygenerowany pomyślnie: " + reportPath);
                System.out.println("Stan flagi przed zapisaniem informacji: " + reportGeneratedInCurrentSession);

                // Zapisz informacje o raporcie w bazie danych
                saveReportInfo(periodType, dates[0], dates[1], reportPath);

                // Bezpośrednio ustaw flagę po zapisaniu raportu
                reportGeneratedInCurrentSession = true;

                System.out.println("Stan flagi po zapisaniu informacji: " + reportGeneratedInCurrentSession);

                // Wyświetl powiadomienie o sukcesie
                showNotification("Sukces", "Raport zapisano w: " + reportPath);

                // Zamknij dialog po wygenerowaniu raportu
                dialog.close();

                // Dodatkowe sprawdzenie po zamknięciu dialogu
                System.out.println("Stan flagi po zamknięciu dialogu: " + reportGeneratedInCurrentSession);

            } catch (Exception ex) {
                ex.printStackTrace();
                showNotification("Błąd", "Nie udało się wygenerować raportu: " + ex.getMessage());
            }
        });

        cancelBtn.setOnAction(e -> {
            System.out.println("Anulowano generowanie raportu");
            dialog.close();
        });

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                typeLabel, typeBox,
                dateLabel, datePicker,
                categoryLabel, categoryListView,
                new Separator(),
                buttonBox
        );

        // Dodaj informację o stanie flagi przed otwarciem dialogu
        System.out.println("Stan flagi przed otwarciem dialogu: " + reportGeneratedInCurrentSession);

        setupDialog(dialog, root);

        // Dodaj obsługę zamknięcia okna
        dialog.setOnHidden(event -> {
            System.out.println("Dialog zamknięty, stan flagi: " + reportGeneratedInCurrentSession);
        });
    }

    /**
     * Zapisuje informację o próbie wygenerowania raportu bez danych.
     */
    private void saveEmptyReportInfo(PeriodType periodType, LocalDate startDate, LocalDate endDate) {
        // Pobranie zalogowanego pracownika
        Employee currentEmployee = userRepository.getCurrentEmployee();
        if (currentEmployee == null) {
            throw new IllegalStateException("Nie jesteś zalogowany.");
        }

        // Utworzenie obiektu raportu
        Report report = new Report();
        report.setEmployee(currentEmployee);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setReportType(periodType.getDisplayName() + " (brak danych)");
        report.setFilePath(""); // Brak ścieżki, ponieważ nie wygenerowano pliku

        // Zapisanie raportu w bazie danych
        reportRepository.addReport(report);

        // Upewnij się, że flaga jest ustawiona
        this.reportGeneratedInCurrentSession = true;

        // Dodaj log dla debugowania
        System.out.println("Zapisano informację o próbie wygenerowania raportu bez danych, flaga ustawiona na: " + this.reportGeneratedInCurrentSession);
    }

    private PeriodType getPeriodTypeFromString(String typeStr) {
        return PeriodType.fromDisplay(typeStr);
    }

    public boolean isReportGeneratedInCurrentSession() {
        return reportGeneratedInCurrentSession;
    }

    private void saveReportInfo(PeriodType periodType, LocalDate startDate, LocalDate endDate, String reportPath) {
        // Pobranie zalogowanego pracownika
        Employee currentEmployee = userRepository.getCurrentEmployee();
        if (currentEmployee == null) {
            throw new IllegalStateException("Nie jesteś zalogowany.");
        }

        switch (periodType) {
            case DAILY:
                break;
            case MONTHLY:
                startDate = startDate.withDayOfMonth(1);
                endDate = startDate.plusMonths(1).minusDays(1);
                break;
            case YEARLY:
                startDate = startDate.withDayOfYear(1);
                endDate = startDate.plusYears(1).minusDays(1);
                break;
            default:
                throw new IllegalArgumentException("Nieprawidłowy typ okresu");
        }

        // Utworzenie obiektu raportu
        Report report = new Report();
        report.setEmployee(currentEmployee);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setReportType(periodType.getDisplayName());
        report.setFilePath(reportPath);

        // Zapisanie raportu w bazie danych
        reportRepository.addReport(report);

        // Upewnij się, że flaga jest ustawiona
        this.reportGeneratedInCurrentSession = true;

        // Dodaj log dla debugowania
        System.out.println("Raport wygenerowany, flaga ustawiona na: " + this.reportGeneratedInCurrentSession);
    }

    private TableView<Report> createReportTable() {
        TableView<Report> tableView = new TableView<>();
        tableView.setMinHeight(300);

        TableColumn<Report, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(50);

        TableColumn<Report, String> typeColumn = new TableColumn<>("Typ raportu");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        typeColumn.setPrefWidth(120);

        TableColumn<Report, LocalDate> dateStartColumn = new TableColumn<>("Od");
        dateStartColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        dateStartColumn.setPrefWidth(100);

        TableColumn<Report, LocalDate> dateEndColumn = new TableColumn<>("Do");
        dateEndColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        dateEndColumn.setPrefWidth(100);


        TableColumn<Report, String> employeeColumn = new TableColumn<>("Wygenerował");
        employeeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmployee().getName() + " " +
                        cellData.getValue().getEmployee().getSurname()));
        employeeColumn.setPrefWidth(150);

        // Kolumna z przyciskami akcji
        TableColumn<Report, Void> actionsColumn = new TableColumn<>("Akcje");
        actionsColumn.setPrefWidth(200);
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("Podgląd");
            private final Button openButton = new Button("Otwórz");
            private final Button deleteButton = new Button("Usuń");
            private final HBox pane = new HBox(5, viewButton, openButton, deleteButton);

            {
                viewButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
                openButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");

                viewButton.setOnAction(event -> {
                    Report report = getTableView().getItems().get(getIndex());
                    showReportDetails(report);
                });

                openButton.setOnAction(event -> {
                    Report report = getTableView().getItems().get(getIndex());
                    openReportFile(report.getFilePath());
                });

                deleteButton.setOnAction(event -> {
                    Report report = getTableView().getItems().get(getIndex());
                    confirmAndDeleteReport(report, getTableView());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tableView.getColumns().addAll(idColumn, typeColumn, dateStartColumn, dateEndColumn,
                employeeColumn, actionsColumn);
        return tableView;
    }

    private void openReportFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                showNotification("Błąd", "Plik nie istnieje: " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showNotification("Błąd", "Nie można otworzyć pliku: " + e.getMessage());
        }
    }

    private void confirmAndDeleteReport(Report report, TableView<Report> tableView) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potwierdzenie usunięcia");
        alert.setHeaderText("Czy na pewno chcesz usunąć ten raport?");
        alert.setContentText("Ta operacja jest nieodwracalna.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reportRepository.removeReport(report.getId());
                refreshReportTable(tableView);
                showNotification("Sukces", "Raport został usunięty.");
            } catch (Exception e) {
                e.printStackTrace();
                showNotification("Błąd", "Nie udało się usunąć raportu: " + e.getMessage());
            }
        }
    }

    private void showReportDetails(Report report) {
        Stage dialog = createStyledDialog("Szczegóły raportu");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        // Informacje o raporcie
        Label titleLabel = new Label("Raport ID: " + report.getId());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10);
        infoGrid.setVgap(10);
        infoGrid.setPadding(new Insets(10));

        int row = 0;
        infoGrid.add(new Label("Typ raportu:"), 0, row);
        infoGrid.add(new Label(report.getReportType()), 1, row++);

        infoGrid.add(new Label("Okres:"), 0, row);
        infoGrid.add(new Label(report.getStartDate() + " - " + report.getEndDate()), 1, row++);

        infoGrid.add(new Label("Data wygenerowania:"), 0, row);
        //infoGrid.add(new Label(report.getDataWygenerowania().toString()), 1, row++);

        infoGrid.add(new Label("Wygenerował:"), 0, row);
        infoGrid.add(new Label(report.getEmployee().getName() + " " + report.getEmployee().getSurname()), 1, row++);

        infoGrid.add(new Label("Ścieżka pliku:"), 0, row);
        infoGrid.add(new Label(report.getFilePath()), 1, row++);

        // Przyciski akcji
        HBox buttonBox = new HBox(10);
        Button openButton = cashierPanel.createStyledButton("Otwórz plik", "#27AE60");
        Button closeButton = cashierPanel.createStyledButton("Zamknij", "#7F8C8D");

        openButton.setOnAction(e -> {
            openReportFile(report.getFilePath());
        });

        closeButton.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(openButton, closeButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        content.getChildren().addAll(titleLabel, infoGrid, new Separator(), buttonBox);

        Scene scene = new Scene(content);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void simulateGenerateReport(String reportType, LocalDate date, List<Transaction> transactions) {
        try {
            // Konwersja typu raportu
            PeriodType periodType;
            switch (reportType) {
                case "Dzienny":
                    periodType = PeriodType.DAILY;
                    break;
                case "Tygodniowy":
                case "Miesięczny":
                    periodType = PeriodType.MONTHLY;
                    break;
                default:
                    periodType = PeriodType.YEARLY;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Błąd", "Nie udało się wygenerować raportu: " + e.getMessage());
        }
    }

    private TableView<Product> createProductTableWithSearch(TextField searchField) {
        TableView<Product> table = new TableView<>();
        table.setMinHeight(300);

        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Product, String> nameCol = new TableColumn<>("Nazwa");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> categoryCol = new TableColumn<>("Kategoria");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Cena");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        table.getColumns().addAll(idCol, nameCol, categoryCol, priceCol);

        ProductRepository productRepo = new ProductRepository();
        ObservableList<Product> productList = FXCollections.observableArrayList(productRepo.getAllProducts());
        productRepo.close();

        table.setItems(productList);
        productRepo.close();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                table.setItems(productList);
            } else {
                ObservableList<Product> filteredList = FXCollections.observableArrayList();
                for (Product product : productList) {
                    if (product.getName().toLowerCase().contains(newValue.toLowerCase()) ||
                            product.getCategory().toLowerCase().contains(newValue.toLowerCase())) {
                        filteredList.add(product);
                    }
                }
                table.setItems(filteredList);
            }
        });

        return table;
    }

    private int getAvailableQuantity(Product product) {
        WarehouseRepository warehouseRepo = new WarehouseRepository();
        int quantity = 0;
        try {
            Warehouse state = warehouseRepo.findStateByProductId(product.getId());
            if (state != null) {
                quantity = state.getQuantity();
            }
        } finally {
            warehouseRepo.close();
        }
        return quantity;
    }

    private TableView<TransactionItem> createCartTable() {
        TableView<TransactionItem> table = new TableView<>();
        table.setMinHeight(300);

        TableColumn<TransactionItem, String> nameCol = new TableColumn<>("Nazwa");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduct().getName()));

        TableColumn<TransactionItem, Integer> quantityCol = new TableColumn<>("Ilość");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantityCol.setOnEditCommit(event -> {
            TransactionItem item = event.getRowValue();
            int newValue = event.getNewValue();
            int maxQuantity = getAvailableQuantity(item.getProduct());
            if (newValue > 0 && newValue <= maxQuantity) {
                item.setQuantity(newValue);
                updateTotalPrice(table.getItems(), null);
            } else {
                table.refresh();
                showNotification("Błąd", "Nieprawidłowa ilość. Maksymalna dostępna ilość: " + maxQuantity);
            }
        });

        TableColumn<TransactionItem, Double> priceCol = new TableColumn<>("Cena jedn.");
        priceCol.setCellValueFactory(cell ->
                new SimpleDoubleProperty(
                        cell.getValue().getProduct().getPrice().doubleValue()
                ).asObject());

        TableColumn<TransactionItem, Double> totalCol = new TableColumn<>("Suma");
        totalCol.setCellValueFactory(cell ->
                new SimpleDoubleProperty(
                        cell.getValue().getProduct().getPrice().doubleValue() *
                                cell.getValue().getQuantity()
                ).asObject());


        TableColumn<TransactionItem, Void> actionCol = new TableColumn<>("Akcje");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button removeButton = new Button("Usuń");

            {
                removeButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
                removeButton.setOnAction(event -> {
                    TransactionItem item = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(item);
                    updateTotalPrice(getTableView().getItems(), null);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeButton);
            }
        });

        table.getColumns().addAll(nameCol, quantityCol, priceCol, totalCol, actionCol);
        table.setEditable(true);
        return table;
    }

    private void updateTotalPrice(ObservableList<TransactionItem> items, Label totalPriceLabel) {
        double total = 0;
        for (TransactionItem item : items) {
            total += item.getProduct().getPrice().doubleValue() * item.getQuantity();
        }
        if (totalPriceLabel != null) {
            totalPriceLabel.setText(String.format("%.2f zł", total));
        }
    }

    private void saveTransaction(ObservableList<TransactionItem> items, Stage dialog) {
        try {
            Employee currentEmployee = userRepository.getCurrentEmployee();
            if (currentEmployee == null) {
                showNotification("Błąd", "Nie jesteś zalogowany.");
                return;
            }

            // Zapisz transakcję
            Transaction transaction = new Transaction();
            transaction.setEmployee(currentEmployee);
            transaction.setDate(new Date());
            transactionRepository.addTransaction(transaction);

            // Pobierz ID zapisanej transakcji
            int transactionId = transaction.getId();

            // Zapisz produkty w transakcji za pomocą natywnego SQL
            Session session = transactionRepository.getSession();
            session.beginTransaction();

            WarehouseRepository warehouseRepo = new WarehouseRepository();
            for (TransactionItem item : items) {
                int productId = item.getProduct().getId();
                int quantity = item.getQuantity();

                // Aktualizuj stan magazynowy
                int availableQuantity = getAvailableQuantity(item.getProduct());
                int newQuantity = availableQuantity - quantity;
                warehouseRepo.setProductQuantity(productId, newQuantity);

                // Zapisz relację transakcja-produkt za pomocą natywnego SQL
                session.createNativeQuery(
                                "INSERT INTO Transakcje_Produkty (Id_transakcji, Id_produktu, Ilosc) VALUES (:txId, :prodId, :qty)",
                                Void.class)
                        .setParameter("txId", transactionId)
                        .setParameter("prodId", productId)
                        .setParameter("qty", quantity)
                        .executeUpdate();
            }

            session.getTransaction().commit();
            session.close();
            warehouseRepo.close();

            showNotification("Sukces", "Transakcja została zapisana pomyślnie.");
            dialog.close();

        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Błąd", "Wystąpił błąd podczas zapisywania transakcji: " + e.getMessage());
        }
    }


    // Zgłoszenie problemu
    public void showIssueReportPanel() {
        Stage dialog = createStyledDialog("Zgłoszenie problemu");
        ComboBox<String> typeBox = createStyledComboBox("Awaria sprzętu", "Błąd oprogramowania", "Inne");
        TextArea description = createStyledTextArea("Opisz problem...");

        Button sendButton = cashierPanel.createStyledButton("Wyślij", "#27AE60");
        Button cancelButton = cashierPanel.createStyledButton("Anuluj", "#E74C3C");

        sendButton.setOnAction(e -> {
            if (typeBox.getValue() == null || description.getText().trim().isEmpty()) {
                showNotification("Błąd", "Uzupełnij wszystkie pola.");
                return;
            }
            showNotification("Sukces", "Zgłoszenie wysłane.");
            dialog.close();
        });

        cancelButton.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(10, sendButton, cancelButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                new Label("Typ zgłoszenia:"), typeBox,
                new Label("Opis:"), description,
                buttons
        );

        setupDialog(dialog, root);
    }

    public void showCloseShiftPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Sprawdź, czy raport dzienny został wygenerowany dzisiaj
        boolean reportGeneratedToday = isDailyReportGeneratedToday();
        System.out.println("Czy raport dzienny został wygenerowany dzisiaj: " + reportGeneratedToday);

        // Użyj obu warunków do decyzji
        if (!reportGeneratedInCurrentSession && !reportGeneratedToday) {
            Label warningLabel = new Label("Uwaga: Nie wygenerowano jeszcze raportu dziennego!");
            warningLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");

            Button generateReportButton = cashierPanel.createStyledButton("Wygeneruj raport dzienny", "#3498DB");
            generateReportButton.setOnAction(e -> showReportDialog());

            layout.getChildren().addAll(warningLabel, generateReportButton);
        }

        Button confirmButton = cashierPanel.createStyledButton("Potwierdź zamknięcie zmiany", "#E67E22");
        confirmButton.setOnAction(e -> {
            // Sprawdź ponownie, bo mogło się zmienić
            boolean currentReportGeneratedToday = isDailyReportGeneratedToday();

            if (!reportGeneratedInCurrentSession && !currentReportGeneratedToday) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Ostrzeżenie");
                alert.setHeaderText("Nie wygenerowano raportu dziennego");
                alert.setContentText("Przed zamknięciem zmiany należy wygenerować raport dzienny. Czy chcesz to zrobić teraz?");

                ButtonType generateButton = new ButtonType("Generuj raport");
                ButtonType ignoreButton = new ButtonType("Ignoruj i zamknij");
                ButtonType cancelButton = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(generateButton, ignoreButton, cancelButton);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get() == generateButton) {
                        showReportDialog();
                        return;
                    } else if (result.get() == cancelButton) {
                        return;
                    }
                    // Jeśli wybrano "Ignoruj i zamknij", kontynuuj
                }
            }

            // Kod zamykania zmiany
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Zamknięcie zmiany");
            successAlert.setHeaderText("Zmiana została pomyślnie zamknięta");
            successAlert.setContentText("Dziękujemy za pracę w tej zmianie!");
            successAlert.showAndWait();

            // Resetuj flagę po zamknięciu zmiany
            reportGeneratedInCurrentSession = false;
        });

        layout.getChildren().add(confirmButton);
        cashierPanel.setCenterPane(layout);
    }

    public void resetReportGeneratedFlag() {
        reportGeneratedInCurrentSession = false;
    }

    public boolean isDailyReportGeneratedToday() {
        LocalDate today = LocalDate.now();
        Employee currentEmployee = userRepository.getCurrentEmployee();

        if (currentEmployee == null) {
            System.out.println("isDailyReportGeneratedToday: Brak zalogowanego pracownika");
            return false;
        }

        List<Report> todaysReports = reportRepository.getEmployeeDayReport(
                currentEmployee.getId(),
                today);

        System.out.println("isDailyReportGeneratedToday: Znaleziono " + todaysReports.size() + " raportów na dzisiaj");

        return !todaysReports.isEmpty();
    }

    public void markReportAsGenerated() {
        System.out.println("Oznaczanie raportu jako wygenerowany");
        this.reportGeneratedInCurrentSession = true;
    }

    public void checkReportFlagState(String panelName) {
        System.out.println("Sprawdzanie flagi przy przełączaniu do panelu " + panelName + ": " + reportGeneratedInCurrentSession);
    }

    public void showAbsenceRequestForm() {
        Stage stage = new Stage();
        stage.setTitle("Wniosek o nieobecność");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        UserRepository userRepository = new UserRepository();
        Employee currentEmployee = userRepository.getCurrentEmployee();
        if (currentEmployee == null) {
            showNotification("Błąd", "Nie jesteś zalogowany.");
            userRepository.close();
            return;
        }

        Label employeeInfoLabel = new Label("Pracownik: %s %s (ID: %d)"
                .formatted(currentEmployee.getName(), currentEmployee.getSurname(), currentEmployee.getId()));
        employeeInfoLabel.setStyle("-fx-font-weight: bold;");

        Label reasonLabel = new Label("Opis:");
        TextField reasonField = new TextField();

        Label fromDateLabel = new Label("Data od:");
        DatePicker fromDatePicker = new DatePicker();

        Label toDateLabel = new Label("Data do:");
        DatePicker toDatePicker = new DatePicker();

        Label typeLabel = new Label("Typ wniosku:");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Urlop wypoczynkowy", "Urlop na żądanie", "Zwolnienie lekarskie", "Inne");
        typeComboBox.setValue("Urlop wypoczynkowy");

        Button submitButton = cashierPanel.createStyledButton("Wyślij wniosek", "#27AE60");
        submitButton.setOnAction(e -> {
            if (validateAbsenceForm(reasonField.getText(), fromDatePicker.getValue(), toDatePicker.getValue())) {
                showNotification("Sukces", "Wniosek został wysłany.");
                stage.close();
            }
        });

        grid.add(employeeInfoLabel, 0, 0, 2, 1);
        grid.add(typeLabel, 0, 1);
        grid.add(typeComboBox, 1, 1);
        grid.add(reasonLabel, 0, 2);
        grid.add(reasonField, 1, 2);
        grid.add(fromDateLabel, 0, 3);
        grid.add(fromDatePicker, 1, 3);
        grid.add(toDateLabel, 0, 4);
        grid.add(toDatePicker, 1, 4);
        grid.add(submitButton, 1, 5);

        Scene scene = new Scene(grid, 400, 350);
        stage.setScene(scene);
        stage.setOnHidden(event -> userRepository.close());
        stage.show();
    }

    private boolean validateAbsenceForm(String reason, LocalDate fromDate, LocalDate toDate) {
        if (reason == null || reason.trim().isEmpty()) {
            showNotification("Błąd", "Musisz podać powód nieobecności");
            return false;
        }
        if (fromDate == null || toDate == null) {
            showNotification("Błąd", "Musisz wybrać datę rozpoczęcia i zakończenia");
            return false;
        }
        if (fromDate.isAfter(toDate)) {
            showNotification("Błąd", "Data rozpoczęcia nie może być późniejsza niż zakończenia");
            return false;
        }
        return true;
    }

    private void animateDialog(Stage dialog, Pane root) {
        FadeTransition ft = new FadeTransition(Duration.millis(300), root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);

        TranslateTransition tt = new TranslateTransition(Duration.millis(300), root);
        tt.setFromY(-20);
        tt.setToY(0);

        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.play();
    }

    private Stage createStyledDialog(String title) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);
        return dialog;
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 8px;");
        return field;
    }

    private void showNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private <T> ComboBox<T> createStyledComboBox(T... items) {
        ComboBox<T> box = new ComboBox<>(FXCollections.observableArrayList(items));
        box.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 6px;");
        return box;
    }

    private DatePicker createStyledDatePicker() {
        DatePicker dp = new DatePicker();
        dp.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 6px;");
        return dp;
    }

    private TextArea createStyledTextArea(String prompt) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 8px;");
        return ta;
    }

    private void setupDialog(Stage dialog, Pane root) {
        Scene scene = new Scene(root);
        dialog.setScene(scene);
        animateDialog(dialog, root);
        dialog.showAndWait();
    }

    public void logout() {
        System.out.println("Sprawdzanie flagi przed wylogowaniem: " + reportGeneratedInCurrentSession);

        // Sprawdź, czy raport dzienny został wygenerowany dzisiaj
        boolean reportGeneratedToday = isDailyReportGeneratedToday();
        System.out.println("Czy raport dzienny został wygenerowany dzisiaj: " + reportGeneratedToday);

        // Użyj obu warunków do decyzji
        if (!reportGeneratedInCurrentSession && !reportGeneratedToday) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Uwaga");
            alert.setHeaderText("Nie wygenerowano raportu dziennego");
            alert.setContentText("Czy na pewno chcesz się wylogować bez wygenerowania raportu dziennego?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() != ButtonType.OK) {
                return; // Anuluj wylogowanie
            }
        }

        UserRepository.resetCurrentEmployee();
        Stage primaryStage = cashierPanel.getPrimaryStage();

        // Usuń handler zamknięcia okna przed zamknięciem
        primaryStage.setOnCloseRequest(null);

        primaryStage.close();
        HelloApplication.showLoginScreen(primaryStage);
    }

    public static class TransactionItem {
        private final Product product;
        private int quantity;

        public TransactionItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getTotal() {
            return product.getPrice().doubleValue() * quantity;
        }

    }

    /**
     * Generuje plik PDF z raportem sprzedaży.
     *
     * @return absolutna ścieżka do wygenerowanego pliku
     */
    private String generateSalesReport(PeriodType periodType,
                                       LocalDate startDate,
                                       LocalDate endDate,
                                       List<String> categories) throws Exception {

        // 1) Pobranie danych sprzedaży
        List<SalesReportGenerator.SalesRecord> salesData =
                getSalesDataForReport(startDate, endDate);

        if (salesData.isEmpty()) {
            throw new SalesReportGenerator.NoDataException(
                    "Brak danych transakcji dla wybranego zakresu dat.");
        }

        // 2) Konfiguracja generatora PDF
        SalesReportGenerator gen = new SalesReportGenerator();
        gen.setSalesData(salesData);

        // 3) Nazwa pliku
        String fileName = "raport_%s_%s_%s.pdf".formatted(
                periodType.getDisplayName().toLowerCase(),
                startDate.format(DateTimeFormatter.BASIC_ISO_DATE),
                endDate.format(DateTimeFormatter.BASIC_ISO_DATE));

        String outputPath = REPORTS_DIRECTORY + File.separator + fileName;

        // 4) Wywołanie JEDYNEJ dostępnej metody generateReport(...)
        SalesReportGenerator.PeriodType pdfType = toPdfPeriodType(periodType);
        gen.generateReport(outputPath, pdfType, categories == null ? List.of() : categories);

        return new File(outputPath).getAbsolutePath();
    }

    private List<SalesReportGenerator.SalesRecord> getSalesDataForReport(
            LocalDate startDate, LocalDate endDate) {

        Date d1 = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date d2 = Date.from(endDate.atTime(23,59,59).atZone(ZoneId.systemDefault()).toInstant());

        List<Transaction> txs = transactionRepository.getTransactionsBetweenDates(d1, d2);
        List<SalesReportGenerator.SalesRecord> out = new ArrayList<>();

        for (Transaction tx : txs) {
            LocalDateTime txTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(tx.getDate().getTime()),
                    ZoneId.systemDefault());

            // Używamy nowej struktury TransactionProduct
            for (TransactionProduct tp : tx.getTransactionProducts()) {
                Product p = tp.getProduct();
                int qty = tp.getQuantity();

                out.add(new SalesReportGenerator.SalesRecord(
                        tx.getId(),
                        txTime,
                        p.getName(),
                        p.getCategory(),
                        qty,
                        qty * p.getPrice().doubleValue()   // <- zamiast BigDecimal * int
                ));
            }
        }
        return out;
    }



    // Dodaj to do klasy CashierPanelController:
    private pdf.SalesReportGenerator.PeriodType toPdfPeriodType(org.example.sys.PeriodType periodType) {
        return switch (periodType) {
            case DAILY   -> pdf.SalesReportGenerator.PeriodType.DAILY;
            case MONTHLY -> pdf.SalesReportGenerator.PeriodType.MONTHLY;
            case YEARLY  -> pdf.SalesReportGenerator.PeriodType.YEARLY;
        };
    }
}