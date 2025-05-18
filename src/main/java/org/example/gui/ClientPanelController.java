/*
 * Classname: ClientPanelController
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.ReadOnlyStringWrapper;

public class ClientPanelController {
    private final ClientPanel clientPanel;

    public ClientPanelController(ClientPanel clientPanel) {
        this.clientPanel = clientPanel;
    }

    public void showClientPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");
        layout.setAlignment(Pos.TOP_CENTER);

        // Przycisk filtruj (tu tylko jako placeholder)
        Button filterButton = new Button("Filtruj");

        // Tabela zakupów
        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(300);

        TableColumn<ObservableList<String>, String> nameCol = new TableColumn<>("Nazwa produktu");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get(0)));

        TableColumn<ObservableList<String>, String> priceCol = new TableColumn<>("Cena produktu");
        priceCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get(1)));

        TableColumn<ObservableList<String>, String> dateCol = new TableColumn<>("Data zakupu");
        dateCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get(2)));

        table.getColumns().addAll(nameCol, priceCol, dateCol);

        // Dane przykładowe
        ObservableList<ObservableList<String>> purchases = FXCollections.observableArrayList();

        purchases.add(FXCollections.observableArrayList("Chleb", "3.50 zł", "2024-04-05"));
        purchases.add(FXCollections.observableArrayList("Mleko", "2.80 zł", "2024-04-10"));
        purchases.add(FXCollections.observableArrayList("Jabłka", "4.20 zł", "2024-04-11"));

        table.setItems(purchases);

        // Przycisk wylogowania
        Button logoutButton = new Button("Wyloguj się");
        logoutButton.setOnAction(e -> logout());

        layout.getChildren().addAll(filterButton, table, logoutButton);

        Scene scene = new Scene(layout, 600, 500);
        clientPanel.getPrimaryStage().setScene(scene);
    }

    private void logout() {
        clientPanel.getPrimaryStage().close();
        Stage loginStage = new Stage();
        try {
            new HelloApplication().start(loginStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
