/*
 * Classname: LogisticianPanelController
 * Version information: 1.1
 * Date: 2025-04-21
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.ProductRepository;
import org.example.database.WarehouseRepository;
import org.example.pdflib.ConfigManager;
import pdf.WarehouseRaport;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Kontroler obsługujący logikę interfejsu użytkownika
 * dla panelu logistyka.
 */
public class LogisticianPanelController {

    private final LogisticianPanel logisticianPanel;
    private final Stage primaryStage;
    private static final Logger logger = LogManager.getLogger(LogisticianPanelController.class);
    private final ProductRepository productRepository = new ProductRepository();
    private final WarehouseRepository warehouseRepository = new WarehouseRepository();

    /**
     * Konstruktor przypisujący panel logistyka.
     *
     * @param logisticianPanel obiekt klasy LogisticianPanel
     */
    public LogisticianPanelController(LogisticianPanel logisticianPanel) {
        this.logisticianPanel = logisticianPanel;
        this.primaryStage = logisticianPanel.getPrimaryStage();
    }

    /**
     * Wyświetla ekran zarządzania magazynem (produkty).
     */
    public void showInventoryManagement() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Zarządzanie magazynem");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<?> tableView = new TableView<>();
        tableView.setMinHeight(200);

        Button addProductButton = new Button("Dodaj produkt");
        Button filterButton = new Button("Filtruj");
        filterButton.setOnAction(e -> showFilterProductDialog());

        Button reportsButton = new Button("Raporty");

        layout.getChildren().addAll(titleLabel, tableView, addProductButton, filterButton, reportsButton);
        logisticianPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla panel zarządzania zamówieniami.
     */
    public void showOrdersPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Zamówienia");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<Order> tableView = new TableView<>();
        tableView.setMinHeight(200);

        TableColumn<Order, Integer> idCol = new TableColumn<>("Id");
        TableColumn<Order, Integer> productIdCol = new TableColumn<>("Id_produktu");
        TableColumn<Order, Integer> employeeIdCol = new TableColumn<>("Id_pracownika");
        TableColumn<Order, Integer> qtyCol = new TableColumn<>("Ilosc");
        TableColumn<Order, Double> priceCol = new TableColumn<>("Cena");
        TableColumn<Order, String> dateCol = new TableColumn<>("Data");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productIdCol.setCellValueFactory(new PropertyValueFactory<>("productId"));
        employeeIdCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        tableView.getColumns().addAll(idCol, productIdCol, employeeIdCol, qtyCol, priceCol, dateCol);
        tableView.setItems(getSampleOrders());

        Button addOrderButton = new Button("Dodaj zamówienie");
        addOrderButton.setOnAction(e -> showAddOrderForm());

        Button filterButton = new Button("Filtruj");
        filterButton.setOnAction(e -> showFilterOrderDialog(tableView));

