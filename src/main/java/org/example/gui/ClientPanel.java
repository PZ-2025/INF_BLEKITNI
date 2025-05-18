/*
 * Classname: ClientPanel
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.gui;

import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class ClientPanel {
    private final Stage primaryStage;
    private final ClientPanelController controller;
    private Image logoImage;
    public ClientPanel(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.controller = new ClientPanelController(this);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(450);
        logoImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/logo.png")
        ));
        primaryStage.getIcons().add(logoImage);
        primaryStage.setTitle("Panel klienta - Panel zakupowy");

        controller.showClientPanel();
        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
