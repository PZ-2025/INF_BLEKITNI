/*
 * Classname: OrderPanel
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.gui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.example.sys.Employee;
import org.example.sys.Order;
import org.example.sys.Product;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Klasa reprezentująca panel do zarządzania zamówieniami.
 */
public class OrderPanel extends VBox {

    private TableView<Order> ordersTable;
    private ObservableList<Order> ordersData;

    /**
     * Konstruktor inicjalizujący interfejs i dane.
     */
    public OrderPanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        initializeUI();
        loadSampleData();
    }

    /**
     * Inicjalizuje komponenty interfejsu użytkownika.
     */
    private void initializeUI() {
        Label titleLabel = new Label("Panel Zamówień");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ordersTable = new TableView<>();

        TableColumn<Order, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Order, String> productCol = new TableColumn<>("Produkt");
        productCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduct().getName()));
        productCol.setPrefWidth(150);

        TableColumn<Order, Integer> quantityCol = new TableColumn<>("Ilość");
        quantityCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        TableColumn<Order, String> supplierCol = new TableColumn<>("Pracownik");
        supplierCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmployee().getLogin()));
        supplierCol.setPrefWidth(150);

        TableColumn<Order, String> dateCol = new TableColumn<>("Data zamówienia");
        dateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDate().toString()));

        TableColumn<Order, String> priceCol = new TableColumn<>("Cena");
        priceCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f zł", cellData.getValue().getPrice())));

        ordersTable.getColumns().addAll(idCol, productCol, quantityCol, supplierCol, dateCol, priceCol);

        Button newOrderButton = new Button("Nowe zamówienie");
        newOrderButton.setOnAction(e -> showNewOrderDialog());

        Button editOrderButton = new Button("Edytuj");
        editOrderButton.setOnAction(e -> editSelectedOrder());

        Button cancelOrderButton = new Button("Anuluj");
        cancelOrderButton.setOnAction(e -> cancelSelectedOrder());

        Button receiveOrderButton = new Button("Przyjmij dostawę");
        receiveOrderButton.setOnAction(e -> receiveSelectedOrder());

        HBox buttonsBox = new HBox(10, newOrderButton, editOrderButton, cancelOrderButton, receiveOrderButton);

        getChildren().addAll(titleLabel, ordersTable, buttonsBox);
    }

    /**
     * Załadowanie przykładowych danych do tabeli.
     */
    private void loadSampleData() {
        Product p1 = new Product("Mleko 1L", "Nabiał", new BigDecimal("2.99"));
        Product p2 = new Product("Chleb pszenny", "Pieczywo", new BigDecimal("3.49"));
        Product p3 = new Product("Jajka L", "Nabiał", new BigDecimal("5.99"));

        Employee emp = new Employee();
        emp.setLogin("admin");

        Order o1 = new Order(p1, emp, 50, p1.getPrice().multiply(BigDecimal.valueOf(50)), new Date());
        Order o2 = new Order(p2, emp, 100, p2.getPrice().multiply(BigDecimal.valueOf(100)), new Date());
        Order o3 = new Order(p3, emp, 30, p3.getPrice().multiply(BigDecimal.valueOf(30)), new Date());

        ordersData = FXCollections.observableArrayList(o1, o2, o3);
        ordersTable.setItems(ordersData);
    }


    private void showNewOrderDialog() {
        System.out.println("Otwieranie formularza nowego zamówienia...");
    }

    private void editSelectedOrder() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Edytowanie zamówienia: " + selected.getId());
        } else {
            showAlert("Brak wybranego zamówienia", "Proszę wybrać zamówienie do edycji");
        }
    }

    private void cancelSelectedOrder() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Anulowanie zamówienia: " + selected.getId());
        } else {
            showAlert("Brak wybranego zamówienia", "Proszę wybrać zamówienie do anulowania");
        }
    }

    private void receiveSelectedOrder() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Przyjmowanie dostawy dla zamówienia: " + selected.getId());
        } else {
            showAlert("Brak wybranego zamówienia", "Proszę wybrać zamówienie do przyjęcia");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