        layout.getChildren().addAll(titleLabel, tableView, addOrderButton, filterButton);
        logisticianPanel.setCenterPane(layout);
    }

    private void showFilterOrderDialog(TableView<Order> tableView) {
        Stage stage = new Stage();
        stage.setTitle("Filtrowanie zamówień");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label idLabel = new Label("Id zamówienia:");
        TextField idField = new TextField();

        Label productIdLabel = new Label("Id produktu:");
        TextField productIdField = new TextField();

        Button filterButton = new Button("Filtruj");
        filterButton.setOnAction(e -> {
            // Logika filtrowania zamówień
            stage.close();
        });

        grid.add(idLabel, 0, 0); grid.add(idField, 1, 0);
        grid.add(productIdLabel, 0, 1); grid.add(productIdField, 1, 1);
        grid.add(filterButton, 1, 2);

        stage.setScene(new Scene(grid, 300, 200));
        stage.show();
    }

    /**
     * Wyświetla ekran raportów magazynowych.
     */
    public void showInventoryReports() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Raporty magazynowe");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Button generateButton = new Button("Generuj raport");
        generateButton.setOnAction(e -> showReportDialog());

        layout.getChildren().addAll(titleLabel, generateButton);
        logisticianPanel.setCenterPane(layout);
    }

    /** Okno konfiguracji raportu magazynowego (kategorie + próg stanu) */
    private void showReportDialog() {
        Stage stage = new Stage();
        stage.setTitle("Generuj raport magazynowy");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        Label categoriesLabel = new Label("Wybierz kategorie:");
        ListView<String> categoriesList = new ListView<>();
        try {
            categoriesList.getItems().addAll(productRepository.getCategories());
        } catch (Exception ex) {
            logger.error("Błąd pobierania kategorii", ex);
        }
        categoriesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Label thresholdLabel = new Label("Próg niskiego stanu magazynowego:");
        Spinner<Integer> thresholdSpinner = new Spinner<>(1, 100, 5);

        // Informacja o docelowej ścieżce (tylko do odczytu)
        Label pathInfoLabel = new Label("Plik zostanie zapisany w katalogu:");
        TextField pathDisplay = new TextField();
        pathDisplay.setEditable(false);
        pathDisplay.setFocusTraversable(false);
        pathDisplay.setPrefWidth(400);
        pathDisplay.setText(ConfigManager.getReportPath());

        Button generate = new Button("Generuj");
        generate.setOnAction(e ->
                handleGenerateButton(
                        new ArrayList<>(categoriesList.getSelectionModel().getSelectedItems()),
                        thresholdSpinner.getValue(),
                        stage
                )
        );

        grid.add(categoriesLabel, 0, 0);
        grid.add(categoriesList, 1, 0);
        grid.add(thresholdLabel, 0, 1);
        grid.add(thresholdSpinner, 1, 1);
        grid.add(pathInfoLabel, 0, 2);
        grid.add(pathDisplay, 1, 2);
        grid.add(generate, 1, 3);

        stage.setScene(new Scene(grid, 600, 350));
        stage.show();
    }

    private void handleBrowseButton(Stage stage, TextField outputPath) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            outputPath.setText(file.getAbsolutePath());
        }
    }

    private void handleGenerateButton(List<String> selectedCategories,
                                      int lowStockThreshold,
                                      Stage stage) {

        String basePath = ConfigManager.getReportPath();
        if (basePath == null || basePath.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Brak ścieżki",
                    "Ustaw domyślną ścieżkę zapisu raportów w ustawieniach administratora.");
            return;
        }

        // Generujemy unikalną nazwę pliku
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File targetFile = new File(basePath, "warehouse-report-" + timestamp + ".pdf");

        try {
            // Pobierz produkty z repozytorium
            List<org.example.sys.Product> products = productRepository.getAllProducts();

            // Pobierz stany magazynowe
            Map<Integer, Integer> qtyById = warehouseRepository
                    .getAllStates()
                    .stream()
                    .collect(Collectors.toMap(
                            org.example.sys.Warehouse::getProductId,
                            org.example.sys.Warehouse::getQuantity
                    ));

            // Filtruj produkty według wybranych kategorii
            List<org.example.sys.Product> filteredProducts = products.stream()
                    .filter(p ->
                            selectedCategories.isEmpty() ||
                                    selectedCategories.contains(p.getCategory()))
                    .collect(Collectors.toList());

            WarehouseRaport.ProductDataExtractor<org.example.sys.Product> extractor =
                    new WarehouseRaport.ProductDataExtractor<>() {
                        public String getName(org.example.sys.Product p)     { return p.getName(); }
                        public String getCategory(org.example.sys.Product p) { return p.getCategory(); }
                        public double getPrice(org.example.sys.Product p)    { return p.getPrice().doubleValue(); }
                        public int getQuantity(org.example.sys.Product p)    { return qtyById.getOrDefault(p.getId(), 0); }
                    };

            WarehouseRaport raport = new WarehouseRaport();
            raport.setLogoPath("src/main/resources/logo.png");
            raport.setLowStockThreshold(lowStockThreshold);
            raport.generateReport(targetFile.getAbsolutePath(), filteredProducts, extractor, selectedCategories);

            showAlert(Alert.AlertType.INFORMATION, "Sukces",
                    "Raport zapisany: " + targetFile.getAbsolutePath());
            stage.close();

        } catch (Exception ex) {
            logger.error("Błąd generowania raportu", ex);
            showAlert(Alert.AlertType.ERROR, "Błąd",
                    "Generowanie raportu nie powiodło się: " + ex.getMessage());
        }
    }

    /**
     * Okno dodawania nowego zamówienia.
     */
    private void showAddOrderForm() {
        Stage stage = new Stage();
        stage.setTitle("Dodaj zamówienie");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label productLabel = new Label("Id produktu:");
        Label employeeLabel = new Label("Id pracownika:");
        Label qtyLabel = new Label("Ilość:");
        Label priceLabel = new Label("Cena:");
        Label dateLabel = new Label("Data:");

        TextField productId = new TextField();
        TextField employeeId = new TextField();
        TextField quantity = new TextField();
        TextField price = new TextField();
        DatePicker datePicker = new DatePicker();

        Button submit = new Button("Zapisz");
        submit.setOnAction(e -> {
            if (productId.getText().isEmpty() || employeeId.getText().isEmpty() ||
                    quantity.getText().isEmpty() || price.getText().isEmpty() || datePicker.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Uzupełnij wszystkie pola.").showAndWait();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Zamówienie dodane pomyślnie.").showAndWait();
                stage.close();
            }
        });

        grid.add(productLabel, 0, 0);
        grid.add(productId, 1, 0);
        grid.add(employeeLabel, 0, 1);
        grid.add(employeeId, 1, 1);
        grid.add(qtyLabel, 0, 2);
        grid.add(quantity, 1, 2);
        grid.add(priceLabel, 0, 3);
        grid.add(price, 1, 3);
        grid.add(dateLabel, 0, 4);
        grid.add(datePicker, 1, 4);
        grid.add(submit, 1, 5);

        stage.setScene(new Scene(grid, 350, 300));
        stage.show();
    }

    /**
     * Okno filtrowania zamówień.
     */
    /**
     * Wyświetla okno formularza filtrowania produktów.
     *
     * @param tableView tabela produktów do filtrowania
     */
    private void showFilterProductDialog(TableView<org.example.sys.Product> tableView) {
        Stage stage = new Stage();
        stage.setTitle("Filtrowanie produktów");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label nameLabel = new Label("Nazwa zawiera:");
        TextField nameField = new TextField();

        Label categoryLabel = new Label("Kategoria:");
        ComboBox<String> categoryComboBox = new ComboBox<>();

        // Pobieranie kategorii z bazy danych
        try {
            List<org.example.sys.Product> products = productRepository.getAllProducts();
            List<String> categories = products.stream()
                    .map(org.example.sys.Product::getCategory)
                    .distinct()
                    .collect(Collectors.toList());

            categoryComboBox.getItems().add("Wszystkie kategorie");
            categoryComboBox.getItems().addAll(categories);
            categoryComboBox.setValue("Wszystkie kategorie");
        } catch (Exception e) {
            logger.error("Błąd podczas pobierania kategorii", e);
            categoryComboBox.getItems().add("Wszystkie kategorie");
            categoryComboBox.setValue("Wszystkie kategorie");
        }

        Label minPriceLabel = new Label("Min. cena:");
        TextField minPriceField = new TextField();

        Label maxPriceLabel = new Label("Max. cena:");
        TextField maxPriceField = new TextField();

        Label minStockLabel = new Label("Min. ilość w magazynie:");
        TextField minStockField = new TextField();

        Button filterButton = new Button("Filtruj");
        filterButton.setOnAction(e -> {
            try {
                // Pobieranie wszystkich produktów
                List<org.example.sys.Product> allProducts = productRepository.getAllProducts();

                // Filtrowanie produktów
                List<org.example.sys.Product> filteredProducts = allProducts.stream()
                        .filter(product -> {
                            // Filtrowanie po nazwie
                            if (!nameField.getText().isEmpty() &&
                                    !product.getName().toLowerCase().contains(nameField.getText().toLowerCase())) {
                                return false;
                            }

                            // Filtrowanie po kategorii
                            String selectedCategory = categoryComboBox.getValue();
                            if (!"Wszystkie kategorie".equals(selectedCategory) &&
                                    !product.getCategory().equals(selectedCategory)) {
                                return false;
                            }

                            // Filtrowanie po minimalnej cenie
                            if (!minPriceField.getText().isEmpty()) {
                                try {
                                    double minPrice = Double.parseDouble(minPriceField.getText().replace(",", "."));
                                    if (product.getPrice().doubleValue() < minPrice) {   // <- .doubleValue()
                                        return false;
                                    }
                                } catch (NumberFormatException ex) {
                                    // Ignorowanie nieprawidłowego formatu
                                }
                            }

                            // Filtrowanie po maksymalnej cenie
                            if (!maxPriceField.getText().isEmpty()) {
                                try {
                                    double maxPrice = Double.parseDouble(maxPriceField.getText().replace(",", "."));
                                    if (product.getPrice().doubleValue() > maxPrice) {   // <- .doubleValue()
                                        return false;
                                    }
                                } catch (NumberFormatException ex) {
                                    // Ignorowanie nieprawidłowego formatu
                                }
                            }

                            return true;
                        })
                        .collect(Collectors.toList());

                // Aktualizacja tabeli
                tableView.setItems(FXCollections.observableArrayList(filteredProducts));
                stage.close();
            } catch (Exception ex) {
                logger.error("Błąd podczas filtrowania produktów", ex);
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się filtrować produktów: " + ex.getMessage());
            }
        });

        // Ustawienie pól w siatce
        grid.add(nameLabel, 0, 0);       grid.add(nameField, 1, 0);
        grid.add(categoryLabel, 0, 1);   grid.add(categoryComboBox, 1, 1);
        grid.add(minPriceLabel, 0, 2);   grid.add(minPriceField, 1, 2);
        grid.add(maxPriceLabel, 0, 3);   grid.add(maxPriceField, 1, 3);
        grid.add(minStockLabel, 0, 4);   grid.add(minStockField, 1, 4);
        grid.add(filterButton, 1, 5);

        Scene scene = new Scene(grid, 400, 250);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Wyświetla okno formularza filtrowania produktów.
     * Pola: Id, Nazwa, Cena, Ilość w magazynie.
     * Przycisk „Filtruj” zamyka okno bez dalszej logiki filtrowania (symulacja działania).
     */
    private void showFilterProductDialog() {
        Stage stage = new Stage();
        stage.setTitle("Filtrowanie produktów");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label idLabel = new Label("Id:");
        TextField idField = new TextField();

        Label nameLabel = new Label("Nazwa:");
        TextField nameField = new TextField();

        Label priceLabel = new Label("Cena:");
        TextField priceField = new TextField();

        Label stockLabel = new Label("Ilość w magazynie:");
        TextField stockField = new TextField();

        Button filterButton = new Button("Filtruj");
        filterButton.setOnAction(e -> {
            stage.close(); // symulacja filtrowania — tylko zamyka okno
        });

        // Ustawienie pól w siatce (etykiety po lewej stronie)
        grid.add(idLabel, 0, 0);       grid.add(idField, 1, 0);
        grid.add(nameLabel, 0, 1);     grid.add(nameField, 1, 1);
        grid.add(priceLabel, 0, 2);    grid.add(priceField, 1, 2);
        grid.add(stockLabel, 0, 3);    grid.add(stockField, 1, 3);
        grid.add(filterButton, 1, 4);

        Scene scene = new Scene(grid, 360, 250);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Wyświetla formularz zgłoszenia wniosku o nieobecność.
     */
    public void showAbsenceRequestForm() {
        Stage stage = new Stage();
        stage.setTitle("Wniosek o nieobecność");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        Label reasonLabel = new Label("Powód:");
        TextField reasonField = new TextField();

        Label fromDateLabel = new Label("Data od:");
        DatePicker fromDatePicker = new DatePicker();

        Label toDateLabel = new Label("Data do:");
        DatePicker toDatePicker = new DatePicker();

        Button submitButton = new Button("Złóż wniosek");
        submitButton.setOnAction(e -> {
            if (reasonField.getText().isEmpty() || fromDatePicker.getValue() == null || toDatePicker.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Uzupełnij wszystkie pola.").showAndWait();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Wniosek złożony pomyślnie.").showAndWait();
                stage.close();
            }
        });

        grid.add(reasonLabel, 0, 0);   grid.add(reasonField, 1, 0);
        grid.add(fromDateLabel, 0, 1); grid.add(fromDatePicker, 1, 1);
        grid.add(toDateLabel, 0, 2);   grid.add(toDatePicker, 1, 2);
        grid.add(submitButton, 1, 3);

        Scene scene = new Scene(grid, 350, 250);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Zwraca przykładową listę zamówień.
     */
    private ObservableList<Order> getSampleOrders() {
        return FXCollections.observableArrayList(
                new Order(1, 101, 201, 10, 99.99, "2025-04-01"),
                new Order(2, 102, 202, 5, 49.50, "2025-04-03"),
                new Order(3, 103, 203, 12, 135.00, "2025-04-10")
        );
    }

    /**
     * Wewnętrzna klasa reprezentująca dane zamówienia.
     */
    public static class Order {
        private final int id;
        private final int productId;
        private final int employeeId;
        private final int quantity;
        private final double price;
        private final String date;

        public Order(int id, int productId, int employeeId, int quantity, double price, String date) {
            this.id = id;
            this.productId = productId;
            this.employeeId = employeeId;
            this.quantity = quantity;
            this.price = price;
            this.date = date;
        }

        public int getId() { return id; }
        public int getProductId() { return productId; }
        public int getEmployeeId() { return employeeId; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public String getDate() { return date; }
    }

    /**
     * Wyświetla alert z podanym typem, tytułem i treścią.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Zamyka aplikację i powraca do okna logowania.
     */
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
