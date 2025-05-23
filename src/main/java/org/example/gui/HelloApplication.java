/*
 * Classname: HelloApplication
 * Version information: 1.3
 * Date: 2025-05-17
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.sys.ConfigPdf;
import org.example.sys.Login;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Główna klasa uruchamiająca aplikację GUI dla hipermarketu Stonka.
 * Zawiera logikę interfejsu logowania i przekierowywania użytkownika
 * na odpowiedni panel w zależności od roli.
 */
public class HelloApplication extends Application {

    // Flaga wskazująca, czy wystąpił błąd krytyczny
    private static final AtomicBoolean criticalErrorOccurred = new AtomicBoolean(false);
    // Komunikat błędu do wyświetlenia
    private static String errorMessage = "";

    /**
     * Punkt wejścia do aplikacji JavaFX.
     *
     * @param primaryStage główna scena aplikacji
     */
    public static void showLoginScreen(Stage primaryStage) {
        try {
            System.out.println("DEBUG: Wywołano showLoginScreen");

            // Usuwamy wszystkie handlery zdarzeń z primaryStage
            primaryStage.setOnCloseRequest(null);

            // Tworzenie nowego obiektu HelloApplication
            HelloApplication app = new HelloApplication();

            // Uruchomienie metody start z podanym Stage
            System.out.println("DEBUG: Przed wywołaniem app.start(primaryStage)");
            app.start(primaryStage);
            System.out.println("DEBUG: Po wywołaniu app.start(primaryStage)");
        } catch (Exception e) {
            System.err.println("BŁĄD w showLoginScreen: " + e.getMessage());
            e.printStackTrace();
            showAlert(
                    Alert.AlertType.ERROR,
                    "Błąd",
                    "Wystąpił błąd podczas wyświetlania ekranu logowania",
                    e.getMessage()
            );
        }
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.println("DEBUG: Rozpoczęcie metody start()");

        // Sprawdź, czy wystąpił błąd krytyczny podczas inicjalizacji bazy danych
        if (criticalErrorOccurred.get()) {
            System.out.println("DEBUG: Wykryto błąd krytyczny, wyświetlam komunikat o błędzie");
            showDatabaseErrorAndExit(primaryStage, errorMessage);
            return;
        }

        try {
            System.out.println("DEBUG: Inicjalizacja ConfigPdf");
            ConfigPdf configPdf = null;
            try {
                configPdf = new ConfigPdf();
                System.out.println("DEBUG: ConfigPdf zainicjalizowany pomyślnie");
            } catch (Exception e) {
                System.err.println("OSTRZEŻENIE: Błąd podczas inicjalizacji ConfigPdf: " + e.getMessage());
                e.printStackTrace();
                // Kontynuuj mimo błędu ConfigPdf
            }

            System.out.println("DEBUG: Tworzenie głównego kontenera VBox");
            VBox root = new VBox(20);
            root.setAlignment(Pos.CENTER);
            root.setStyle("-fx-background-color: lightblue; -fx-padding: 30;");

            System.out.println("DEBUG: Ładowanie obrazu logo");
            Image image = null;
            try {
                InputStream logoStream = getClass().getResourceAsStream("/logo.png");
                if (logoStream != null) {
                    image = new Image(logoStream);
                    System.out.println("DEBUG: Obraz logo załadowany pomyślnie");
                } else {
                    System.err.println("BŁĄD: Nie znaleziono pliku logo.png w zasobach");
                    // Tworzymy domyślny obraz zamiast pustego
                    InputStream defaultLogoStream = getClass().getResourceAsStream("/default_logo.png");
                    if (defaultLogoStream != null) {
                        image = new Image(defaultLogoStream);
                        System.out.println("DEBUG: Użyto domyślnego obrazu logo");
                    } else {
                        // Jeśli domyślny obraz też nie istnieje, tworzymy pusty obraz
                        // Tworzymy pusty obraz używając URL do przezroczystego pixela
                        image = new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=");
                        System.out.println("DEBUG: Użyto pustego obrazu logo");
                    }
                }
            } catch (Exception e) {
                System.err.println("BŁĄD podczas ładowania logo: " + e.getMessage());
                e.printStackTrace();
                // Tworzymy pusty obraz
                image = new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=");
                System.out.println("DEBUG: Użyto pustego obrazu logo po błędzie");
            }

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(150);
            imageView.setPreserveRatio(true);

            try {
                primaryStage.getIcons().add(image);
                System.out.println("DEBUG: Dodano ikonę do primaryStage");
            } catch (Exception e) {
                System.err.println("OSTRZEŻENIE: Nie można ustawić ikony aplikacji: " + e.getMessage());
                // Kontynuuj mimo błędu
            }

            System.out.println("DEBUG: Tworzenie etykiet");
            Label titleLabel = new Label("Stonka najlepszy hipermarket");
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            titleLabel.setOpacity(0);

            Label welcomeLabel = new Label("Witamy w Stonce");
            welcomeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));
            welcomeLabel.setOpacity(0);

