/*
 * Classname: AdminPanelController
 * Version information: 1.3
 * Date: 2025-05-18
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.application.Platform;
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
import org.example.pdflib.ConfigManager;
import org.example.pdflib.ReportGenerator;
import org.example.sys.Employee;
import org.example.sys.TechnicalIssue;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;

import java.io.File;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Kontroler odpowiedzialny za obsługę logiki
 * interfejsu administratora w aplikacji GUI.
 * Zoptymalizowana wersja z asynchronicznym ładowaniem danych.
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

        // Przyciski
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
            showAlert(
                    Alert.AlertType.WARNING,
                    "Brak wyboru",
                    "Wybierz użytkownika do edycji."
            );
            return;
        }

        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(20));

        Label titleLabel = new Label("Edytuj użytkownika");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField nameField = new TextField(selected.getName());
        TextField surnameField = new TextField(selected.getSurname());
        TextField loginField = new TextField(selected.getLogin());
        TextField emailField = new TextField(selected.getEmail());

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(
                "Nowe hasło (pozostaw puste, aby nie zmieniać)"
        );

        ComboBox<String> stanowiskoBox = new ComboBox<>();
        stanowiskoBox.getItems().addAll(
                "Kasjer", "Kierownik", "Admin", "Logistyk"
        );
        stanowiskoBox.setValue(selected.getStanowisko());

        TextField ageField = new TextField(
                String.valueOf(selected.getAge())
        );
        TextField salaryField = new TextField(
                String.valueOf(selected.getZarobki())
        );

        Button saveButton = new Button("Zapisz zmiany");
        Button cancelButton = new Button("Anuluj");

        HBox buttons = new HBox(10, saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);

        formLayout.getChildren().addAll(
                titleLabel, nameField, surnameField,
                loginField, passwordField, emailField,
                stanowiskoBox, ageField, salaryField, buttons
        );

        adminPanel.setCenterPane(formLayout);

        saveButton.setOnAction(e -> {
            try {
                /* walidacja pustych pól – jak wcześniej */
                if (nameField.getText().isEmpty()
                        || surnameField.getText().isEmpty()
                        || loginField.getText().isEmpty()
                        || emailField.getText().isEmpty()
                        || stanowiskoBox.getValue() == null
                        || ageField.getText().isEmpty()
                        || salaryField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Brak danych",
                            "Uzupełnij wszystkie pola (poza hasłem).");
                    return;
                }

                showLoadingIndicator();

                final Employee employeeToUpdate = selected;
                employeeToUpdate.setName(nameField.getText());
                employeeToUpdate.setSurname(surnameField.getText());
                employeeToUpdate.setLogin(loginField.getText());
                employeeToUpdate.setEmail(emailField.getText());

                if (!passwordField.getText().isEmpty()) {
                    employeeToUpdate.setPassword(passwordField.getText()); // PasswordException
                }

                employeeToUpdate.setStanowisko(stanowiskoBox.getValue());
                employeeToUpdate.setAge(Integer.parseInt(ageField.getText()));
                employeeToUpdate.setZarobki(new BigDecimal(salaryField.getText())); // SalaryException

                Task<Void> updateTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        userRepository.aktualizujPracownika(employeeToUpdate);
                        return null;
                    }
                };

                updateTask.setOnSucceeded(evt -> {
                    showAlert(Alert.AlertType.INFORMATION, "Sukces",
                            "Dane użytkownika zostały zaktualizowane.");
                    showUserManagement();
                });

                updateTask.setOnFailed(evt -> {
                    updateTask.getException().printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Błąd",
                            "Wystąpił błąd podczas zapisywania zmian: "
                                    + updateTask.getException().getMessage());
                    showUserManagement();
                });

                executor.execute(updateTask);

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Błąd",
                        "Nieprawidłowy format wieku lub zarobków!");
                showUserManagement();
            } catch (PasswordException ex) {
                showAlert(Alert.AlertType.ERROR, "Nieprawidłowe hasło",
                        ex.getMessage());
                showUserManagement();
            } catch (SalaryException ex) {
                showAlert(Alert.AlertType.ERROR, "Nieprawidłowe zarobki",
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

        ComboBox<String> stanowiskoBox = new ComboBox<>();
        stanowiskoBox.getItems().addAll(
                "Kasjer", "Kierownik", "Admin", "Logistyk"
        );
        stanowiskoBox.setPromptText("Stanowisko");

        TextField ageField = new TextField();
        ageField.setPromptText("Wiek");

        TextField salaryField = new TextField();
        salaryField.setPromptText("Zarobki (PLN)");

        Button saveButton = new Button("Zapisz");
        Button cancelButton = new Button("Anuluj");

        HBox buttons = new HBox(10, saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);

        formLayout.getChildren().addAll(
                titleLabel, nameField, surnameField,
                loginField, passwordField, emailField,
                stanowiskoBox, ageField, salaryField, buttons
        );

        adminPanel.setCenterPane(formLayout);

        saveButton.setOnAction(e -> {
            try {
                /* walidacja pustych pól – jak wcześniej */
                if (nameField.getText().isEmpty()
                        || surnameField.getText().isEmpty()
                        || loginField.getText().isEmpty()
                        || passwordField.getText().isEmpty()
                        || emailField.getText().isEmpty()
                        || stanowiskoBox.getValue() == null
                        || ageField.getText().isEmpty()
                        || salaryField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Brak danych",
                            "Uzupełnij wszystkie pola!");
                    return;
                }

                showLoadingIndicator();

                int         wiek     = Integer.parseInt(ageField.getText());
                BigDecimal  zarobki  = new BigDecimal(salaryField.getText());

                Employee nowy = new Employee();
                nowy.setName(nameField.getText());
                nowy.setSurname(surnameField.getText());
                nowy.setLogin(loginField.getText());
                /* --- tu mogły wylecieć wyjątki --- */
                nowy.setPassword(passwordField.getText());   // PasswordException
                nowy.setEmail(emailField.getText());
                nowy.setStanowisko(stanowiskoBox.getValue());
                nowy.setAge(wiek);
                nowy.setZarobki(zarobki);                    // SalaryException

                Task<Void> addTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        userRepository.dodajPracownika(nowy);
                        return null;
                    }
                };

                addTask.setOnSucceeded(evt -> {
                    showAlert(Alert.AlertType.INFORMATION, "Sukces",
                            "Dodano nowego użytkownika!");
                    showUserManagement();
                });

                addTask.setOnFailed(evt -> {
                    addTask.getException().printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Błąd",
                            "Nie udało się dodać użytkownika: "
                                    + addTask.getException().getMessage());
                    showUserManagement();   // usuwa ekran ładowania
                });

                executor.execute(addTask);

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Błąd",
                        "Nieprawidłowy format wieku lub zarobków!");
                showUserManagement();
            } catch (PasswordException ex) {
                showAlert(Alert.AlertType.ERROR, "Nieprawidłowe hasło",
                        ex.getMessage());
                showUserManagement();
            } catch (SalaryException ex) {
                showAlert(Alert.AlertType.ERROR, "Nieprawidłowe zarobki",
                        ex.getMessage());
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
     */
    private void usunWybranegoUzytkownika() {
        Employee selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Sprawdź, czy pracownik ma rolę "root"
            if ("root".equalsIgnoreCase(selected.getStanowisko())) {
                showAlert(
                        Alert.AlertType.ERROR,
                        "Operacja niedozwolona",
                        "Nie można usunąć użytkownika z rolą root"
                );
                return;
            }

            // Pokaż potwierdzenie
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Potwierdzenie");
            confirmAlert.setHeaderText("Czy na pewno chcesz usunąć tego użytkownika?");
            confirmAlert.setContentText("Ta operacja jest nieodwracalna.");

            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Pokaż wskaźnik ładowania
                    showLoadingIndicator();

                    // Usuń asynchronicznie
                    final Employee employeeToDelete = selected;
                    Task<Void> deleteTask = new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            userRepository.usunPracownika(employeeToDelete);
                            return null;
                        }
                    };

                    deleteTask.setOnSucceeded(e -> {
                        showAlert(
                                Alert.AlertType.INFORMATION,
                                "Sukces",
                                "Usunięto użytkownika!"
                        );
                        showUserManagement();
                    });

                    deleteTask.setOnFailed(e -> {
                        Throwable exception = deleteTask.getException();
                        if (exception instanceof SecurityException) {
                            Platform.runLater(() -> showAlert(
                                    Alert.AlertType.ERROR,
                                    "Operacja niedozwolona",
                                    exception.getMessage()
                            ));
                        } else {
                            exception.printStackTrace();
                            Platform.runLater(() -> showAlert(
                                    Alert.AlertType.ERROR,
                                    "Błąd",
                                    "Nie udało się usunąć użytkownika: " + exception.getMessage()
                            ));
                        }
                        showUserManagement();
                    });

                    executor.execute(deleteTask);
                }
            });
        } else {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Brak wyboru",
                    "Wybierz użytkownika do usunięcia."
            );
        }
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
        sortingComboBox.getItems().addAll(
                "Nazwa", "Data", "Priorytet"
        );

        Button backButton = new Button("Wróć");
        backButton.setOnAction(e -> showConfigPanel());

        layout.getChildren().addAll(
                titleLabel,
                logoLabel,
                logoField,
                updateLogoButton,
                sortingLabel,
                sortingComboBox,
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
        reportType.setPrefWidth(250);

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
                new Label("Data od:"), startDatePicker,
                new Label("Data do:"), endDatePicker,
                generateButton
        );

        return layout;
    }


    /**
     * Wyświetla panel zgłoszeń technicznych.
     */
    public void showIssuesPanel() {
        if (issuesPanelView == null) {
            // Pokaż wskaźnik ładowania
            showLoadingIndicator();

            // Utwórz widok asynchronicznie
            Task<VBox> task = new Task<>() {
                @Override
                protected VBox call() throws Exception {
                    return createIssuesPanelView();
                }
            };

            task.setOnSucceeded(e -> {
                issuesPanelView = task.getValue();
                adminPanel.setCenterPane(issuesPanelView);
                // Załaduj dane zgłoszeń asynchronicznie
                refreshIssuesTable((TableView<TechnicalIssue>) issuesPanelView.lookup("#issuesTableView"));
            });

            task.setOnFailed(e -> {
                task.getException().printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się załadować panelu zgłoszeń");
            });

            executor.execute(task);
        } else {
            // Jeśli widok już istnieje, po prostu go pokaż
            adminPanel.setCenterPane(issuesPanelView);
            // Odśwież dane zgłoszeń
            refreshIssuesTable((TableView<TechnicalIssue>) issuesPanelView.lookup("#issuesTableView"));
        }
    }

    /**
     * Tworzy widok panelu zgłoszeń.
     *
     * @return VBox zawierający kompletny widok
     */
    private VBox createIssuesPanelView() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        Label titleLabel = new Label("Lista zgłoszeń technicznych");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Tabela zgłoszeń
        TableView<TechnicalIssue> issuesTableView = new TableView<>();
        issuesTableView.setId("issuesTableView"); // ID do późniejszego wyszukiwania
        issuesTableView.setMinHeight(200);

        // Optymalizacja tabeli
        issuesTableView.setFixedCellSize(25);
        issuesTableView.setCache(true);
        issuesTableView.setCacheHint(CacheHint.SPEED);

        TableColumn<TechnicalIssue, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<TechnicalIssue, String> typeCol = new TableColumn<>("Typ");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<TechnicalIssue, LocalDate> dateCol = new TableColumn<>("Data zgłoszenia");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateSubmitted"));

        TableColumn<TechnicalIssue, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<String> comboBox = new ComboBox<>();
            {
                comboBox.getItems().addAll("Nowe", "W trakcie", "Rozwiązane");
                comboBox.setOnAction(e -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        TechnicalIssue issue = getTableRow().getItem();
                        String newStatus = comboBox.getValue();

                        // Aktualizuj status asynchronicznie
                        Task<Void> updateTask = new Task<>() {
                            @Override
                            protected Void call() throws Exception {
                                issue.setStatus(newStatus);
                                technicalIssueRepository.aktualizujZgloszenie(issue);
                                return null;
                            }
                        };

                        executor.execute(updateTask);
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    comboBox.setValue(getTableRow().getItem().getStatus());
                    setGraphic(comboBox);
                }
            }
        });

        issuesTableView.getColumns().addAll(idCol, typeCol, dateCol, statusCol);

        // Dodaj przycisk do wyświetlania szczegółów
        Button detailsButton = new Button("Pokaż szczegóły");
        detailsButton.setOnAction(e -> showIssueDetails(issuesTableView));

        // Dodaj przycisk do odświeżania listy
        Button refreshButton = new Button("Odśwież listę");
        refreshButton.setOnAction(e -> refreshIssuesTable(issuesTableView));

        HBox buttonBox = new HBox(10, detailsButton, refreshButton);
        buttonBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(titleLabel, issuesTableView, buttonBox);
        return layout;
    }

    /**
     * Odświeża listę zgłoszeń technicznych asynchronicznie.
     */
    private void refreshIssuesTable(TableView<TechnicalIssue> tableView) {
        Task<List<TechnicalIssue>> task = new Task<>() {
            @Override
            protected List<TechnicalIssue> call() throws Exception {
                return technicalIssueRepository.pobierzWszystkieZgloszenia();
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
                    "Nie udało się pobrać listy zgłoszeń"
            ));
        });

        executor.execute(task);
    }

    /**
     * Wyświetla szczegóły wybranego zgłoszenia.
     */
    private void showIssueDetails(TableView<TechnicalIssue> tableView) {
        TechnicalIssue selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Brak wyboru", "Wybierz zgłoszenie do wyświetlenia.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Szczegóły zgłoszenia");
        alert.setHeaderText("Zgłoszenie ID: " + selected.getId());
        alert.setContentText(
                "Typ: " + selected.getType() + "\n" +
                        "Opis: " + selected.getDescription() + "\n" +
                        "Data zgłoszenia: " + selected.getDateSubmitted() + "\n" +
                        "Pracownik ID: " + selected.getEmployee().getId() + "\n" +
                        "Status: " + selected.getStatus()
        );
        alert.showAndWait();
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
     * Symuluje wykonanie backupu bazy danych.
     */
    private void performDatabaseBackup() {
        // Pokaż wskaźnik ładowania
        showLoadingIndicator();

        Task<String> backupTask = new Task<>() {
            @Override
            protected String call() throws Exception {
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
                    return outputFile.getAbsolutePath();
                } else {
                    throw new RuntimeException("Nie udało się wykonać kopii zapasowej. Kod wyjścia: " + exitCode);
                }
            }
        };

        backupTask.setOnSucceeded(e -> {
            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Backup zakończony",
                    "Plik zapisany:\n" + backupTask.getValue()
            );
            showConfigPanel();
        });

        backupTask.setOnFailed(e -> {
            backupTask.getException().printStackTrace();
            showAlert(
                    Alert.AlertType.ERROR,
                    "Błąd backupu",
                    "Wystąpił błąd podczas backupu:\n" + backupTask.getException().getMessage()
            );
            showConfigPanel();
        });

        executor.execute(backupTask);
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
    /**
     * Pokazuje dialog z dodatkowymi filtrami zależnie od raportu.
     */
    private void showFilterDialogForReport(String reportName, LocalDate from, LocalDate to) {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Filtruj: " + reportName);
        dialog.setHeaderText("Podaj dodatkowe parametry");

        ButtonType genType = new ButtonType("Generuj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(genType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // domyślne filtry datami
        Map<String, String> filters = new HashMap<>();
        filters.put("from", from.toString());
        filters.put("to",   to.toString());

        int row = 0;
        switch (reportName) {
            case "Raport sprzedaży":
                TextField category = new TextField();
                category.setPromptText("Kategoria produktu");
                grid.add(new Label("Kategoria:"), 0, row);
                grid.add(category, 1, row++);
                dialog.setResultConverter(btn -> {
                    if (btn == genType) {
                        filters.put("category", category.getText());
                        return filters;
                    }
                    return null;
                });
                break;

            case "Raport pracowników":
                TextField role = new TextField();
                role.setPromptText("Stanowisko");
                grid.add(new Label("Stanowisko:"), 0, row);
                grid.add(role, 1, row++);
                dialog.setResultConverter(btn -> {
                    if (btn == genType) {
                        filters.put("role", role.getText());
                        return filters;
                    }
                    return null;
                });
                break;

            case "Raport zgłoszeń":
                ComboBox<String> statusBox = new ComboBox<>();
                statusBox.getItems().addAll("Nowe", "W trakcie", "Rozwiązane");
                statusBox.setPromptText("Status zgłoszenia");
                grid.add(new Label("Status:"), 0, row);
                grid.add(statusBox, 1, row++);
                dialog.setResultConverter(btn -> {
                    if (btn == genType) {
                        filters.put("status", statusBox.getValue());
                        return filters;
                    }
                    return null;
                });
                break;

            default:
                dialog.setResultConverter(btn -> btn == genType ? filters : null);
        }

        dialog.getDialogPane().setContent(grid);

        Optional<Map<String, String>> result = dialog.showAndWait();
        result.ifPresent(flt -> {
            try {
                File output = ReportGenerator.generate(reportName, flt);
                showAlert(Alert.AlertType.INFORMATION,
                        "Gotowe",
                        "Raport wygenerowany:\n" + output.getAbsolutePath());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR,
                        "Błąd",
                        "Nie udało się wygenerować raportu:\n" + ex.getMessage());
            }
        });
    }

}
