/*
 * Classname: ManagerPanel
 * Version information: 1.0
 * Date: 2025-04-06
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

/**
 * Panel kierownika, zawiera menu boczne oraz centralny panel widoku.
 * Pozwala nawigować po funkcjach przypisanych do roli kierownika.
 */
public class ManagerPanel {

    private BorderPane root;
    private Stage primaryStage;
    private ManagerPanelController controller;
    private Image logoImage;

    /**
     * Konstruktor panelu kierownika.
     *
     * @param stage główne okno aplikacji
     */
    public ManagerPanel(Stage stage) {
        this.primaryStage = stage;
        this.controller = new ManagerPanelController(this);

        logoImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/logo.png")
        ));
        primaryStage.getIcons().add(logoImage);

        primaryStage.setTitle("Panel kierownika");
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(450);

        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: lightblue;");

        VBox menu = createMenu();
        root.setLeft(menu);

        controller.showTaskPanel();

        animateFadeIn(menu, 1000);
        animateSlideDown(menu, 800);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Tworzy panel boczny z przyciskami menu.
     *
     * @return VBox z elementami menu
     */
    private VBox createMenu() {
        VBox menu = new VBox(15);
        menu.setPadding(new Insets(20));
        menu.setStyle(
                "-fx-background-color: #E0E0E0; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10;"
        );
        menu.setAlignment(Pos.TOP_LEFT);

        Image image = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/logo.png"))
        );
        ImageView logo = new ImageView(image);
        logo.setFitWidth(100);
        logo.setFitHeight(100);
        logo.setPreserveRatio(true);

        VBox logoBox = new VBox(logo);
        logoBox.setAlignment(Pos.CENTER);

        Button tasksButton = createStyledButton("Zadania dla pracowników");
        Button absenceButton = createStyledButton("Wnioski o nieobecność");
        Button logoutButton = createStyledButton("Wyloguj się", "#E74C3C");

        tasksButton.setOnAction(e -> controller.showTaskPanel());
        absenceButton.setOnAction(e -> controller.showAbsencePanel());
        logoutButton.setOnAction(e -> controller.logout());

        menu.getChildren().addAll(
                logoBox,
                tasksButton,
                absenceButton,
                logoutButton
        );

        return menu;
    }

    /**
     * Tworzy przycisk ze stylowaniem domyślnym (niebieski).
     *
     * @param text tekst przycisku
     * @return przycisk z domyślnym stylem
     */
    private Button createStyledButton(String text) {
        return createStyledButton(text, "#2980B9");
    }

    /**
     * Tworzy przycisk z określonym kolorem tła.
     *
     * @param text  tekst przycisku
     * @param color kolor tła (hex)
     * @return wystylizowany przycisk
     */
    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold;"
        );

        // Efekt powiększenia po najechaniu
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(
                    Duration.millis(200), button
            );
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(
                    Duration.millis(200), button
            );
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
        });

        return button;
    }

    /**
     * Animacja płynnego pojawiania się elementu.
     */
    private void animateFadeIn(VBox element, int duration) {
        FadeTransition fade = new FadeTransition(
                Duration.millis(duration),
                element
        );
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    /**
     * Animacja przesunięcia elementu z góry w dół.
     */
    private void animateSlideDown(VBox element, int duration) {
        TranslateTransition slide = new TranslateTransition(
                Duration.millis(duration),
                element
        );
        slide.setFromY(-50);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_BOTH);
        slide.play();
    }

    /**
     * Ustawia centralny panel aplikacji.
     *
     * @param pane widok do wyświetlenia
     */
    public void setCenterPane(Pane pane) {
        root.setCenter(pane);
    }

    /**
     * Zwraca obiekt Stage przypisany do panelu kierownika.
     *
     * @return główna scena
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}