            System.out.println("DEBUG: Tworzenie siatki formularza");
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setTranslateY(-50);

            Label loginLabel = new Label("Login");
            TextField loginField = new TextField();
            loginField.setPromptText("Tutaj podaj login");
            loginField.setStyle("-fx-background-color: #FFD966; -fx-padding: 5;");

            Label passwordLabel = new Label("Hasło");
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Tutaj podaj hasło");

            passwordField.setStyle("-fx-background-color: #FFD966; -fx-padding: 5;");

            grid.add(loginLabel, 0, 0);
            grid.add(loginField, 1, 0);
            grid.add(passwordLabel, 0, 1);
            grid.add(passwordField, 1, 1);

            System.out.println("DEBUG: Tworzenie przycisków");
            HBox topButtonBox = new HBox(15);
            topButtonBox.setAlignment(Pos.CENTER);

            Button loginButton = new Button("Zaloguj");
            styleButton(loginButton, "#2980B9");
            loginButton.setOnAction(e -> {
                System.out.println("DEBUG: Kliknięto przycisk Zaloguj");
                try {
                    Login.attemptLogin(
                            loginField.getText(),
                            passwordField.getText(),
                            root
                    );
                } catch (Exception ex) {
                    System.err.println("BŁĄD podczas logowania: " + ex.getMessage());
                    ex.printStackTrace();
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Błąd logowania",
                            "Nie można zalogować",
                            "Wystąpił błąd podczas próby logowania: " + ex.getMessage()
                    );
                }
            });

            Button resetPasswordButton = new Button("Resetowanie hasła");
            styleButton(resetPasswordButton, "#F39C12");
            resetPasswordButton.setOnAction(e -> {
                System.out.println("DEBUG: Kliknięto przycisk Resetowanie hasła");
                try {
                    showResetPasswordWindow();
                } catch (Exception ex) {
                    System.err.println("BŁĄD podczas otwierania okna resetowania hasła: " + ex.getMessage());
                    ex.printStackTrace();
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Błąd",
                            "Nie można otworzyć okna resetowania hasła",
                            ex.getMessage()
                    );
                }
            });

            topButtonBox.getChildren().addAll(loginButton, resetPasswordButton);

            HBox bottomButtonBox = new HBox(15);
            bottomButtonBox.setAlignment(Pos.CENTER);

            Button cvButton = new Button("Złóż CV");
            styleButton(cvButton, "#1F618D");
            cvButton.setOnAction(e -> {
                System.out.println("DEBUG: Kliknięto przycisk Złóż CV");
                try {
                    showCVForm();
                } catch (Exception ex) {
                    System.err.println("BŁĄD podczas otwierania formularza CV: " + ex.getMessage());
                    ex.printStackTrace();
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Błąd",
                            "Nie można otworzyć formularza CV",
                            ex.getMessage()
                    );
                }
            });

            Button exitButton = new Button("Wyjście");
            styleButton(exitButton, "#E74C3C");
            exitButton.setOnAction(e -> {
                System.out.println("DEBUG: Kliknięto przycisk Wyjście");
                exitApplication();
            });

            bottomButtonBox.getChildren().addAll(cvButton, exitButton);

            System.out.println("DEBUG: Dodawanie elementów do głównego kontenera");
            root.getChildren().addAll(
                    imageView,
                    titleLabel,
                    welcomeLabel,
                    grid,
                    topButtonBox,
                    bottomButtonBox
            );

            System.out.println("DEBUG: Konfiguracja animacji");
            animateFadeIn(titleLabel, 1000);
            animateFadeIn(welcomeLabel, 1200);
            animateSlideDown(grid, 1000);

            System.out.println("DEBUG: Tworzenie sceny");
            Scene scene = new Scene(root, 600, 500);

            System.out.println("DEBUG: Konfiguracja primaryStage");
            primaryStage.setScene(scene);
            primaryStage.setTitle("Stonka - Logowanie");
            primaryStage.setMinWidth(700);
            primaryStage.setMinHeight(450);

            System.out.println("DEBUG: Wywoływanie primaryStage.show()");
            primaryStage.show();
            System.out.println("DEBUG: primaryStage.show() wykonane");

        } catch (Exception e) {
            System.err.println("BŁĄD KRYTYCZNY w start(): " + e.getMessage());
            e.printStackTrace();
            showAlert(
                    Alert.AlertType.ERROR,
                    "Błąd",
                    "Wystąpił błąd podczas uruchamiania aplikacji",
                    e.getMessage()
            );
            Platform.exit();
        }

        System.out.println("DEBUG: Koniec metody start()");
    }

    /**
     * Wyświetla komunikat o błędzie bazy danych i zamyka aplikację.
     *
     * @param stage główna scena aplikacji
     * @param message komunikat błędu
     */
    private void showDatabaseErrorAndExit(Stage stage, String message) {
        System.out.println("DEBUG: Wyświetlanie komunikatu o błędzie bazy danych");

        VBox errorBox = new VBox(20);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setPadding(new Insets(30));
        errorBox.setStyle("-fx-background-color: #FFEBEE;");

        Label errorTitle = new Label("Błąd połączenia z bazą danych");
        errorTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        errorTitle.setStyle("-fx-text-fill: #C62828;");

        Label errorDetails = new Label("Nie można uruchomić aplikacji z powodu problemów z bazą danych:");
        errorDetails.setFont(Font.font("Arial", 14));

        TextArea errorText = new TextArea(message);
        errorText.setEditable(false);
        errorText.setWrapText(true);
        errorText.setPrefHeight(100);
        errorText.setStyle("-fx-control-inner-background: #FFEBEE; -fx-border-color: #C62828;");

        Label instructionLabel = new Label("Sprawdź czy:");
        instructionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        VBox instructionBox = new VBox(5);
        instructionBox.getChildren().addAll(
                new Label("• Serwer bazy danych jest uruchomiony"),
                new Label("• Dane dostępowe do bazy są poprawne"),
                new Label("• Baza danych istnieje i jest dostępna"),
                new Label("• Firewall nie blokuje połączenia")
        );

        Button exitButton = new Button("Zamknij aplikację");
        exitButton.setStyle("-fx-background-color: #C62828; -fx-text-fill: white;");
        exitButton.setOnAction(e -> Platform.exit());

        errorBox.getChildren().addAll(
                errorTitle,
                errorDetails,
                errorText,
                instructionLabel,
                instructionBox,
                exitButton
        );

        Scene errorScene = new Scene(errorBox, 600, 450);
        stage.setScene(errorScene);
        stage.setTitle("Stonka - Błąd krytyczny");

        System.out.println("DEBUG: Wyświetlanie okna błędu bazy danych");
        stage.show();
    }

    /**
     * Wyświetla formularz do składania CV.
     */
    private void showCVForm() {
        System.out.println("DEBUG: Tworzenie formularza CV");

        Stage cvStage = new Stage();
        cvStage.setTitle("Składanie CV");

        VBox cvLayout = new VBox(15);
        cvLayout.setPadding(new Insets(20));
        cvLayout.setAlignment(Pos.CENTER);

        // Formularz danych osobowych
        GridPane formGrid = new GridPane();
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setHgap(10);
        formGrid.setVgap(10);

        // Pola formularza
        Label nameLabel = new Label("Imię:");
        TextField nameField = new TextField();

        Label surnameLabel = new Label("Nazwisko:");
        TextField surnameField = new TextField();

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label phoneLabel = new Label("Telefon:");
        TextField phoneField = new TextField();

        Label positionLabel = new Label("Stanowisko:");
        ComboBox<String> positionCombo = new ComboBox<>();
        positionCombo.getItems().addAll(
                "Kasjer", "Sprzedawca", "Magazynier",
                "Kierownik działu", "Specjalista ds. marketingu"
        );
        positionCombo.setPromptText("Wybierz stanowisko");

        // Dodanie pól do grid
        formGrid.add(nameLabel, 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(surnameLabel, 0, 1);
        formGrid.add(surnameField, 1, 1);
        formGrid.add(emailLabel, 0, 2);
        formGrid.add(emailField, 1, 2);
        formGrid.add(phoneLabel, 0, 3);
        formGrid.add(phoneField, 1, 3);
        formGrid.add(positionLabel, 0, 4);
        formGrid.add(positionCombo, 1, 4);

        // Obsługa załączania pliku CV
        Label cvFileLabel = new Label("Załącz CV (PDF/DOCX):");
        Button attachButton = new Button("Wybierz plik");
        Label fileNameLabel = new Label("Nie wybrano pliku");

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Dokumenty", "*.pdf", "*.docx", "*.doc")
        );

        attachButton.setOnAction(e -> {
            System.out.println("DEBUG: Wybieranie pliku CV");
            try {
                File selectedFile = fileChooser.showOpenDialog(cvStage);
                if (selectedFile != null) {
                    fileNameLabel.setText(selectedFile.getName());
                    System.out.println("DEBUG: Wybrano plik: " + selectedFile.getAbsolutePath());
                } else {
                    System.out.println("DEBUG: Nie wybrano pliku");
                }
            } catch (Exception ex) {
                System.err.println("BŁĄD podczas wybierania pliku: " + ex.getMessage());
                ex.printStackTrace();
                showAlert(
                        Alert.AlertType.ERROR,
                        "Błąd",
                        "Nie można wybrać pliku",
                        ex.getMessage()
                );
            }
        });

        HBox fileBox = new HBox(10);
        fileBox.setAlignment(Pos.CENTER_LEFT);
        fileBox.getChildren().addAll(attachButton, fileNameLabel);

        // Przycisk wysłania
        Button submitButton = new Button("Wyślij aplikację");
        submitButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white;");
        submitButton.setOnAction(e -> {
            System.out.println("DEBUG: Próba wysłania formularza CV");
            try {
                if (validateCVForm(nameField, surnameField, emailField, phoneField, positionCombo, fileNameLabel)) {
                    System.out.println("DEBUG: Formularz CV zwalidowany pomyślnie");
                    showAlert(
                            Alert.AlertType.INFORMATION,
                            "Sukces",
                            "Aplikacja wysłana",
                            "Dziękujemy za przesłanie CV. Skontaktujemy się z Tobą w ciągu 7 dni."
                    );
                    cvStage.close();
                } else {
                    System.out.println("DEBUG: Walidacja formularza CV nie powiodła się");
                }
            } catch (Exception ex) {
                System.err.println("BŁĄD podczas wysyłania formularza CV: " + ex.getMessage());
                ex.printStackTrace();
                showAlert(
                        Alert.AlertType.ERROR,
                        "Błąd",
                        "Nie można wysłać formularza",
                        ex.getMessage()
                );
            }
        });

        cvLayout.getChildren().addAll(
                new Label("Formularz aplikacyjny"),
                formGrid,
                cvFileLabel,
                fileBox,
                submitButton
        );

        Scene cvScene = new Scene(cvLayout, 400, 450);
        cvStage.setScene(cvScene);

        System.out.println("DEBUG: Wyświetlanie formularza CV");
        cvStage.show();
    }

    /**
     * Waliduje formularz CV.
     */
    private boolean validateCVForm(
            TextField nameField,
            TextField surnameField,
            TextField emailField,
            TextField phoneField,
            ComboBox<String> positionCombo,
            Label fileNameLabel
    ) {
        System.out.println("DEBUG: Walidacja formularza CV");

        if (nameField.getText().isEmpty() || surnameField.getText().isEmpty()) {
            System.out.println("DEBUG: Brak imienia lub nazwiska");
            showAlert(
                    Alert.AlertType.ERROR,
                    "Błąd",
                    "Brakujące dane",
                    "Proszę podać imię i nazwisko."
            );
            return false;
        }

        if (emailField.getText().isEmpty() || !emailField.getText().contains("@")) {
            System.out.println("DEBUG: Nieprawidłowy email: " + emailField.getText());
            showAlert(
                    Alert.AlertType.ERROR,
                    "Błąd",
                    "Nieprawidłowy email",
                    "Proszę podać poprawny adres email."
            );
            return false;
        }

        if (positionCombo.getValue() == null) {
            System.out.println("DEBUG: Nie wybrano stanowiska");
            showAlert(
                    Alert.AlertType.ERROR,
                    "Błąd",
                    "Nie wybrano stanowiska",
                    "Proszę wybrać stanowisko, na które aplikujesz."
            );
            return false;
        }

        if (fileNameLabel.getText().equals("Nie wybrano pliku")) {
            System.out.println("DEBUG: Nie wybrano pliku CV");
            showAlert(
                    Alert.AlertType.ERROR,
                    "Błąd",
                    "Brak załącznika",
                    "Proszę załączyć plik CV."
            );
            return false;
        }

        return true;
    }

    /**
     * Obsługuje logikę logowania i przekierowuje na odpowiedni panel.
     *
     * @param enteredUsername login
     * @param enteredPassword hasło
     * @param root kontener GUI
     */
    private void handleLogin(String enteredUsername, String enteredPassword, VBox root) {
        System.out.println("DEBUG: Próba logowania użytkownika: " + enteredUsername);
        try {
            org.example.sys.Login.attemptLogin(enteredUsername, enteredPassword, root);
        } catch (Exception e) {
            System.err.println("BŁĄD podczas logowania: " + e.getMessage());
            e.printStackTrace();
            showAlert(
                    Alert.AlertType.ERROR,
                    "Błąd logowania",
                    "Nie można zalogować",
                    "Wystąpił błąd: " + e.getMessage()
            );
        }
    }

    /**
     * Wyświetla komunikat typu Alert.
     */
    public static void showAlert(
            Alert.AlertType type,
            String title,
            String header,
            String content
    ) {
        System.out.println("DEBUG: Wyświetlanie alertu: " + title + " - " + header);
        try {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("BŁĄD podczas wyświetlania alertu: " + e.getMessage());
            e.printStackTrace();
            // Wyświetl alert w konsoli, skoro nie można wyświetlić w GUI
            System.err.println("ALERT [" + type + "]: " + title + " - " + header + "\n" + content);
        }
    }

    /**
     * Animuje płynne pojawienie się napisu.
     */
    private void animateFadeIn(Label label, int duration) {
        System.out.println("DEBUG: Animacja fadeIn dla: " + label.getText());
        try {
            FadeTransition fade = new FadeTransition(
                    Duration.millis(duration),
                    label
            );
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        } catch (Exception e) {
            System.err.println("OSTRZEŻENIE: Błąd animacji fadeIn: " + e.getMessage());
            // Ustaw widoczność bez animacji
            label.setOpacity(1);
        }
    }

    /**
     * Animuje przesunięcie formularza w dół.
     */
    private void animateSlideDown(GridPane grid, int duration) {
        System.out.println("DEBUG: Animacja slideDown dla formularza");
        try {
            TranslateTransition slide = new TranslateTransition(
                    Duration.millis(duration),
                    grid
            );
            slide.setFromY(-50);
            slide.setToY(0);
            slide.setInterpolator(Interpolator.EASE_BOTH);
            slide.play();
        } catch (Exception e) {
            System.err.println("OSTRZEŻENIE: Błąd animacji slideDown: " + e.getMessage());
            // Ustaw pozycję bez animacji
            grid.setTranslateY(0);
        }
    }

    /**
     * Nadaje styl oraz animacje przyciskowi.
     */
    private void styleButton(Button button, String color) {
        System.out.println("DEBUG: Stylizacja przycisku: " + button.getText());
        try {
            button.setStyle(
                    "-fx-background-color: " + color + "; "
                            + "-fx-text-fill: black; "
                            + "-fx-font-weight: bold;"
            );
            button.setOnMouseEntered(e -> {
                try {
                    ScaleTransition scale = new ScaleTransition(
                            Duration.millis(200),
                            button
                    );
                    scale.setToX(1.1);
                    scale.setToY(1.1);
                    scale.play();
                } catch (Exception ex) {
                    System.err.println("OSTRZEŻENIE: Błąd animacji przycisku (mouseEntered): " + ex.getMessage());
                }
            });
            button.setOnMouseExited(e -> {
                try {
                    ScaleTransition scale = new ScaleTransition(
                            Duration.millis(200),
                            button
                    );
                    scale.setToX(1);
                    scale.setToY(1);
                    scale.play();
                } catch (Exception ex) {
                    System.err.println("OSTRZEŻENIE: Błąd animacji przycisku (mouseExited): " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("OSTRZEŻENIE: Błąd podczas stylizacji przycisku: " + e.getMessage());
            // Ustaw podstawowy styl bez animacji
            button.setStyle("-fx-background-color: " + color + ";");
        }
    }

    /**
     * Zamyka aplikację po potwierdzeniu użytkownika.
     */
    private void exitApplication() {
        System.out.println("DEBUG: Próba zamknięcia aplikacji");
        try {
            Alert confirmExit = new Alert(Alert.AlertType.CONFIRMATION);
            confirmExit.setTitle("Potwierdzenie wyjścia");
            confirmExit.setHeaderText("Czy na pewno chcesz wyjść?");
            confirmExit.setContentText("Zmiany mogą nie zostać zapisane.");

            confirmExit.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    System.out.println("DEBUG: Zamykanie aplikacji potwierdzone");
                    Platform.exit();
                } else {
                    System.out.println("DEBUG: Anulowano zamykanie aplikacji");
                }
            });
        } catch (Exception e) {
            System.err.println("BŁĄD podczas zamykania aplikacji: " + e.getMessage());
            e.printStackTrace();
            // Zamknij aplikację mimo błędu
            Platform.exit();
        }
    }

    /**
     * Wyświetla okno resetowania hasła.
     */
    private void showResetPasswordWindow() {
        System.out.println("DEBUG: Otwieranie okna resetowania hasła");

        Stage resetStage = new Stage();
        resetStage.setTitle("Resetowanie hasła");

        VBox resetLayout = new VBox(10);
        resetLayout.setPadding(new Insets(20));
        resetLayout.setAlignment(Pos.CENTER);

        Label emailLabel = new Label("Podaj swój email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        Button sendCodeButton = new Button("Wyślij kod odzyskiwania");
        sendCodeButton.setOnAction(e -> {
            System.out.println("DEBUG: Próba wysłania kodu resetowania hasła");
            String email = emailField.getText();
            if (email.isEmpty()) {
                System.out.println("DEBUG: Brak adresu email");
                HelloApplication.showAlert(
                        Alert.AlertType.ERROR,
                        "Błąd",
                        "Brak adresu email",
                        "Proszę podać adres e-mail, na który ma zostać wysłany kod."
                );
            } else {
                try {
                    System.out.println("DEBUG: Wysyłanie kodu resetowania na email: " + email);
                    Login.sendResetCode(email);
                    resetStage.close();
                    showVerificationWindow();
                } catch (Exception ex) {
                    System.err.println("BŁĄD podczas wysyłania kodu resetowania: " + ex.getMessage());
                    ex.printStackTrace();
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Błąd",
                            "Nie można wysłać kodu resetowania",
                            ex.getMessage()
                    );
                }
            }
        });

        resetLayout.getChildren().addAll(emailLabel, emailField, sendCodeButton);

        Scene resetScene = new Scene(resetLayout, 300, 200);
        resetStage.setScene(resetScene);

        System.out.println("DEBUG: Wyświetlanie okna resetowania hasła");
        resetStage.show();
    }

    // Funkcja do wyświetlania okna weryfikacyjnego
    public void showVerificationWindow() {
        System.out.println("DEBUG: Otwieranie okna weryfikacji kodu");

        Stage verificationStage = new Stage();
        verificationStage.setTitle("Weryfikacja kodu");

        VBox verificationLayout = new VBox(10);
        verificationLayout.setPadding(new Insets(20));
        verificationLayout.setAlignment(Pos.CENTER);

        Label codeLabel = new Label("Podaj kod weryfikacyjny:");
        TextField codeField = new TextField();
        codeField.setPromptText("Kod");

        Button verifyButton = new Button("Zweryfikuj");
        verifyButton.setOnAction(e -> {
            System.out.println("DEBUG: Próba weryfikacji kodu");
            String code = codeField.getText();
            if (code.length() == 6) {
                System.out.println("DEBUG: Kod weryfikacyjny poprawny: " + code);
                verificationStage.close();
                showNewPasswordWindow();
            } else {
                System.out.println("DEBUG: Niepoprawny kod weryfikacyjny: " + code);
                showAlert(Alert.AlertType.ERROR, "Błąd", "Niepoprawny kod", "Proszę podać poprawny 6-znakowy kod.");
            }
        });

        verificationLayout.getChildren().addAll(codeLabel, codeField, verifyButton);

        Scene verificationScene = new Scene(verificationLayout, 300, 200);
        verificationStage.setScene(verificationScene);

        System.out.println("DEBUG: Wyświetlanie okna weryfikacji kodu");
        verificationStage.show();
    }

    private void showNewPasswordWindow() {
        System.out.println("DEBUG: Otwieranie okna ustawiania nowego hasła");

        Stage passwordStage = new Stage();
        passwordStage.setTitle("Ustaw nowe hasło");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label newPasswordLabel = new Label("Nowe hasło:");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Wprowadź nowe hasło");

        Label repeatPasswordLabel = new Label("Powtórz hasło:");
        PasswordField repeatPasswordField = new PasswordField();
        repeatPasswordField.setPromptText("Powtórz nowe hasło");

        Button submitButton = new Button("Zapisz nowe hasło");
        submitButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white;");
        submitButton.setOnAction(e -> {
            System.out.println("DEBUG: Próba zapisania nowego hasła");
            String newPass = newPasswordField.getText();
            String repeatPass = repeatPasswordField.getText();

            if (newPass.isEmpty() || repeatPass.isEmpty()) {
                System.out.println("DEBUG: Puste pola hasła");
                showAlert(Alert.AlertType.ERROR, "Błąd", "Puste pola", "Proszę wypełnić oba pola hasła.");
            } else if (!newPass.equals(repeatPass)) {
                System.out.println("DEBUG: Hasła nie są zgodne");
                showAlert(Alert.AlertType.ERROR, "Błąd", "Hasła nie są zgodne", "Upewnij się, że oba hasła są identyczne.");
            } else if (newPass.length() < 8) {
                System.out.println("DEBUG: Hasło za krótkie: " + newPass.length() + " znaków");
                showAlert(Alert.AlertType.ERROR, "Błąd", "Hasło za krótkie", "Hasło musi mieć co najmniej 8 znaków.");
            } else {
                System.out.println("DEBUG: Walidacja hasła pomyślna, próba aktualizacji w bazie");
                // TODO: Dodać logikę do aktualizacji hasła w bazie danych
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Hasło zmienione", "Twoje hasło zostało zaktualizowane.");
                passwordStage.close();
            }
        });

        layout.getChildren().addAll(
                newPasswordLabel, newPasswordField,
                repeatPasswordLabel, repeatPasswordField,
                submitButton
        );

        Scene scene = new Scene(layout, 300, 250);
        passwordStage.setScene(scene);

        System.out.println("DEBUG: Wyświetlanie okna ustawiania nowego hasła");
        passwordStage.show();
    }

    /**
     * Główna metoda uruchamiająca aplikację.
     */
    public static void main(String[] args) {
        System.out.println("DEBUG: Rozpoczęcie metody main()");

        // Ustawienie handlera dla niezłapanych wyjątków
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("NIEZŁAPANY WYJĄTEK w wątku " + thread.getName() + ": " + throwable.getMessage());
            throwable.printStackTrace();
        });

        try {
            System.out.println("DEBUG: Próba inicjalizacji bazy danych");
            try {
                // Inicjalizacja bazy danych
                org.example.database.DatabaseInitializer.initialize();
                System.out.println("DEBUG: Baza danych zainicjalizowana pomyślnie");
            } catch (Exception e) {
                System.err.println("BŁĄD podczas inicjalizacji bazy danych: " + e.getMessage());
                e.printStackTrace();

                // Obsługa wyjątków związanych z bazą danych
                criticalErrorOccurred.set(true);

                // Zapisz komunikat błędu
                if (e instanceof SQLException) {
                    SQLException sqlEx = (SQLException) e;
                    errorMessage = formatSQLException(sqlEx);
                } else {
                    errorMessage = "Wystąpił nieoczekiwany błąd: " + e.getMessage();
                }

                System.out.println("DEBUG: Ustawiono flagę błędu krytycznego: " + criticalErrorOccurred.get());
            }

            // Dodajmy handler zamknięcia dla całej aplikacji
            Platform.setImplicitExit(true);

            System.out.println("DEBUG: Uruchamianie aplikacji JavaFX");
            launch(args);
            System.out.println("DEBUG: Aplikacja JavaFX zakończona");

        } catch (Exception e) {
            System.err.println("BŁĄD KRYTYCZNY w main(): " + e.getMessage());
            e.printStackTrace();

            // W przypadku błędu, który uniemożliwia uruchomienie JavaFX, wyświetl komunikat w konsoli
            System.err.println("\n=== BŁĄD KRYTYCZNY ===");
            System.err.println("Aplikacja nie może zostać uruchomiona z powodu błędu:");
            System.err.println(e.getMessage());
            System.err.println("======================\n");
        }

        System.out.println("DEBUG: Koniec metody main()");
    }

    /**
     * Formatuje wyjątek SQLException do czytelnej postaci.
     *
     * @param ex wyjątek SQLException
     * @return sformatowany komunikat błędu
     */
    private static String formatSQLException(SQLException ex) {
        System.out.println("DEBUG: Formatowanie wyjątku SQLException");

        StringBuilder sb = new StringBuilder();
        sb.append("Błąd SQL: ").append(ex.getMessage()).append("\n");
        sb.append("Kod błędu: ").append(ex.getErrorCode()).append("\n");
        sb.append("Stan SQL: ").append(ex.getSQLState()).append("\n\n");

        // Dodaj informacje o przyczynie błędu
        Throwable cause = ex.getCause();
        if (cause != null) {
            sb.append("Przyczyna: ").append(cause.getMessage()).append("\n");
        }

        // Dodaj sugestie rozwiązania problemu
        sb.append("\nMożliwe rozwiązania:\n");

        // Sugestie w zależności od kodu błędu
        switch (ex.getErrorCode()) {
            case 0:
                sb.append("- Sprawdź, czy serwer bazy danych jest uruchomiony\n");
                sb.append("- Sprawdź ustawienia połączenia (host, port)\n");
                break;
            case 1045:
                sb.append("- Nieprawidłowa nazwa użytkownika lub hasło\n");
                sb.append("- Sprawdź uprawnienia użytkownika bazy danych\n");
                break;
            case 1049:
                sb.append("- Baza danych nie istnieje\n");
                sb.append("- Sprawdź nazwę bazy danych\n");
                break;
            default:
                sb.append("- Sprawdź logi serwera bazy danych\n");
                sb.append("- Skontaktuj się z administratorem systemu\n");
        }

        return sb.toString();
    }
}