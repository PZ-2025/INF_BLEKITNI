/*
 * Classname: AdminPanelController
 * Version information: 1.1
 * Date: 2025-04-27
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.database.TechnicalIssueRepository;
import org.example.database.UserRepository;
import org.example.sys.Employee;
import org.example.pdflib.ConfigManager;
import org.example.sys.TechnicalIssue;
import org.example.database.AddressRepository;
import org.example.sys.Address;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

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
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Lista użytkowników");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        tableView = new TableView<>();

        // === Poprawione nazwy pól zgodnie z getterami ===
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
        adminPanel.setCenterPane(layout);
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
                if (nameField.getText().isEmpty()
                        || surnameField.getText().isEmpty()
                        || loginField.getText().isEmpty()
                        || emailField.getText().isEmpty()
                        || stanowiskoBox.getValue() == null
                        || ageField.getText().isEmpty()
                        || salaryField.getText().isEmpty()) {
                    showAlert(
                            Alert.AlertType.WARNING,
                            "Brak danych",
                            "Uzupełnij wszystkie pola (poza hasłem)."
                    );
                    return;
                }

                selected.setName(nameField.getText());
                selected.setSurname(surnameField.getText());
                selected.setLogin(loginField.getText());
                selected.setEmail(emailField.getText());

                if (!passwordField.getText().isEmpty()) {
                    selected.setPassword(passwordField.getText());
                }

                selected.setStanowisko(stanowiskoBox.getValue());
                selected.setAge(
                        Integer.parseInt(ageField.getText())
                );
                selected.setZarobki(
                        new BigDecimal(salaryField.getText())
                );

                userRepository.aktualizujPracownika(selected);

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Sukces",
                        "Dane użytkownika zostały zaktualizowane."
                );
                showUserManagement();

            } catch (NumberFormatException ex) {
                showAlert(
                        Alert.AlertType.ERROR,
                        "Błąd",
                        "Nieprawidłowy format wieku lub zarobków!"
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(
                        Alert.AlertType.ERROR,
                        "Błąd",
                        "Wystąpił błąd podczas zapisywania zmian: "
                                + ex.getMessage()
                );
            }
        });

        cancelButton.setOnAction(e -> showUserManagement());
    }

    /**
     * Pobiera dane z bazy i ładuje do tabeli.
     */
    private void odswiezListePracownikow() {
        tableView.getItems().clear();
        tableView.getItems().addAll(
                userRepository.pobierzWszystkichPracownikow()
        );
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
        stanowiskoBox.getItems().addAll("Kasjer", "Kierownik", "Admin", "Logistyk");
        stanowiskoBox.setPromptText("Stanowisko");

        TextField ageField = new TextField();
        ageField.setPromptText("Wiek");

        TextField salaryField = new TextField();
        salaryField.setPromptText("Zarobki (PLN)");

        // Adres
        AddressRepository addressRepository = new AddressRepository();
        ComboBox<Address> adresComboBox = new ComboBox<>();
        adresComboBox.getItems().addAll(addressRepository.pobierzWszystkieAdresy());
        adresComboBox.setPromptText("Wybierz istniejący adres");

        Button dodajNowyAdresBtn = new Button("Dodaj nowy adres");
        dodajNowyAdresBtn.setOnAction(e -> otworzOknoNowegoAdresu(adresComboBox));

        Button saveButton = new Button("Zapisz");
        Button cancelButton = new Button("Anuluj");

        HBox buttons = new HBox(10, saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);

        formLayout.getChildren().addAll(
                titleLabel, nameField, surnameField,
                loginField, passwordField, emailField,
                stanowiskoBox, ageField, salaryField,
                new Label("Adres:"), adresComboBox, dodajNowyAdresBtn,
                buttons
        );

        adminPanel.setCenterPane(formLayout);

        saveButton.setOnAction(e -> {
            try {
                if (nameField.getText().isEmpty() || surnameField.getText().isEmpty() ||
                        loginField.getText().isEmpty() || passwordField.getText().isEmpty() ||
                        emailField.getText().isEmpty() || stanowiskoBox.getValue() == null ||
                        ageField.getText().isEmpty() || salaryField.getText().isEmpty() ||
                        adresComboBox.getValue() == null) {

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
                nowy.setStanowisko(stanowiskoBox.getValue());
                nowy.setAge(wiek);
                nowy.setZarobki(zarobki);
                nowy.setAdres(adresComboBox.getValue());

                userRepository.dodajPracownika(nowy);

                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Dodano nowego użytkownika!");
                showUserManagement();

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nieprawidłowy format wieku lub zarobków!");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się dodać użytkownika: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> showUserManagement());
    }


    /**
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
     */
    public void showConfigPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Opcje konfiguracyjne");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        CheckBox logsCheckbox = new CheckBox("Włącz logi systemowe");
        logsCheckbox.setSelected(true);

        Button configurePDF = new Button("Konfiguruj raporty PDF");
        configurePDF.setOnAction(e -> showPDFConfigPanel());

        Button backupButton = new Button("Wykonaj backup bazy danych");
        backupButton.setStyle(
                "-fx-background-color: #27AE60; -fx-text-fill: white;"
        );
        backupButton.setOnAction(e -> performDatabaseBackup());

        Button saveButton = new Button("Zapisz");
        saveButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");

        layout.getChildren().addAll(
                titleLabel,
                logsCheckbox,
                configurePDF,
                backupButton,
                saveButton
        );

        adminPanel.setCenterPane(layout);
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
        generateButton.setStyle(
                "-fx-background-color: #3498DB; "
                        + "-fx-text-fill: white;"
        );

        layout.getChildren().addAll(
                titleLabel,
                reportType,
                dateLabel,
                startDatePicker,
                endDatePicker,
                generateButton
        );

        adminPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla panel zgłoszeń technicznych.
     */
    public void showIssuesPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        Label titleLabel = new Label("Lista zgłoszeń technicznych");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Tabela zgłoszeń
        TableView<TechnicalIssue> issuesTableView = new TableView<>();
        issuesTableView.setMinHeight(200);

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
                    TechnicalIssue issue = getTableView().getItems().get(getIndex());
                    issue.setStatus(comboBox.getValue());
                    technicalIssueRepository.aktualizujZgloszenie(issue); // Zapisz zmianę w bazie
                    refreshIssuesTable(issuesTableView); // Odśwież widok
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    comboBox.setValue(getTableRow().getItem().getStatus());
                    setGraphic(comboBox);
                }
            }
        });

        issuesTableView.getColumns().addAll(idCol, typeCol, dateCol, statusCol);
        refreshIssuesTable(issuesTableView);

        layout.getChildren().addAll(titleLabel, issuesTableView);
        adminPanel.setCenterPane(layout);
    }

    /**
     * Odświeża listę zgłoszeń technicznych.
     */
    private void refreshIssuesTable(TableView<TechnicalIssue> tableView) {
        tableView.getItems().clear();
        tableView.getItems().addAll(technicalIssueRepository.pobierzWszystkieZgloszenia());
    }

    /**
     * Wyświetla szczegóły wybranego zgłoszenia.
     */
    private void showIssueDetails() {
        TechnicalIssue selected = issuesTableView.getSelectionModel().getSelectedItem();
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
        technicalIssueRepository.close();
        userRepository.close();
        primaryStage.close();
        Stage loginStage = new Stage();
        try {
            new HelloApplication().start(loginStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    private void showAlert(
            Alert.AlertType type,
            String title,
            String header
    ) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(null);
        alert.showAndWait();
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

}