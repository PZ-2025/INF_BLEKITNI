/*
 * Classname: EmployeePanel
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Objects;

public class EmployeePanel {

    private BorderPane root;
    private Stage primaryStage;
    private EmployeePanelController controller;
    private Image logoImage;

    public EmployeePanel(Stage stage) {
        this.primaryStage = stage;
        this.controller = new EmployeePanelController(this);

        logoImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/logo.png")
        ));
        primaryStage.getIcons().add(logoImage);

        primaryStage.setTitle("Panel pracownika");
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(450);

        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: lightblue;");

        controller.showEmployeePanel();

        Scene scene = new Scene(root, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setCenterPane(javafx.scene.layout.Pane pane) {
        root.setCenter(pane);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
