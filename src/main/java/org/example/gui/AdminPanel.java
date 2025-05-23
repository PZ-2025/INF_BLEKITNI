/*
 * Classname: AdminPanel
 * Version information: 1.0
 * Date: 2025-04-06
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Klasa reprezentująca główny panel administratora.
 * Tworzy GUI, które zawiera panel nawigacyjny oraz różne widoki zarządzania.
 */
public class AdminPanel {
    private BorderPane root;
    private Stage primaryStage;
    private AdminPanelController controller;
    private Image logoImage;
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public AdminPanel(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(450);
        this.controller = new AdminPanelController(this);

        // Asynchroniczne ładowanie logo
        loadLogoAsync();

        primaryStage.setTitle("Panel administratora");

        // Główna struktura układu
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: lightblue;");

        // Lewy panel nawigacyjny - tworzony asynchronicznie
        createMenuAsync();

        // Ustawienie sceny
        Scene scene = new Scene(root, 700, 450);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Domyślnie wyświetl widok użytkowników - asynchronicznie
        Platform.runLater(() -> controller.showUserManagement());
    }

    private void loadLogoAsync() {
        Task<Image> task = new Task<>() {
            @Override
            protected Image call() throws Exception {
                return new Image(Objects.requireNonNull(
                        getClass().getResourceAsStream("/logo.png")
                ), 100, 100, true, true);
            }
        };

        task.setOnSucceeded(e -> {
            logoImage = task.getValue();
            primaryStage.getIcons().add(logoImage);
        });

        executor.execute(task);
    }

    private void createMenuAsync() {
        Task<VBox> task = new Task<>() {
            @Override
            protected VBox call() throws Exception {
                return createMenu();
            }
        };

        task.setOnSucceeded(e -> {
            VBox menu = task.getValue();
            root.setLeft(menu);

            // Animacje po załadowaniu menu
            animateFadeIn(menu, 1000);
            animateSlideDown(menu, 800);
        });

        executor.execute(task);
    }

    private VBox createMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setStyle(
                "-fx-background-color: #E0E0E0; "
                        + "-fx-border-radius: 10; "
                        + "-fx-background-radius: 10;"
        );
        menu.setAlignment(Pos.TOP_LEFT);

        // Logo w lewym górnym rogu
        Image image = new Image(
                Objects.requireNonNull(
                        getClass().getResourceAsStream("/logo.png")
                ), 100, 100, true, true
        );
        ImageView logo = new ImageView(image);
        logo.setFitWidth(100);
        logo.setFitHeight(100);
        logo.setPreserveRatio(true);

        // Przyciski
        Button usersButton = createStyledButton("Użytkownicy");
        usersButton.setOnAction(e -> controller.showUserManagement());

        Button configButton = createStyledButton("Konfiguracja");
        configButton.setOnAction(e -> controller.showConfigPanel());

        Button reportsButton = createStyledButton("Raporty");
        reportsButton.setOnAction(e -> controller.showReportsPanel());

        Button issuesButton = createStyledButton("Zgłoszenia");
        issuesButton.setOnAction(e -> controller.showIssuesPanel());

        Button logoutButton = createStyledButton("Wyloguj", "#E74C3C");
        logoutButton.setOnAction(e -> controller.logout());

        // Dodanie elementów do menu
        menu.getChildren().add(logo);
        menu.getChildren().addAll(
                usersButton,
                configButton,
                reportsButton,
                issuesButton,
                logoutButton);

        return menu;
    }

    private Button createStyledButton(String text) {
        return createStyledButton(text, "#2980B9");
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color
                + "; -fx-text-fill: white; -fx-font-weight: bold;");

        // Efekt powiększenia przy najechaniu kursorem
        button.setOnMouseEntered(e -> {
            button.setScaleX(1.1);
            button.setScaleY(1.1);
        });

        button.setOnMouseExited(e -> {
            button.setScaleX(1);
            button.setScaleY(1);
        });

        return button;
    }

    private void animateFadeIn(VBox element, int duration) {
        // Włączenie cache dla lepszej wydajności animacji
        element.setCache(true);
        element.setCacheHint(CacheHint.SPEED);

        FadeTransition fade = new FadeTransition(
                Duration.millis(duration), element
        );
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setOnFinished(e -> {
            // Wyłączenie cache po zakończeniu animacji
            element.setCache(false);
        });
        fade.play();
    }

    private void animateSlideDown(VBox element, int duration) {
        // Włączenie cache dla lepszej wydajności animacji
        element.setCache(true);
        element.setCacheHint(CacheHint.SPEED);

        TranslateTransition slide = new TranslateTransition(
                Duration.millis(duration), element
        );
        slide.setFromY(-50);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_BOTH);
        slide.setOnFinished(e -> {
            // Wyłączenie cache po zakończeniu animacji
            element.setCache(false);
        });
        slide.play();
    }

    public void setCenterPane(javafx.scene.layout.Pane pane) {
        root.setCenter(pane);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
