/*
 * Classname: AdminPanelController
 * Version information: 1.3
 * Date: 2025-05-18
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.database.TechnicalIssueRepository;
import org.example.database.UserRepository;
import org.example.pdflib.*;
import org.example.sys.Employee;
import org.example.sys.TechnicalIssue;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import pdf.NoDataException;
import pdf.SalesReportGenerator;
import pdf.StatsRaportGenerator;
import pdf.TaskRaportGenerator;
import org.example.pdflib.ConfigManager;
import org.example.pdflib.ReportGenerator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.io.File;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.example.sys.TechnicalIssue;
import org.example.database.AddressRepository;
import org.example.sys.Address;
import pdf.NoDataException;
import pdf.SalesReportGenerator;
import pdf.StatsRaportGenerator;
import pdf.TaskRaportGenerator;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Kontroler odpowiedzialny za obsługę logiki
 * interfejsu administratora w aplikacji GUI.
 */
public class AdminPanelController {

    private final AdminPanel adminPanel;
    private final Stage primaryStage;
    private final UserRepository userRepository;
    private TableView<Employee> tableView;
    private final TechnicalIssueRepository technicalIssueRepository;
    private TableView<TechnicalIssue> issuesTableView;

    // Executor do operacji asynchronicznych
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    // Cache widoków dla lepszej wydajności
    private VBox userManagementView;
    private VBox configPanelView;
    private VBox reportsPanelView;
    private VBox issuesPanelView;

    /**
     * Konstruktor klasy kontrolera.
     *
     * @param adminPanel główny panel administratora
     */
    public AdminPanelController(AdminPanel adminPanel) {
        this.adminPanel = adminPanel;
        this.primaryStage = adminPanel.getPrimaryStage();
        this.userRepository = new UserRepository();
        this.technicalIssueRepository = new TechnicalIssueRepository();
    }

    /**
     * Wyświetla panel zarządzania użytkownikami.
     */
    public void showUserManagement() {
        if (userManagementView == null) {
            // Pokaż wskaźnik ładowania
            showLoadingIndicator();

            // Utwórz widok asynchronicznie
            Task<VBox> task = new Task<>() {
                @Override
                protected VBox call() throws Exception {
                    return createUserManagementView();
                }
            };

            task.setOnSucceeded(e -> {
                userManagementView = task.getValue();
                adminPanel.setCenterPane(userManagementView);
                // Załaduj dane pracowników asynchronicznie
                odswiezListePracownikow();
            });

            task.setOnFailed(e -> {
                task.getException().printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się załadować panelu użytkowników");
            });

            executor.execute(task);
        } else {
            // Jeśli widok już istnieje, po prostu go pokaż
            adminPanel.setCenterPane(userManagementView);
            // Odśwież dane
            odswiezListePracownikow();
        }
    }

    /**
     * Wyświetla wskaźnik ładowania podczas operacji asynchronicznych.
     */
    private void showLoadingIndicator() {
        VBox loadingBox = new VBox(10);
        loadingBox.setAlignment(Pos.CENTER);

        ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxSize(50, 50);

        Label loadingLabel = new Label("Ładowanie...");
        loadingLabel.setStyle("-fx-font-size: 14px;");

        loadingBox.getChildren().addAll(progress, loadingLabel);
        adminPanel.setCenterPane(loadingBox);
    }

    /**
     * Tworzy widok zarządzania użytkownikami.
     *
     * @return VBox zawierający kompletny widok
     */
    private VBox createUserManagementView() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Lista użytkowników");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        configureTableView();

