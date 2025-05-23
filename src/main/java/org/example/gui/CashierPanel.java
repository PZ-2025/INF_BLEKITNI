/*
 * Classname: CashierPanel
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.gui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;
import java.util.Optional;

/**
 * Klasa reprezentująca graficzny interfejs kasjera.
 * Zawiera przyciski nawigacyjne i animacje wejścia.
 */
public class CashierPanel {

    private BorderPane root;
    private Stage primaryStage;
    private CashierPanelController controller;
    private Image logoImage;

    public CashierPanel(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(450);
        this.controller = new CashierPanelController(this);

        logoImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/logo.png")
        ));
        primaryStage.getIcons().add(logoImage);
        primaryStage.setTitle("Panel kasjera");

        // Główna struktura układu
        root = new BorderPane();
        root.setPadding(new Insets(10));

        // Lewy panel nawigacyjny
        VBox menu = createMenu();
        root.setLeft(menu);

        // Domyślnie wyświetl ekran sprzedaży
        controller.showSalesScreen();

        // Animacje pojawiania się menu
        animateFadeIn(menu, 1000);
        animateSlideDown(menu, 800);

        // Tworzenie sceny
        Scene scene = new Scene(root, 700, 450);
        primaryStage.setScene(scene);

        // Obsługa próby zamknięcia okna - używamy metod z kontrolera
        primaryStage.setOnCloseRequest(event -> {
            // Sprawdź, czy raport został wygenerowany w bieżącej sesji lub wcześniej dzisiaj
            boolean reportGenerated = controller.isReportGeneratedInCurrentSession() || controller.isDailyReportGeneratedToday();

            if (!reportGenerated) {
                event.consume(); // Zapobiega zamknięciu okna

                // Wyświetl alert z pytaniem
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Uwaga");
                alert.setHeaderText("Nie wygenerowano raportu dziennego");
                alert.setContentText("Czy na pewno chcesz zamknąć aplikację bez wygenerowania raportu dziennego?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Użytkownik potwierdził zamknięcie - usuń handler i zamknij
                    primaryStage.setOnCloseRequest(null);
                    Platform.exit();
                }
            }
        });

        primaryStage.show();
    }

    private VBox createMenu() {
        VBox menu = new VBox(15);
        menu.setPadding(new Insets(20));
        menu.setStyle(
                "-fx-background-color: #E0E0E0; "
                        + "-fx-border-radius: 10; "
                        + "-fx-background-radius: 10;"
        );
        menu.setAlignment(Pos.TOP_LEFT);

        // Logo w lewym górnym rogu
        Image image = new Image(getClass().getResourceAsStream("/logo.png"));
        ImageView logo = new ImageView(image);
        logo.setFitWidth(100);
        logo.setFitHeight(100);
        logo.setPreserveRatio(true);

        // Przycisk ekranu sprzedaży
        Button salesButton = createStyledButton("Ekran sprzedaży");
        salesButton.setOnAction(e -> controller.showSalesScreen());

        // Przycisk raportów sprzedaży
        Button reportsButton = createStyledButton("Raporty sprzedaży");
        reportsButton.setOnAction(e -> controller.showSalesReportsPanel());

        // Przycisk zamknięcia zmiany
        Button closeShiftButton = createStyledButton("Zamknięcie zmiany");
        closeShiftButton.setOnAction(e -> controller.showCloseShiftPanel());

        // Przycisk zgłoszenia awarii
        Button issueReportButton = createStyledButton("Zgłoszenie awarii");
        issueReportButton.setOnAction(e -> controller.showIssueReportPanel());

        // Przycisk wniosku o nieobecność
        Button absenceButton = createStyledButton("Złóż wniosek o nieobecność");
        absenceButton.setOnAction(e -> controller.showAbsenceRequestForm());

        // Przycisk wylogowania (czerwony)
        Button logoutButton = createStyledButton("Wyloguj się", "#E74C3C");
        logoutButton.setOnAction(e -> {
            // Używamy flagi z kontrolera zamiast lokalnej flagi
            controller.logout();
        });

        menu.getChildren().add(logo);
        menu.getChildren().addAll(
                salesButton,
                reportsButton,
                closeShiftButton,
                issueReportButton,
                absenceButton,
                logoutButton
        );

        return menu;
    }

    /**
     * Tworzy stylizowany przycisk z domyślnym niebieskim kolorem.
     *
     * @param text etykieta przycisku
     * @return gotowy przycisk
     */
    Button createStyledButton(String text) {
        return createStyledButton(text, "#2980B9");
    }

    /**
     * Tworzy stylizowany przycisk z podanym kolorem.
     *
     * @param text  etykieta przycisku
     * @param color kolor tła przycisku
     * @return gotowy przycisk
     */
    Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: " + color + "; "
                        + "-fx-text-fill: white; "
                        + "-fx-font-weight: bold;"
        );

        // Animacja powiększenia po najechaniu
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(
                    Duration.millis(200),
                    button
            );
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(
                    Duration.millis(200),
                    button
            );
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
        });

        return button;
    }

    /**
     * Animuje płynne pojawienie się elementu.
     *
     * @param element  element do animacji
     * @param duration czas trwania w milisekundach
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
     * Animuje przesunięcie elementu z góry w dół.
     *
     * @param element  element do animacji
     * @param duration czas trwania w milisekundach
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
     * Ustawia środkowy panel aplikacji.
     *
     * @param pane element do wyświetlenia w centrum
     */
    public void setCenterPane(Pane pane) {
        root.setCenter(pane);
    }

    /**
     * Zwraca główny Stage przypisany do aplikacji.
     *
     * @return główna scena aplikacji
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public CashierPanelController getController() {
        return this.controller;
    }

    /**
     * Resetuje handler zamknięcia okna.
     */
    public void resetCloseRequestHandler() {
        primaryStage.setOnCloseRequest(null);
    }
}