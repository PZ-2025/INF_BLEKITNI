/*
 * Classname: LogisticianPanel
 * Version information: 1.1
 * Date: 2025-04-11
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

/**
 * Klasa reprezentująca panel logistyka w aplikacji GUI.
 */
public class LogisticianPanel {

    private BorderPane root;
    private Stage primaryStage;
    private LogisticianPanelController controller;
    private Image logoImage;

    /**
     * Konstruktor klasy LogisticianPanel.
     *
     * @param primaryStage główna scena przypisana do panelu
     */
    public LogisticianPanel(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(450);
        this.controller = new LogisticianPanelController(this);

        logoImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/logo.png")
        ));
        primaryStage.getIcons().add(logoImage);

        primaryStage.setTitle("Panel logistyka");

        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: lightblue;");

        VBox menu = createMenu();
        root.setLeft(menu);

        controller.showInventoryReports(); // domyślny widok

        animateFadeIn(menu, 1000);
        animateSlideDown(menu, 800);

        Scene scene = new Scene(root, 700, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Tworzy menu boczne z logo i przyciskami.
     */
    private VBox createMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setAlignment(Pos.TOP_LEFT);
        menu.setStyle("-fx-background-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10;");

        // logo
        Image image = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/logo.png")
        ));
        ImageView logo = new ImageView(image);
        logo.setFitWidth(100);
        logo.setFitHeight(100);
        logo.setPreserveRatio(true);

        // przyciski
        Button inventoryButton = createStyledButton("Zarządzanie magazynem");
        inventoryButton.setOnAction(e -> controller.showInventoryManagement());

        Button ordersButton = createStyledButton("Zamówienia");
        ordersButton.setOnAction(e -> controller.showOrdersPanel());

        Button reportsButton = createStyledButton("Raporty magazynowe");
        reportsButton.setOnAction(e -> controller.showInventoryReports());

        Button absenceButton = createStyledButton("Złóż wniosek o nieobecność");
        absenceButton.setOnAction(e -> controller.showAbsenceRequestForm());

        Button logoutButton = createStyledButton("Wyloguj", "#E74C3C");
        logoutButton.setOnAction(e -> controller.logout());

        menu.getChildren().addAll(logo, inventoryButton, ordersButton, reportsButton, absenceButton, logoutButton);

        return menu;
    }

    /**
     * Stylizuje przyciski jak w panelu admina.
     */
    private Button createStyledButton(String text) {
        return createStyledButton(text, "#2980B9");
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color
                + "; -fx-text-fill: white; -fx-font-weight: bold;");

        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
        });

        return button;
    }

    private void animateFadeIn(VBox element, int duration) {
        FadeTransition fade = new FadeTransition(Duration.millis(duration), element);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void animateSlideDown(VBox element, int duration) {
        TranslateTransition slide = new TranslateTransition(Duration.millis(duration), element);
        slide.setFromY(-50);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_BOTH);
        slide.play();
    }

    public void setCenterPane(javafx.scene.layout.Pane pane) {
        root.setCenter(pane);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}