        // Kolumny tabeli
        TableColumn<Employee, String> nameCol = new TableColumn<>("Imię");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Employee, String> surnameCol = new TableColumn<>("Nazwisko");
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));

        TableColumn<Employee, Integer> ageCol = new TableColumn<>("Wiek");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<Employee, String> loginCol = new TableColumn<>("Login");
        loginCol.setCellValueFactory(new PropertyValueFactory<>("login"));

        TableColumn<Employee, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Employee, String> stanowiskoCol = new TableColumn<>("Stanowisko");
        stanowiskoCol.setCellValueFactory(new PropertyValueFactory<>("stanowisko"));

        TableColumn<Employee, BigDecimal> zarobkiCol = new TableColumn<>("Zarobki");
        zarobkiCol.setCellValueFactory(new PropertyValueFactory<>("zarobki"));

        tableView.getColumns().addAll(
                nameCol, surnameCol, ageCol,
                loginCol, emailCol, stanowiskoCol, zarobkiCol
        );

        odswiezListePracownikow();

        // === Przyciski ===
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button addUserButton = new Button("Dodaj użytkownika");
        Button editUserButton = new Button("Edytuj użytkownika");
        Button deleteUserButton = new Button("Usuń użytkownika");

        addUserButton.setOnAction(e -> dodajNowegoUzytkownika());
        editUserButton.setOnAction(e -> edytujWybranegoUzytkownika());
        deleteUserButton.setOnAction(e -> usunWybranegoUzytkownika());

        buttonBox.getChildren().addAll(
                addUserButton, editUserButton, deleteUserButton
        );

        layout.getChildren().addAll(titleLabel, tableView, buttonBox);
        return layout;
    }

    /**
     * Konfiguruje TableView dla lepszej wydajności.
     */
    private void configureTableView() {
        // Ustawienie wirtualizacji dla lepszej wydajności
        tableView.setFixedCellSize(25); // Stała wysokość komórek
        tableView.setCache(true);
        tableView.setCacheHint(CacheHint.SPEED);

        // Ograniczenie liczby wierszy ładowanych jednocześnie
        int rowsToShow = 20;
        tableView.setPrefHeight(rowsToShow * tableView.getFixedCellSize() + 30); // +30 na nagłówek
    }

    /**
     * Formularz edycji wybranego użytkownika.
     */
    private void edytujWybranegoUzytkownika() {
        Employee selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING,
                    "Brak wyboru",
                    "Wybierz użytkownika do edycji.");
            return;
        }

        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(20));

        Label titleLabel = new Label("Edytuj użytkownika");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField nameField = new TextField(selected.getName());
        nameField.setPromptText("Imię");

        TextField surnameField = new TextField(selected.getSurname());
        surnameField.setPromptText("Nazwisko");

        TextField loginField = new TextField(selected.getLogin());
        loginField.setPromptText("Login");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Nowe hasło (pozostaw puste, aby nie zmieniać)");

        TextField emailField = new TextField(selected.getEmail());
        emailField.setPromptText("Email");

        // Adres
        Label addressLabel = new Label("Adres:");
        AddressRepository addressRepository = new AddressRepository();
        ComboBox<Address> adresComboBox = new ComboBox<>();
        adresComboBox.getItems().addAll(addressRepository.pobierzWszystkieAdresy());
        adresComboBox.setValue(selected.getAdres()); // ustawiamy istniejący
        adresComboBox.setPromptText("Wybierz adres");

        Button dodajNowyAdresBtn = new Button("Dodaj nowy adres");
        dodajNowyAdresBtn.setOnAction(e -> otworzOknoNowegoAdresu(adresComboBox));

        ComboBox<String> stanowiskoBox = new ComboBox<>();
        stanowiskoBox.getItems().addAll("Kasjer", "Kierownik", "Admin", "Logistyk");
        stanowiskoBox.setValue(selected.getStanowisko());

        TextField ageField = new TextField(String.valueOf(selected.getAge()));
        ageField.setPromptText("Wiek");

        TextField salaryField = new TextField(selected.getZarobki().toString());
        salaryField.setPromptText("Zarobki (PLN)");

        Button saveButton = new Button("Zapisz zmiany");
        Button cancelButton = new Button("Anuluj");
        HBox buttons = new HBox(10, saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);

        formLayout.getChildren().addAll(
                titleLabel,
                nameField,
                surnameField,
                loginField,
                passwordField,
                emailField,
                addressLabel,
                new HBox(10, adresComboBox, dodajNowyAdresBtn),
                stanowiskoBox,
                ageField,
                salaryField,
                buttons
        );

        adminPanel.setCenterPane(formLayout);

        saveButton.setOnAction(e -> {
            try {
                if (nameField.getText().isEmpty()
                        || surnameField.getText().isEmpty()
                        || loginField.getText().isEmpty()
                        || emailField.getText().isEmpty()
                        || adresComboBox.getValue() == null
                        || stanowiskoBox.getValue() == null
                        || ageField.getText().isEmpty()
                        || salaryField.getText().isEmpty()) {

                    showAlert(Alert.AlertType.WARNING,
                            "Brak danych",
                            "Uzupełnij wszystkie pola (poza hasłem).");
                    return;
                }

                showLoadingIndicator();

                selected.setName(nameField.getText());
                selected.setSurname(surnameField.getText());
                selected.setLogin(loginField.getText());
                selected.setEmail(emailField.getText());
                selected.setAdres(adresComboBox.getValue());
                if (!passwordField.getText().isEmpty()) {
                    selected.setPassword(passwordField.getText());
                }
                selected.setStanowisko(stanowiskoBox.getValue());
                selected.setAge(Integer.parseInt(ageField.getText()));
                selected.setZarobki(new BigDecimal(salaryField.getText()));

                Task<Void> updateTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        userRepository.aktualizujPracownika(selected);
                        return null;
                    }
                };

                updateTask.setOnSucceeded(evt -> {
                    showAlert(Alert.AlertType.INFORMATION,
                            "Sukces",
                            "Dane użytkownika zostały zaktualizowane.");
                    showUserManagement();
                });

                updateTask.setOnFailed(evt -> {
                    updateTask.getException().printStackTrace();
                    showAlert(Alert.AlertType.ERROR,
                            "Błąd",
                            "Wystąpił błąd podczas zapisywania zmian: "
                                    + updateTask.getException().getMessage());
                    showUserManagement();
                });

                executor.execute(updateTask);

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR,
                        "Błąd",
                        "Nieprawidłowy format wieku lub zarobków!");
                showUserManagement();
            } catch (PasswordException ex) {
                showAlert(Alert.AlertType.ERROR,
                        "Nieprawidłowe hasło",
                        ex.getMessage());
                showUserManagement();
            } catch (SalaryException ex) {
                showAlert(Alert.AlertType.ERROR,
                        "Nieprawidłowe zarobki",
                        ex.getMessage());
                showUserManagement();
            }
        });

        cancelButton.setOnAction(e -> showUserManagement());
    }


    /**
     * Pobiera dane z bazy i ładuje do tabeli asynchronicznie.
     */
    private void odswiezListePracownikow() {
        Task<List<Employee>> task = new Task<>() {
            @Override
            protected List<Employee> call() throws Exception {
                return userRepository.pobierzWszystkichPracownikow();
            }
        };

        task.setOnSucceeded(e -> {
            tableView.getItems().clear();
            tableView.getItems().addAll(task.getValue());
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            Platform.runLater(() -> showAlert(
                    Alert.AlertType.ERROR,
                    "Błąd",
                    "Nie udało się pobrać listy pracowników"
            ));
        });

        executor.execute(task);
    }

    /**
     * Formularz dodawania nowego użytkownika.
     */
    private void dodajNowegoUzytkownika() {
        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(20));

        Label titleLabel = new Label("Dodaj nowego użytkownika");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField nameField = new TextField();
        nameField.setPromptText("Imię");

        TextField surnameField = new TextField();
        surnameField.setPromptText("Nazwisko");

        TextField loginField = new TextField();
        loginField.setPromptText("Login");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Hasło");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        // Adres
        Label addressLabel = new Label("Adres:");
        AddressRepository addressRepository = new AddressRepository();
        ComboBox<Address> adresComboBox = new ComboBox<>();
        adresComboBox.getItems().addAll(addressRepository.pobierzWszystkieAdresy());
        adresComboBox.setPromptText("Wybierz istniejący adres");

        Button dodajNowyAdresBtn = new Button("Dodaj nowy adres");
        dodajNowyAdresBtn.setOnAction(e -> otworzOknoNowegoAdresu(adresComboBox));

        ComboBox<String> stanowiskoBox = new ComboBox<>();
        stanowiskoBox.getItems().addAll("Kasjer", "Kierownik", "Admin", "Logistyk");
        stanowiskoBox.setPromptText("Stanowisko");

        TextField ageField = new TextField();
        ageField.setPromptText("Wiek");

        TextField salaryField = new TextField();
        salaryField.setPromptText("Zarobki (PLN)");

        Button saveButton = new Button("Zapisz");
        Button cancelButton = new Button("Anuluj");

        HBox buttons = new HBox(10, saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);

        // składamy wszystko w formLayout
        formLayout.getChildren().addAll(
                titleLabel,
                nameField,
                surnameField,
                loginField,
                passwordField,
                emailField,
                addressLabel,
                new HBox(10, adresComboBox, dodajNowyAdresBtn),
                stanowiskoBox,
                ageField,
                salaryField,
                buttons
        );

        adminPanel.setCenterPane(formLayout);

        saveButton.setOnAction(e -> {
            try {
                // sprawdzamy, czy nie ma pustych pól
                if (nameField.getText().isEmpty()
                        || surnameField.getText().isEmpty()
                        || loginField.getText().isEmpty()
                        || passwordField.getText().isEmpty()
                        || emailField.getText().isEmpty()
                        || adresComboBox.getValue() == null
                        || stanowiskoBox.getValue() == null
                        || ageField.getText().isEmpty()
                        || salaryField.getText().isEmpty()) {

                    showAlert(Alert.AlertType.WARNING, "Brak danych", "Uzupełnij wszystkie pola!");
                    return;
                }

                int wiek = Integer.parseInt(ageField.getText());
                BigDecimal zarobki = new BigDecimal(salaryField.getText());

                Employee nowy = new Employee();
                nowy.setName(nameField.getText());
                nowy.setSurname(surnameField.getText());
                nowy.setLogin(loginField.getText());
                nowy.setPassword(passwordField.getText());
                nowy.setEmail(emailField.getText());
                nowy.setAdres(adresComboBox.getValue());
                nowy.setStanowisko(stanowiskoBox.getValue());
                nowy.setAge(wiek);
                nowy.setZarobki(zarobki);

                Task<Void> addTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        userRepository.dodajPracownika(nowy);
                        return null;
                    }
                };

                addTask.setOnSucceeded(evt -> {
                    showAlert(Alert.AlertType.INFORMATION, "Sukces", "Dodano nowego użytkownika!");
                    showUserManagement();
                });

                addTask.setOnFailed(evt -> {
                    addTask.getException().printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Błąd",
                            "Nie udało się dodać użytkownika: " + addTask.getException().getMessage());
                    showUserManagement();
                });

                executor.execute(addTask);

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nieprawidłowy format wieku lub zarobków!");
                showUserManagement();
            } catch (PasswordException ex) {
                showAlert(Alert.AlertType.ERROR, "Nieprawidłowe hasło", ex.getMessage());
                showUserManagement();
            } catch (SalaryException ex) {
                showAlert(Alert.AlertType.ERROR, "Nieprawidłowe zarobki", ex.getMessage());
                showUserManagement();
            }
        });

        cancelButton.setOnAction(e -> showUserManagement());
    }


    /**
     * Usuwa zaznaczonego użytkownika asynchronicznie.
     */
    /**
     * Usuwa zaznaczonego użytkownika asynchronicznie.
     * Zabezpiecza przed usunięciem użytkownika z rolą "root".
     * Usuwa zaznaczonego użytkownika (soft-delete) i odświeża tabelę.
     */
    private void usunWybranegoUzytkownika() {
        Employee selected = tableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Brak wyboru",
                    "Wybierz użytkownika do usunięcia."
            );
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Potwierdzenie usunięcia");
        confirm.setHeaderText("Czy na pewno chcesz usunąć użytkownika?");
        confirm.setContentText(selected.getName() + " " + selected.getSurname());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userRepository.usunPracownika(selected);
                    odswiezListePracownikow(); // ponowne załadowanie aktywnych
                    showAlert(
                            Alert.AlertType.INFORMATION,
                            "Sukces",
                            "Użytkownik został oznaczony jako usunięty."
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Błąd",
                            "Nie udało się usunąć użytkownika: " + e.getMessage()
                    );
                }
            }
        });
    }

    /**
     * Wyświetla panel ustawień konfiguracyjnych systemu.
     * Tworzenie węzłów odbywa się na wątku JavaFX – brak konfliktów z toolkitem.
     */
    public void showConfigPanel() {
        if (configPanelView == null) {
            // bez asynchronicznego Task – budowa UI jest lekka
            configPanelView = createConfigPanelView();
        }
        adminPanel.setCenterPane(configPanelView);
    }

    /**
     * Tworzy widok panelu konfiguracji.
     *
     * @return VBox zawierający kompletny widok
     */
    /**
     * Buduje widok panelu konfiguracji.
     */
    private VBox createConfigPanelView() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Opcje konfiguracyjne");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // bezpieczne pobieranie wartości z ConfigManager
        boolean loggingEnabled;
        boolean notificationsEnabled;
        try {
            loggingEnabled       = ConfigManager.isLoggingEnabled();
            notificationsEnabled = ConfigManager.isNotificationsEnabled();
        } catch (Exception ex) {
            // gdyby plik properties był uszkodzony
            loggingEnabled = false;
            notificationsEnabled = false;
        }

        CheckBox logsCheckbox = new CheckBox("Włącz logi systemowe");
        logsCheckbox.setSelected(loggingEnabled);

        CheckBox notificationsCheckbox = new CheckBox("Włącz powiadomienia");
        notificationsCheckbox.setSelected(notificationsEnabled);

        Button configurePDF = new Button("Konfiguruj raporty PDF");
        configurePDF.setOnAction(e -> showPDFConfigPanel());

        Button backupButton = new Button("Wykonaj backup bazy danych");
        backupButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white;");
        backupButton.setOnAction(e -> performDatabaseBackup());

        Button saveButton = new Button("Zapisz");
        saveButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            ConfigManager.setLoggingEnabled(logsCheckbox.isSelected());
            ConfigManager.setNotificationsEnabled(notificationsCheckbox.isSelected());
            showAlert(Alert.AlertType.INFORMATION, "Zapisano",
                    "Ustawienia zostały zachowane.");
        });

        layout.getChildren().addAll(
                titleLabel,
                logsCheckbox,
                notificationsCheckbox,
                configurePDF,
                backupButton,
                saveButton
        );

        adminPanel.setCenterPane(layout);

        return layout;
    }

    /**
     * Wyświetla panel konfiguracji plików PDF.
     */
    public void showPDFConfigPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Aktualizacja loga hipermarketu");

        Label logoLabel = new Label("Logo:");
        TextField logoField = new TextField();

        Button updateLogoButton = new Button("Aktualizuj logo");

        Label sortingLabel = new Label("Sortowanie domyślne:");
        ComboBox<String> sortingComboBox = new ComboBox<>();
        sortingComboBox.getItems().addAll("Nazwa", "Data", "Priorytet");

        Label pathLabel = new Label("Ścieżka zapisu raportów:");
        TextField pathField = new TextField();
        pathField.setPromptText("Np. C:/raporty/");
        pathField.setText(ConfigManager.getReportPath());

        Button saveButton = new Button("Zapisz konfigurację");
        saveButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");

        saveButton.setOnAction(e -> {
            String path = pathField.getText().trim();

            if (path.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Błąd", "Ścieżka nie może być pusta.");
                return;
            }

            File folder = new File(path);
            if (!folder.exists() || !folder.isDirectory()) {
                showAlert(Alert.AlertType.ERROR, "Niepoprawna ścieżka", "Podany folder nie istnieje.");
                return;
            }

            ConfigManager.setReportPath(path);
            showAlert(Alert.AlertType.INFORMATION, "Zapisano", "Ścieżka została zapisana.");
        });

        Button backButton = new Button("Wróć");
        backButton.setOnAction(e -> showConfigPanel());

        layout.getChildren().addAll(
                titleLabel,
                logoLabel, logoField,
                updateLogoButton,
                sortingLabel, sortingComboBox,
                pathLabel, pathField,
                saveButton,
                backButton
        );

        adminPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla panel generowania raportów.
     */
    public void showReportsPanel() {
        if (reportsPanelView == null) {
            reportsPanelView = createReportsPanelView();
        }
        adminPanel.setCenterPane(reportsPanelView);
    }

    /**
     * Buduje (synchronnie) widok panelu raportów.
     */
    private VBox createReportsPanelView() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Wybierz rodzaj raportu");

        ComboBox<String> reportType = new ComboBox<>();
        reportType.getItems().addAll(
                "Raport sprzedaży",
                "Raport pracowników",
                "Raport zgłoszeń"
        );
        reportType.setPrefWidth(200);

        Label dateLabel = new Label("Wybierz zakres dat");
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Data początkowa");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Data końcowa");

        Button generateButton = new Button("Generuj raport");
        generateButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
        generateButton.setOnAction(e -> {
            String selected = reportType.getValue();
            LocalDate from  = startDatePicker.getValue();
            LocalDate to    = endDatePicker.getValue();

            if (selected == null || from == null || to == null) {
                showAlert(Alert.AlertType.WARNING, "Brak danych",
                        "Wybierz typ raportu oraz zakres dat.");
                return;
            }
            showFilterDialogForReport(selected, from, to);
        });

        layout.getChildren().addAll(
                titleLabel,
                reportType,
                dateLabel,
                startDatePicker,
                endDatePicker,
                generateButton
        );

        return layout;
    }


    /**
     * Wyświetla panel zgłoszeń technicznych.
     */
    public void showIssuesPanel() {
        if (issuesPanelView == null) {
            showLoadingIndicator();
            Task<VBox> task = new Task<>() {
                @Override
                protected VBox call() {
                    return createIssuesPanelView();
                }
            };
            task.setOnSucceeded(e -> {
                issuesPanelView = task.getValue();
                // Na FX-thread ustawiamy widok i odświeżamy dane
                Platform.runLater(() -> {
                    adminPanel.setCenterPane(issuesPanelView);
                    @SuppressWarnings("unchecked")
                    TableView<TechnicalIssue> tbl =
                            (TableView<TechnicalIssue>) issuesPanelView.lookup("#issuesTableView");
                    refreshIssuesTable(tbl);
                });
            });
            task.setOnFailed(e -> {
                task.getException().printStackTrace();
                Platform.runLater(() ->
                        showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się załadować panelu zgłoszeń")
                );
            });
            executor.execute(task);
        } else {
            Platform.runLater(() -> {
                adminPanel.setCenterPane(issuesPanelView);
                @SuppressWarnings("unchecked")
                TableView<TechnicalIssue> tbl =
                        (TableView<TechnicalIssue>) issuesPanelView.lookup("#issuesTableView");
                refreshIssuesTable(tbl);
            });
        }
    }

    // w dowolnym miejscu klasy AdminPanelController (np. po metodzie showReportsPanel)
    private void showFilterDialogForReport(String reportType, LocalDate from, LocalDate to) {
        // TODO: tutaj wyświetl dialog wyboru dodatkowych filtrów
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Filtruj raport");
        alert.setHeaderText("Typ: " + reportType
                + "\nOkres: " + from + " – " + to);
        alert.showAndWait();
    }



    /**
     * Tworzy widok panelu zgłoszeń.
     *
     * @return VBox zawierający kompletny widok
     */
    private VBox createIssuesPanelView() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label title = new Label("Lista zgłoszeń technicznych");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<TechnicalIssue> tbl = new TableView<TechnicalIssue>();
        tbl.setId("issuesTableView");
        tbl.setMinHeight(200);

        // kolumny
        TableColumn<TechnicalIssue, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<TechnicalIssue, String> typeCol = new TableColumn<>("Typ");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<TechnicalIssue, LocalDate> dateCol = new TableColumn<>("Data zgłoszenia");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateSubmitted"));

        TableColumn<TechnicalIssue, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<String> cb = new ComboBox<>(
                    FXCollections.observableArrayList("Nowe", "W trakcie", "Rozwiązane")
            );
            {
                cb.setOnAction(e -> {
                    TechnicalIssue issue = getTableRow().getItem();
                    if (issue != null) {
                        issue.setStatus(cb.getValue());
                        executor.execute(new Task<>() {
                            @Override protected Void call() {
                                technicalIssueRepository.aktualizujZgloszenie(issue);
                                return null;
                            }
                        });
                    }
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    cb.setValue(getTableRow().getItem().getStatus());
                    setGraphic(cb);
                }
            }
        });

        tbl.getColumns().addAll(idCol, typeCol, dateCol, statusCol);

        // --- przyciski poniżej tabeli ---
        Button details = new Button("Pokaż szczegóły");
        details.setOnAction(e -> showIssueDetails(tbl));

        Button refresh = new Button("Odśwież listę");
        refresh.setOnAction(e -> refreshIssuesTable(tbl));

        HBox btnBox = new HBox(10, details, refresh);
        btnBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(title, tbl, btnBox);
        return layout;
    }

    /**
     * Odświeża listę zgłoszeń technicznych.
     */
    private void refreshIssuesTable(TableView<TechnicalIssue> tbl) {
        Task<List<TechnicalIssue>> task = new Task<>() {
            @Override protected List<TechnicalIssue> call() {
                return technicalIssueRepository.pobierzWszystkieZgloszenia();
            }
        };
        task.setOnSucceeded(e -> {
            tbl.getItems().setAll(task.getValue());
        });
        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            Platform.runLater(() ->
                    showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się pobrać zgłoszeń")
            );
        });
        executor.execute(task);
    }

    /**
     * Wyświetla szczegóły wybranego zgłoszenia.
     */
    private void showIssueDetails(TableView<TechnicalIssue> tbl) {
        TechnicalIssue sel = tbl.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "Brak wyboru", "Wybierz zgłoszenie!");
            return;
        }
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Szczegóły zgłoszenia");
        a.setHeaderText("Zgłoszenie ID: " + sel.getId());
        a.setContentText(
                "Typ: " + sel.getType() + "\n" +
                        "Opis: " + sel.getDescription() + "\n" +
                        "Data: " + sel.getDateSubmitted() + "\n" +
                        "Pracownik ID: " + sel.getEmployee().getId() + "\n" +
                        "Status: " + sel.getStatus()
        );
        a.showAndWait();
    }
    /**
     * Wylogowuje użytkownika i uruchamia okno logowania.
     */
    public void logout() {
        // Zamknij połączenia z bazą danych
        Task<Void> closeTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                technicalIssueRepository.close();
                userRepository.close();
                return null;
            }
        };

        closeTask.setOnSucceeded(e -> {
            primaryStage.close();
            Stage loginStage = new Stage();
            try {
                new HelloApplication().start(loginStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        executor.execute(closeTask);
    }

    /**
     * Wykonuje backup bazy danych MySQL do pliku .sql.
     */
    private void performDatabaseBackup() {
        try {
            String timestamp = java.time.LocalDateTime.now().toString().replace(":", "-");
            String fileName = "stonkadb-backup-" + timestamp + ".sql";

            File backupDir = new File("backups");
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            File outputFile = new File(backupDir, fileName);

            // Wykrywanie systemu operacyjnego
            String os = System.getProperty("os.name").toLowerCase();
            String mysqldumpPath;

            if (os.contains("win")) {
                // Ścieżka dla Windows
                mysqldumpPath = "C:\\xampp\\mysql\\bin\\mysqldump.exe";
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                // Ścieżka dla Linux/Unix/Mac
                File[] possiblePaths = {
                        new File("/usr/bin/mysqldump"),
                        new File("/usr/local/bin/mysqldump"),
                        new File("/usr/local/mysql/bin/mysqldump"),
                        new File("/opt/mysql/bin/mysqldump")
                };

                File foundPath = null;
                for (File path : possiblePaths) {
                    if (path.exists()) {
                        foundPath = path;
                        break;
                    }
                }

                if (foundPath != null) {
                    mysqldumpPath = foundPath.getAbsolutePath();
                } else {
                    mysqldumpPath = "mysqldump";
                }
            } else {
                throw new UnsupportedOperationException("Nieobsługiwany system operacyjny: " + os);
            }

            ProcessBuilder pb = new ProcessBuilder(
                    mysqldumpPath,
                    "-u", org.example.database.ILacz.MYSQL_USER,
                    "--databases", org.example.database.ILacz.DB_NAME
            );

            String password = org.example.database.ILacz.MYSQL_PASSWORD;
            if (password != null && !password.isEmpty()) {
                Map<String, String> env = pb.environment();
                env.put("MYSQL_PWD", password);
            }

            pb.redirectOutput(outputFile);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                showAlert(Alert.AlertType.INFORMATION, "Backup zakończony",
                        "Plik zapisany:\n" + outputFile.getAbsolutePath());
            } else {
                showAlert(Alert.AlertType.ERROR, "Błąd backupu",
                        "Nie udało się wykonać kopii zapasowej. Kod wyjścia: " + exitCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Wyjątek",
                    "Wystąpił błąd podczas backupu:\n" + e.getMessage());
        }
    }

    /**
     * Wyświetla komunikat w okienku dialogowym.
     *
     * @param type   typ alertu
     * @param title  tytuł okna
     * @param header treść nagłówka
     */
    private void showAlert(Alert.AlertType type, String title, String header) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(null);
            alert.showAndWait();
        });
    }

    private void otworzOknoNowegoAdresu(ComboBox<Address> adresComboBox) {
        Stage stage = new Stage();
        stage.setTitle("Dodaj nowy adres");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField miejscowosc = new TextField();
        miejscowosc.setPromptText("Miejscowość");

        TextField numerDomu = new TextField();
        numerDomu.setPromptText("Numer domu");

        TextField numerMieszkania = new TextField();
        numerMieszkania.setPromptText("Numer mieszkania (opcjonalnie)");

        TextField kodPocztowy = new TextField();
        kodPocztowy.setPromptText("Kod pocztowy");

        TextField miasto = new TextField();
        miasto.setPromptText("Miasto");

        Button zapiszBtn = new Button("Zapisz adres");

        zapiszBtn.setOnAction(e -> {
            // WALIDACJA
            if (miejscowosc.getText().isEmpty()
                    || numerDomu.getText().isEmpty()
                    || kodPocztowy.getText().isEmpty()
                    || miasto.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Wszystkie pola (poza numerem mieszkania) muszą być wypełnione.");
                return;
            }

            if (!kodPocztowy.getText().matches("\\d{2}-\\d{3}")) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nieprawidłowy format kodu pocztowego. Poprawny to np. 00-001.");
                return;
            }

            // ZAPIS
            AddressRepository repo = new AddressRepository();
            Address nowy = new Address();
            nowy.setMiejscowosc(miejscowosc.getText());
            nowy.setNumerDomu(numerDomu.getText());
            nowy.setNumerMieszkania(numerMieszkania.getText().isEmpty() ? null : numerMieszkania.getText());
            nowy.setKodPocztowy(kodPocztowy.getText());
            nowy.setMiasto(miasto.getText());

            repo.dodajAdres(nowy);

            // Odśwież listę i wybierz nowy adres
            adresComboBox.getItems().clear();
            adresComboBox.getItems().addAll(repo.pobierzWszystkieAdresy());
            adresComboBox.setValue(nowy);

            stage.close();
        });

        layout.getChildren().addAll(
                new Label("Nowy adres:"),
                miejscowosc, numerDomu, numerMieszkania,
                kodPocztowy, miasto, zapiszBtn
        );

        stage.setScene(new javafx.scene.Scene(layout));
        stage.show();
    }
    @FXML
    public void onGenerateReportsClicked() {
        System.out.println("Kliknięto Raporty w panelu admina");
        try {
            // Twój dotychczasowy kod generowania raportów:
            File reportsDir = new File("reports");
            if (!reportsDir.exists()) reportsDir.mkdirs();

            // raport statystyk
            StatsReportGenerator statsGen = new StatsReportGenerator();
            statsGen.setLogoPath(getClass().getResource("/logo.png").getPath());
            statsGen.setTaskData(List.of(
                    new StatsReportGenerator.TaskRecord("Inwentaryzacja", "Magazyn",
                            StatsReportGenerator.Priority.MEDIUM,
                            LocalDate.now().minusDays(1),
                            LocalDate.now(),
                            "Jan Kowalski")
            ));
            File statsPdf = statsGen.generateReport(
                    "reports/stats_report.pdf",
                    LocalDate.now(),
                    StatsReportGenerator.PeriodType.DAILY,
                    List.of("Magazyn"),
                    List.of(StatsReportGenerator.Priority.MEDIUM)
            );

            // raport zadań
            TaskReportGenerator taskGen = new TaskReportGenerator();
            taskGen.setLogoPath(getClass().getResource("/logo.png").getPath());
            taskGen.setTaskData(List.of(
                    new TaskReportGenerator.TaskRecord("Sprzątanie",
                            LocalDate.now().plusDays(2),
                            null,
                            "Anna Nowak")
            ));
            File tasksPdf = taskGen.generateReport(
                    "reports/tasks_report.pdf",
                    TaskReportGenerator.PeriodType.LAST_WEEK,
                    List.of("W trakcie")
            );

            showAlert("Sukces",
                    "Wygenerowano raporty:\n" +
                            statsPdf.getName() + "\n" +
                            tasksPdf.getName()
            );
        } catch (NoDataException nde) {
            showAlert("Brak danych", nde.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Błąd", "Coś poszło nie tak: " + ex.getMessage());
        }
    }




    /** Pomocnicza metoda: ścieżka do zasobu w classpath */
    private String getResourcePath(String resourceName) {
        return getClass().getClassLoader().getResource(resourceName).getPath();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
