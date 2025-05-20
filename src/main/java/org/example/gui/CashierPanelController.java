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
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import org.example.database.*;
import org.example.sys.*;

import java.time.LocalDate;
import java.util.Date;

public class CashierPanelController {

    private final CashierPanel cashierPanel;

    public CashierPanelController(CashierPanel cashierPanel) {
        this.cashierPanel = cashierPanel;
    }

    public void showSalesScreen() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Button newTransactionButton = cashierPanel.createStyledButton("Rozpocznij nową transakcję");
        newTransactionButton.setOnAction(e -> startNewTransaction());
        layout.getChildren().add(newTransactionButton);
        cashierPanel.setCenterPane(layout);
    }

    public void showSalesReportsPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label label = new Label("Raporty sprzedaży");
        label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Przycisk przykładowy do generowania raportu
        Button generateButton = cashierPanel.createStyledButton("Generuj raport sprzedaży");
        generateButton.setOnAction(e -> showNotification("Raport", "Raport sprzedaży został wygenerowany."));

        layout.getChildren().addAll(label, generateButton);
        cashierPanel.setCenterPane(layout);
    }

    public void showCloseShiftPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label label = new Label("Zamknięcie zmiany");
        label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button confirmButton = cashierPanel.createStyledButton("Zamknij zmianę", "#E67E22");
        confirmButton.setOnAction(e -> {
            showNotification("Zmiana zamknięta", "Zmiana została pomyślnie zamknięta.");
            cashierPanel.setReportGenerated(true); // flaga do zamykania aplikacji
        });

        layout.getChildren().addAll(label, confirmButton);
        cashierPanel.setCenterPane(layout);
    }

    public void showIssueReportPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label("Zgłoś problem techniczny:");
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Awaria sprzętu", "Błąd oprogramowania", "Inne");
        typeBox.setPromptText("Wybierz typ problemu");

        TextArea description = new TextArea();
        description.setPromptText("Opisz problem...");
        description.setWrapText(true);
        description.setPrefHeight(100);

        Button sendButton = cashierPanel.createStyledButton("Wyślij zgłoszenie", "#27AE60");
        sendButton.setOnAction(e -> {
            if (typeBox.getValue() == null || description.getText().trim().isEmpty()) {
                showNotification("Błąd", "Uzupełnij wszystkie pola.");
                return;
            }

            UserRepository userRepo = new UserRepository();
            TechnicalIssueRepository issueRepo = new TechnicalIssueRepository();
            try {
                Employee emp = userRepo.getCurrentEmployee();
                if (emp == null) {
                    showNotification("Błąd", "Brak zalogowanego pracownika.");
                    return;
                }

                TechnicalIssue issue = new TechnicalIssue(
                        typeBox.getValue(),
                        description.getText(),
                        LocalDate.now(),
                        emp,
                        "Nowe"
                );
                issueRepo.dodajZgloszenie(issue);
                showNotification("Sukces", "Zgłoszenie wysłane.");
            } finally {
                userRepo.close();
                issueRepo.close();
            }
        });

        layout.getChildren().addAll(label, typeBox, description, sendButton);
        cashierPanel.setCenterPane(layout);
    }

    public void showAbsenceRequestForm() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label("Wniosek o nieobecność:");
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Urlop wypoczynkowy", "Urlop na żądanie", "Zwolnienie lekarskie", "Inne");
        typeBox.setPromptText("Typ wniosku");

        DatePicker startPicker = new DatePicker();
        DatePicker endPicker = new DatePicker();
        TextArea reason = new TextArea();
        reason.setPromptText("Podaj powód...");
        reason.setWrapText(true);
        reason.setPrefHeight(80);

        Button submit = cashierPanel.createStyledButton("Wyślij wniosek", "#27AE60");
        submit.setOnAction(e -> {
            if (typeBox.getValue() == null || startPicker.getValue() == null || endPicker.getValue() == null || reason.getText().trim().isEmpty()) {
                showNotification("Błąd", "Uzupełnij wszystkie pola.");
                return;
            }
            if (startPicker.getValue().isAfter(endPicker.getValue())) {
                showNotification("Błąd", "Data rozpoczęcia nie może być późniejsza niż zakończenia.");
                return;
            }

            AbsenceRequestRepository repo = new AbsenceRequestRepository();
            UserRepository userRepo = new UserRepository();
            try {
                Employee emp = userRepo.getCurrentEmployee();
                if (emp == null) {
                    showNotification("Błąd", "Brak zalogowanego pracownika.");
                    return;
                }

                AbsenceRequest request = new AbsenceRequest(
                        typeBox.getValue(),
                        java.sql.Date.valueOf(startPicker.getValue()),
                        java.sql.Date.valueOf(endPicker.getValue()),
                        reason.getText(),
                        emp,
                        AbsenceRequest.StatusWniosku.OCZEKUJE // Teraz używamy enuma zamiast String
                );
                repo.dodajWniosek(request);
                showNotification("Sukces", "Wniosek został zapisany.");
            } finally {
                repo.close();
                userRepo.close();
            }
        });

        layout.getChildren().addAll(label, typeBox, startPicker, endPicker, reason, submit);
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
                int availableQuantity = getDostepnaIlosc(selectedProduct);

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
        ObservableList<Product> productList = FXCollections.observableArrayList(productRepo.pobierzWszystkieProdukty());
        productRepo.close();

        table.setItems(productList);

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

    private int getDostepnaIlosc(Product produkt) {
        WarehouseRepository warehouseRepo = new WarehouseRepository();
        int ilosc = 0;
        try {
            Warehouse stan = warehouseRepo.znajdzStanPoIdProduktu(produkt.getId());
            if (stan != null) {
                ilosc = stan.getIlosc();
            }
        } finally {
            warehouseRepo.close();
        }
        return ilosc;
    }

    private TableView<TransactionItem> createCartTable() {
        TableView<TransactionItem> table = new TableView<>();
        table.setMinHeight(300);

        TableColumn<TransactionItem, String> nameCol = new TableColumn<>("Nazwa");
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduct().getName())
        );

        TableColumn<TransactionItem, Integer> quantityCol = new TableColumn<>("Ilość");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantityCol.setOnEditCommit(event -> {
            TransactionItem item = event.getRowValue();
            int newValue = event.getNewValue();
            int maxQuantity = getDostepnaIlosc(item.getProduct());
            if (newValue > 0 && newValue <= maxQuantity) {
                item.setQuantity(newValue);
                updateTotalPrice(table.getItems(), null);
            } else {
                table.refresh();
                showNotification("Błąd",
                        "Nieprawidłowa ilość. Maksymalna dostępna ilość: " + maxQuantity);
            }
        });

        TableColumn<TransactionItem, Double> priceCol = new TableColumn<>("Cena jedn.");
        priceCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(
                        cellData.getValue().getProduct().getPrice().doubleValue()
                ).asObject()
        );

        TableColumn<TransactionItem, Double> totalCol = new TableColumn<>("Suma");
        totalCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(
                        cellData.getValue().getProduct().getPrice().doubleValue() *
                                cellData.getValue().getQuantity()
                ).asObject()
        );

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
            UserRepository userRepo = new UserRepository();
            Employee currentEmployee = userRepo.getCurrentEmployee();
            if (currentEmployee == null) {
                showNotification("Błąd", "Nie jesteś zalogowany. Zaloguj się ponownie.");
                userRepo.close();
                return;
            }

            Transaction transaction = new Transaction();
            transaction.setPracownik(currentEmployee);
            transaction.setData(new Date());

            TransactionRepository transactionRepo = new TransactionRepository();
            transactionRepo.dodajTransakcje(transaction);
            transactionRepo.close();

            userRepo.close();
            showNotification("Sukces", "Transakcja została zapisana pomyślnie.");
            dialog.close();

        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Błąd", "Wystąpił błąd podczas zapisywania transakcji: " + e.getMessage());
        }
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

    public void logout() {
        UserRepository.resetCurrentEmployee();
        Stage stage = cashierPanel.getPrimaryStage();
        stage.close();
        HelloApplication.showLoginScreen(stage); // zakładamy istnienie tej metody
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
    }
}
