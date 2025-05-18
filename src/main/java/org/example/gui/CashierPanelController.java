/*
 * Classname: CashierPanelController
 * Version information: 1.1
 * Date: 2025-05-17
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import org.example.database.AbsenceRequestRepository;
import org.example.database.ProductRepository;
import org.example.database.ReportRepository;
import org.example.database.TransactionRepository;
import org.example.database.TechnicalIssueRepository;
import org.example.database.UserRepository;
import org.example.sys.*;
import pdf.SalesReportGenerator;
import pdf.SalesReportGenerator.PeriodType;
import pdf.SalesReportGenerator.SalesRecord;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CashierPanelController {
    private final CashierPanel cashierPanel;
    private final ReportRepository reportRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

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
                if (selectedProduct.getQuantity() < quantity) {
                    showNotification("Błąd", "Niewystarczająca ilość produktu.");
                    return;
                }
                boolean found = false;
                for (TransactionItem item : cartItems) {
                    if (item.getProduct().getId() == selectedProduct.getId()) {
                        int newQuantity = item.getQuantity() + quantity;
                        if (newQuantity > selectedProduct.getQuantity()) {
                            showNotification("Błąd", "Niewystarczająca ilość produktu.");
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
                showNotification("Błąd", "Koszyk jest pusty.");
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
        List<Report> reports = reportRepository.pobierzWszystkieRaporty();
        tableView.setItems(FXCollections.observableArrayList(reports));
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
        List<String> categories = productRepo.pobierzKategorie();
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
                // Generowanie raportu
                PeriodType periodType = getPeriodTypeFromString(reportTypeStr);
                //String reportPath = generateSalesReport(periodType, selectedDate, selectedCategories);

                // Zapisanie informacji o raporcie w bazie danych
                //saveReportInfo(periodType, selectedDate, reportPath);

                showNotification("Sukces", "Raport został wygenerowany.");
                dialog.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                showNotification("Błąd", "Nie udało się wygenerować raportu: " + ex.getMessage());
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                typeLabel, typeBox,
                dateLabel, datePicker,
                categoryLabel, categoryListView,
                new Separator(),
                buttonBox
        );

        setupDialog(dialog, root);
    }

    private PeriodType getPeriodTypeFromString(String typeStr) {
        if (typeStr.equals(PeriodType.DAILY.getDisplayName())) {
            return PeriodType.DAILY;
        } else if (typeStr.equals(PeriodType.MONTHLY.getDisplayName())) {
            return PeriodType.MONTHLY;
        } else if (typeStr.equals(PeriodType.YEARLY.getDisplayName())) {
            return PeriodType.YEARLY;
        }
        return PeriodType.DAILY; // domyślnie
    }

    /*private String generateSalesReport(PeriodType periodType, LocalDate selectedDate, List<String> categories) throws Exception {
        // Pobranie danych transakcji
        List<SalesRecord> salesData = getSalesDataForReport(periodType, selectedDate);

        if (salesData.isEmpty()) {
            throw new Exception("Brak danych transakcji dla wybranego okresu.");
        }

        // Utworzenie generatora raportów
        SalesReportGenerator reportGenerator = new SalesReportGenerator();
        reportGenerator.setSalesData(salesData);

        // Ustalenie nazwy pliku
        String periodName = switch (periodType) {
            case DAILY -> "dzienny";
            case MONTHLY -> "miesięczny";
            case YEARLY -> "roczny";
        };

        String fileName = String.format("raport_%s_%s.pdf", periodName, selectedDate.format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE));
        String outputPath = REPORTS_DIRECTORY + File.separator + fileName;

        // Generowanie raportu
        reportGenerator.generateReport(outputPath, periodType, categories);

        return outputPath;
    }*/

    /*private List<SalesRecord> getSalesDataForReport(PeriodType periodType, LocalDate selectedDate) {
        // Pobranie transakcji z bazy danych
        List<Transaction> transactions = transactionRepository.getTransactionsByPeriod(selectedDate, periodType);

        // Konwersja danych transakcji do formatu wymaganego przez generator raportów
        List<SalesRecord> salesRecords = new ArrayList<>();

        for (Transaction transaction : transactions) {
            // Dla każdego produktu w transakcji tworzymy rekord sprzedaży
            for (Warehouse product : transaction.getProdukty()) {
                // Zakładamy, że Warehouse zawiera informacje o produkcie i ilości
                // W rzeczywistej implementacji należy dostosować to do struktury danych

                LocalDateTime transactionDateTime = transaction.getData()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                // Tworzenie rekordu sprzedaży
                SalesRecord salesRecord = new SalesRecord(
                        transaction.getId(),
                        transactionDateTime,
                        product.getNazwa(),
                        product.getKategoria(),
                        product.getIlosc(),
                        product.getCena() * product.getIlosc()
                );

                salesRecords.add(salesRecord);
            }
        }

        return salesRecords;
    }*/

    // NOWA METODA
    private void saveReportInfo(PeriodType periodType, LocalDate selectedDate, String reportPath) {
        // Pobranie zalogowanego pracownika
        Employee currentEmployee = userRepository.getCurrentEmployee();
        if (currentEmployee == null) {
            throw new IllegalStateException("Nie jesteś zalogowany.");
        }

        // Ustalenie dat raportu
        LocalDate startDate, endDate;

        switch (periodType) {
            case DAILY:
                startDate = selectedDate;
                endDate = selectedDate;
                break;
            case MONTHLY:
                startDate = selectedDate.withDayOfMonth(1);
                endDate = startDate.plusMonths(1).minusDays(1);
                break;
            case YEARLY:
                startDate = selectedDate.withDayOfYear(1);
                endDate = startDate.plusYears(1).minusDays(1);
                break;
            default:
                throw new IllegalArgumentException("Nieprawidłowy typ okresu");
        }

        // Utworzenie obiektu raportu
        Report report = new Report();
        report.setPracownik(currentEmployee);
        report.setDataPoczatku(startDate);
        report.setDataZakonczenia(endDate);
        report.setTypRaportu(periodType.getDisplayName());
        report.setSciezkaPliku(reportPath);
        //report.setDataWygenerowania(LocalDate.now());

        // Zapisanie raportu w bazie danych
        reportRepository.dodajRaport(report);
    }

    private TableView<Report> createReportTable() {
        TableView<Report> tableView = new TableView<>();
        tableView.setMinHeight(300);

        TableColumn<Report, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(50);

        TableColumn<Report, String> typeColumn = new TableColumn<>("Typ raportu");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("typRaportu"));
        typeColumn.setPrefWidth(120);

        TableColumn<Report, LocalDate> dateStartColumn = new TableColumn<>("Od");
        dateStartColumn.setCellValueFactory(new PropertyValueFactory<>("dataPoczatku"));
        dateStartColumn.setPrefWidth(100);

        TableColumn<Report, LocalDate> dateEndColumn = new TableColumn<>("Do");
        dateEndColumn.setCellValueFactory(new PropertyValueFactory<>("dataZakonczenia"));
        dateEndColumn.setPrefWidth(100);

        TableColumn<Report, LocalDate> genDateColumn = new TableColumn<>("Data wygenerowania");
        genDateColumn.setCellValueFactory(new PropertyValueFactory<>("dataWygenerowania"));
        genDateColumn.setPrefWidth(150);

        TableColumn<Report, String> employeeColumn = new TableColumn<>("Wygenerował");
        employeeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPracownik().getName() + " " +
                        cellData.getValue().getPracownik().getSurname()));
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
                    openReportFile(report.getSciezkaPliku());
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
                genDateColumn, employeeColumn, actionsColumn);
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
                reportRepository.usunRaport(report.getId());
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
        infoGrid.add(new Label(report.getTypRaportu()), 1, row++);

        infoGrid.add(new Label("Okres:"), 0, row);
        infoGrid.add(new Label(report.getDataPoczatku() + " - " + report.getDataZakonczenia()), 1, row++);

        infoGrid.add(new Label("Data wygenerowania:"), 0, row);
        //infoGrid.add(new Label(report.getDataWygenerowania().toString()), 1, row++);

        infoGrid.add(new Label("Wygenerował:"), 0, row);
        infoGrid.add(new Label(report.getPracownik().getName() + " " + report.getPracownik().getSurname()), 1, row++);

        infoGrid.add(new Label("Ścieżka pliku:"), 0, row);
        infoGrid.add(new Label(report.getSciezkaPliku()), 1, row++);

        // Przyciski akcji
        HBox buttonBox = new HBox(10);
        Button openButton = cashierPanel.createStyledButton("Otwórz plik", "#27AE60");
        Button closeButton = cashierPanel.createStyledButton("Zamknij", "#7F8C8D");

        openButton.setOnAction(e -> {
            openReportFile(report.getSciezkaPliku());
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

            // Generowanie raportu
            //List<SalesRecord> salesData = getSalesDataForReport(periodType, date);
            //String reportPath = generateSalesReport(periodType, date, null);

            // Zapisanie informacji o raporcie
            //saveReportInfo(periodType, date, reportPath);

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
        TableColumn<Product, Integer> quantityCol = new TableColumn<>("Dostępna ilość");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        table.getColumns().addAll(idCol, nameCol, categoryCol, priceCol, quantityCol);

        ProductRepository productRepo = new ProductRepository();
        ObservableList<Product> productList = FXCollections.observableArrayList(productRepo.pobierzWszystkieProdukty());
        table.setItems(productList);
        productRepo.close();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isBlank()) {
                table.setItems(productList);
            } else {
                ObservableList<Product> filtered = FXCollections.observableArrayList();
                for (Product p : productList) {
                    if (p.getName().toLowerCase().contains(newVal.toLowerCase()) ||
                            p.getCategory().toLowerCase().contains(newVal.toLowerCase())) {
                        filtered.add(p);
                    }
                }
                table.setItems(filtered);
            }
        });

        return table;
    }

    private TableView<TransactionItem> createCartTable() {
        TableView<TransactionItem> table = new TableView<>();
        table.setMinHeight(300);

        TableColumn<TransactionItem, String> nameCol = new TableColumn<>("Nazwa");
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduct().getName()));

        TableColumn<TransactionItem, Integer> quantityCol = new TableColumn<>("Ilość");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantityCol.setOnEditCommit(event -> {
            TransactionItem item = event.getRowValue();
            int newValue = event.getNewValue();
            if (newValue > 0 && newValue <= item.getProduct().getQuantity()) {
                item.setQuantity(newValue);
                updateTotalPrice(table.getItems(), null);
            } else {
                table.refresh();
                showNotification("Błąd", "Nieprawidłowa ilość.");
            }
        });

        TableColumn<TransactionItem, Double> priceCol = new TableColumn<>("Cena jedn.");
        priceCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getProduct().getPrice()).asObject());

        TableColumn<TransactionItem, Double> totalCol = new TableColumn<>("Suma");
        totalCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(
                        cellData.getValue().getProduct().getPrice() * cellData.getValue().getQuantity()
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
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        if (totalPriceLabel != null) {
            totalPriceLabel.setText(String.format("%.2f zł", total));
        }
    }

    private void saveTransaction(ObservableList<TransactionItem> items, Stage dialog) {
        try {
            UserRepository userRepo = new UserRepository();
            Employee currentEmployee = userRepo.getCurrentEmployee();
            if (currentEmployee == null) {
                showNotification("Błąd", "Nie jesteś zalogowany.");
                userRepo.close();
                return;
            }

            Transaction transaction = new Transaction();
            transaction.setPracownik(currentEmployee);
            transaction.setData(new Date());

            ProductRepository productRepo = new ProductRepository();
            for (TransactionItem item : items) {
                Product product = item.getProduct();
                int newQuantity = product.getQuantity() - item.getQuantity();
                productRepo.aktualizujIloscProduktu(product.getId(), newQuantity);
            }

            TransactionRepository transactionRepo = new TransactionRepository();
            transactionRepo.dodajTransakcje(transaction);
            transactionRepo.close();

            userRepo.close();
            productRepo.close();
            showNotification("Sukces", "Transakcja została zapisana.");
            dialog.close();
        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Błąd", "Nie można zapisać transakcji: " + e.getMessage());
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
        Button confirmButton = cashierPanel.createStyledButton("Potwierdź zamknięcie zmiany", "#E67E22");
        confirmButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Zamknięcie zmiany");
            alert.setHeaderText("Zmiana została pomyślnie zamknięta");
            alert.setContentText("Dziękujemy za pracę w tej zmianie!");
            alert.showAndWait();
        });
        layout.getChildren().add(confirmButton);
        cashierPanel.setCenterPane(layout);
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

    private void setupDialog(Stage dialog, Pane root) {
        Scene scene = new Scene(root);
        dialog.setScene(scene);
        animateDialog(dialog, root);
        dialog.showAndWait();
    }

    private ComboBox<String> createStyledComboBox(String... items) {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(items);
        combo.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 8px;");
        combo.getSelectionModel().selectFirst();
        return combo;
    }

    private DatePicker createStyledDatePicker() {
        DatePicker dp = new DatePicker();
        dp.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 8px;");
        dp.getEditor().setStyle("-fx-background-color: #E0E0E0;");
        return dp;
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 8px;");
        return field;
    }

    private TextArea createStyledTextArea(String prompt) {
        TextArea area = new TextArea();
        area.setPromptText(prompt);
        area.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 8px;");
        area.setWrapText(true);
        return area;
    }

    private void showNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void logout() {
        UserRepository.resetCurrentEmployee();
        Stage primaryStage = cashierPanel.getPrimaryStage();
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

        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getTotal() { return product.getPrice() * quantity; }
    }
}

