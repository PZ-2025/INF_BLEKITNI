/*
 * Classname: ManagerPanelController
 * Version information: 1.3
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */

package org.example.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.database.AbsenceRequestRepository;
import org.example.database.TaskRepository;
import org.example.database.UserRepository;
import org.example.sys.AbsenceRequest;
import org.example.sys.Employee;
import org.example.sys.Task;

import java.time.LocalTime;
import java.sql.Date;


/**
 * Kontroler logiki interfejsu użytkownika dla panelu kierownika.
 */
public class ManagerPanelController {

    private final ManagerPanel managerPanel;
    private final Stage primaryStage;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private TableView<String> absenceTable;

    /**
     * Konstruktor kontrolera.
     *
     * @param managerPanel główny panel kierownika
     */
    public ManagerPanelController(ManagerPanel managerPanel) {
        this.managerPanel = managerPanel;
        this.primaryStage = managerPanel.getPrimaryStage();
        this.userRepository = new UserRepository();
        this.taskRepository = new TaskRepository();
    }

    /**
     * Wyświetla panel z listą zadań oraz rekrutacji.
     */
    public void showTaskPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label taskLabel = new Label("Lista zadań");

        TableView<Task> taskTable = new TableView<>();
        taskTable.setMinHeight(200);

        TableColumn<Task, String> nameCol = new TableColumn<>("Zadanie");
        nameCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getName()
                )
        );

        TableColumn<Task, String> dateCol = new TableColumn<>("Termin");
        dateCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDate() != null
                                ? data.getValue().getDate().toString()
                                : "brak daty"
                )
        );

        taskTable.getColumns().addAll(nameCol, dateCol);
        taskTable.getItems().addAll(taskRepository.getAllTasks());

        HBox taskButtons = new HBox(10);
        taskButtons.setAlignment(Pos.CENTER);

        Button addTaskButton = new Button("Dodaj zadanie");
        addTaskButton.setOnAction(e -> showAddTaskPanel());

        Button assignEmployeeButton = new Button("Przypisz pracownika");
        assignEmployeeButton.setOnAction(e -> showAssignEmployeeDialog());

        Button editButton = new Button("Edytuj zadanie");
        Button deleteButton = new Button("Usuń zadanie");

        editButton.setOnAction(e -> {
            Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                showEditTaskDialog(selectedTask);
            } else {
                showAlert(Alert.AlertType.WARNING, "Błąd", "Wybierz zadanie do edycji.");
            }
        });

        deleteButton.setOnAction(e -> {
            Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                taskRepository.removeTask(selectedTask);
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Usunięto zadanie.");
                showTaskPanel();
            } else {
                showAlert(Alert.AlertType.WARNING, "Błąd", "Wybierz zadanie do usunięcia.");
            }
        });

        taskButtons.getChildren().addAll(
                addTaskButton,
                assignEmployeeButton,
                editButton,
                deleteButton
        );

        Label recruitLabel = new Label("Panel rekrutacji");
        ListView<String> recruitmentList = new ListView<>();
        recruitmentList.getItems().addAll(
                "Jan Kowalski - CV.pdf",
                "Anna Nowak - CV.pdf"
        );

        HBox recruitButtons = new HBox(10);
        recruitButtons.setAlignment(Pos.CENTER);

        Button inviteButton = new Button("Zaproszenie na rozmowę");
        Button rejectButton = new Button("Odrzuć aplikację");

        recruitButtons.getChildren().addAll(inviteButton, rejectButton);

        layout.getChildren().addAll(
                taskLabel, taskTable, taskButtons,
                recruitLabel, recruitmentList, recruitButtons
        );

        managerPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla panel dodawania nowego zadania.
     */
    public void showAddTaskPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label nameLabel = new Label("Nazwa zadania");
        TextField nameField = new TextField();

        Label descLabel = new Label("Opis");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(4);

        Label statusLabel = new Label("Status");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Nowe", "W trakcie", "Zakończone");

        Label dateLabel = new Label("Termin");
        DatePicker deadlinePicker = new DatePicker();

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button backButton = new Button("Wróć");
        backButton.setOnAction(e -> showTaskPanel());

        Button saveButton = new Button("Zapisz");
        saveButton.setStyle("-fx-background-color: #2980B9; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            try {
                String name   = nameField.getText();
                String description    = descriptionArea.getText();
                String status  = statusCombo.getValue();
                // deadlinePicker to Twój DatePicker z datą terminu
                Date date = Date.valueOf(deadlinePicker.getValue());

                // jeśli masz pole timeField (np. Spinner<LocalTime>), użyj jej:
                // LocalTime timeOfShift = timeField.getValue();
                // a jeśli nie, użyj bieżącej godziny:
                LocalTime timeOfShift = LocalTime.now();

                if (name.isEmpty() || description.isEmpty() || status == null || date == null) {
                    showAlert(Alert.AlertType.WARNING, "Błąd", "Wypełnij wszystkie pola.");
                    return;
                }

                // teraz konstruktor pięcio-argumentowy
                Task newTask = new Task(name, date, status, description, timeOfShift);
                taskRepository.addTask(newTask);

                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Zadanie dodane!");
                showTaskPanel();

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Błąd",
                        "Nie udało się dodać zadania: " + ex.getMessage());
            }
        });


        buttonBox.getChildren().addAll(backButton, saveButton);

        layout.getChildren().addAll(
                nameLabel, nameField,
                descLabel, descriptionArea,
                statusLabel, statusCombo,
                dateLabel, deadlinePicker,
                buttonBox
        );

        managerPanel.setCenterPane(layout);
    }

    /**
     * Wyświetla panel z wnioskami o nieobecność.
     */
    public void showAbsencePanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label absenceLabel = new Label("Wnioski o nieobecność");
        absenceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Utworzenie tabeli wniosków
        TableView<AbsenceRequest> absenceTable = new TableView<>();
        absenceTable.setMinHeight(300);
        absenceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Kolumna ID wniosku
        TableColumn<AbsenceRequest, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(
                data.getValue().getId()).asObject());
        idColumn.setPrefWidth(50);

        // Kolumna typu wniosku
        TableColumn<AbsenceRequest, String> typeColumn = new TableColumn<>("Typ wniosku");
        typeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getRequestType()));
        typeColumn.setPrefWidth(150);

        // Kolumna pracownika
        TableColumn<AbsenceRequest, String> employeeColumn = new TableColumn<>("Pracownik");
        employeeColumn.setCellValueFactory(data -> {
            Employee employee = data.getValue().getEmployee();
            if (employee != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        employee.getName() + " " + employee.getSurname());
            } else {
                return new javafx.beans.property.SimpleStringProperty("Nieznany");
            }
        });
        employeeColumn.setPrefWidth(150);

        // Kolumna daty od
        TableColumn<AbsenceRequest, String> fromDateColumn = new TableColumn<>("Od");
        fromDateColumn.setCellValueFactory(data -> {
            // użyj:
            java.util.Date utilStart = data.getValue().getStartDate();
            if (utilStart != null) {
                java.sql.Date sqlStart = new java.sql.Date(utilStart.getTime());
                return new javafx.beans.property.SimpleStringProperty(sqlStart.toString());
            } else {
                return new javafx.beans.property.SimpleStringProperty("Brak daty");
            }
        });
        fromDateColumn.setPrefWidth(100);

        // Kolumna daty do
        TableColumn<AbsenceRequest, String> toDateColumn = new TableColumn<>("Do");
        toDateColumn.setCellValueFactory(data -> {
            java.util.Date utilEnd = data.getValue().getEndDate();
            if (utilEnd != null) {
                java.sql.Date sqlEnd = new java.sql.Date(utilEnd.getTime());
                return new javafx.beans.property.SimpleStringProperty(sqlEnd.toString());
            } else {
                return new javafx.beans.property.SimpleStringProperty("Brak daty");
            }
        });
        toDateColumn.setPrefWidth(100);

        // Kolumna opisu
        TableColumn<AbsenceRequest, String> descriptionColumn = new TableColumn<>("Opis");
        descriptionColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDescription()));
        descriptionColumn.setPrefWidth(200);

        // Dodanie kolumn do tabeli
        absenceTable.getColumns().addAll(
                idColumn, typeColumn, employeeColumn,
                fromDateColumn, toDateColumn, descriptionColumn
        );

        // Utworzenie repozytorium wniosków
        AbsenceRequestRepository absenceRepository = new AbsenceRequestRepository();

        // Pobranie wszystkich wniosków
        try {
            absenceTable.getItems().addAll(absenceRepository.getAllRequests());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd",
                    "Nie udało się załadować wniosków: " + e.getMessage());
        }

        // Przyciski akcji
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button approveButton = new Button("Zatwierdź");
        approveButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white;");

        Button rejectButton = new Button("Odrzuć");
        rejectButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");

        Button refreshButton = new Button("Odśwież");
        refreshButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");

        // Obsługa zatwierdzania wniosku
        approveButton.setOnAction(e -> {
            AbsenceRequest selectedRequest = absenceTable.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                try {
                    String currentDescription = selectedRequest.getDescription();
                    String newDescription = (currentDescription != null && !currentDescription.isEmpty())
                            ? currentDescription + " [ZATWIERDZONY]"
                            : "[ZATWIERDZONY]";
                    selectedRequest.setDescription(newDescription);

                    // Aktualizacja wniosku
                    absenceRepository.updateRequest(selectedRequest);

                    // Aktualizacja statusu pracownika, jeśli to urlop chorobowy
                    if (selectedRequest.getRequestType().toLowerCase().contains("chorob")) {
                        Employee employee = selectedRequest.getEmployee();
                        employee.startSickLeave(selectedRequest.getStartDate());
                        userRepository.updateEmployee(employee);
                    }

                    showAlert(Alert.AlertType.INFORMATION, "Sukces",
                            "Wniosek został zatwierdzony.");

                    // Odświeżenie tabeli
                    absenceTable.getItems().clear();
                    absenceTable.getItems().addAll(absenceRepository.getAllRequests());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Błąd",
                            "Nie udało się zatwierdzić wniosku: " + ex.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Błąd",
                        "Wybierz wniosek do zatwierdzenia.");
            }
        });

        // Obsługa odrzucania wniosku
        rejectButton.setOnAction(e -> {
            AbsenceRequest selectedRequest = absenceTable.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                try {
                    String currentDescription = selectedRequest.getDescription();
                    String newDescription = (currentDescription != null && !currentDescription.isEmpty())
                            ? currentDescription + " [REJECTED]"
                            : "[REJECTED]";
                    selectedRequest.setDescription(newDescription);

                    absenceRepository.updateRequest(selectedRequest);
                    showAlert(Alert.AlertType.INFORMATION, "Sukces",
                            "Wniosek został odrzucony.");

                    // Odświeżenie tabeli
                    absenceTable.getItems().clear();
                    absenceTable.getItems().addAll(absenceRepository.getAllRequests());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Błąd",
                            "Nie udało się odrzucić wniosku: " + ex.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Błąd",
                        "Wybierz wniosek do odrzucenia.");
            }
        });

        // Obsługa odświeżania tabeli
        refreshButton.setOnAction(e -> {
            try {
                absenceTable.getItems().clear();
                absenceTable.getItems().addAll(absenceRepository.getAllRequests());
                showAlert(Alert.AlertType.INFORMATION, "Sukces",
                        "Lista wniosków została odświeżona.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Błąd",
                        "Nie udało się odświeżyć listy wniosków: " + ex.getMessage());
            }
        });

        buttonBox.getChildren().addAll(approveButton, rejectButton, refreshButton);

        // Przycisk powrotu
        Button backButton = new Button("Wróć");
        backButton.setOnAction(e -> showTaskPanel());

        // Zamknięcie repozytorium po zamknięciu panelu
        primaryStage.setOnHidden(event -> {
            if (absenceRepository != null) {
                absenceRepository.close();
            }
        });

        layout.getChildren().addAll(
                absenceLabel, absenceTable, buttonBox, backButton
        );

        managerPanel.setCenterPane(layout);
    }



    /**
     * Wyświetla okno przypisywania pracownika do zadania.
     */
    public void showAssignEmployeeDialog() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Przypisz pracownika do zadania");

        VBox dialogLayout = new VBox(15);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.setAlignment(Pos.CENTER);

        Label taskLabel = new Label("Wybierz zadanie:");
        ComboBox<String> taskComboBox = new ComboBox<>();
        taskRepository.getAllTasks()
                .forEach(t -> taskComboBox.getItems().add(t.getName()));

        Label employeeLabel = new Label("Wybierz pracownika:");
        ComboBox<String> employeeComboBox = new ComboBox<>();
        userRepository.getAllEmployess().forEach(p ->
                employeeComboBox.getItems().add(p.getName() + " " + p.getSurname())
        );

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button assignButton = new Button("Przypisz");
        Button cancelButton = new Button("Anuluj");

        assignButton.setOnAction(e -> {
            if (taskComboBox.getValue() != null && employeeComboBox.getValue() != null) {
                System.out.println(
                        "Przypisano: " + employeeComboBox.getValue() +
                                " do " + taskComboBox.getValue()
                );
                dialogStage.close();
            } else {
                showAlert(Alert.AlertType.WARNING, "Błąd",
                        "Wybierz zarówno zadanie, jak i pracownika.");
            }
        });

        cancelButton.setOnAction(e -> dialogStage.close());
        buttonBox.getChildren().addAll(assignButton, cancelButton);

        dialogLayout.getChildren().addAll(
                taskLabel, taskComboBox,
                employeeLabel, employeeComboBox,
                buttonBox
        );

        Scene scene = new Scene(dialogLayout, 300, 250);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    /**
     * Wyświetla okno dialogowe edycji zadania.
     *
     * @param task zadanie do edycji
     */
    private void showEditTaskDialog(Task task) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Edycja zadania");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label nameLabel = new Label("Nazwa zadania:");
        TextField nameField = new TextField(task.getName());

        Label descLabel = new Label("Opis:");
        TextArea descArea = new TextArea(task.getDescription());
        descArea.setPrefRowCount(4);

        Label statusLabel = new Label("Status:");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Nowe", "W trakcie", "Zakończone");
        statusCombo.setValue(task.getStatus());

        Label dateLabel = new Label("Termin:");
        DatePicker deadlinePicker = new DatePicker();
        if (task.getDate() != null) {
            deadlinePicker.setValue(
                    new java.sql.Date(task.getDate().getTime()).toLocalDate()
            );
        }

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button saveButton = new Button("Zapisz");
        Button cancelButton = new Button("Anuluj");

        saveButton.setOnAction(e -> {
            try {
                task.setName(nameField.getText());
                task.setDescription(descArea.getText());
                task.setStatus(statusCombo.getValue());
                if (deadlinePicker.getValue() != null) {
                    task.setDate(java.sql.Date.valueOf(deadlinePicker.getValue()));
                }
                taskRepository.updateTask(task);

                showAlert(Alert.AlertType.INFORMATION, "Sukces",
                        "Zadanie zaktualizowane!");
                dialogStage.close();
                showTaskPanel();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Błąd",
                        "Nie udało się zaktualizować zadania: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialogStage.close());
        buttonBox.getChildren().addAll(saveButton, cancelButton);

        layout.getChildren().addAll(
                nameLabel, nameField,
                descLabel, descArea,
                statusLabel, statusCombo,
                dateLabel, deadlinePicker,
                buttonBox
        );

        Scene scene = new Scene(layout, 350, 500);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    /**
     * Wylogowuje użytkownika i zamyka zasoby.
     */
    public void logout() {
        primaryStage.close();
        Stage loginStage = new Stage();
        try {
            new HelloApplication().start(loginStage);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            userRepository.close();
            taskRepository.close();
        }
    }

    /**
     * Wyświetla komunikat typu Alert.
     *
     * @param type   typ alertu
     * @param title  tytuł
     * @param header nagłówek
     */
    private void showAlert(Alert.AlertType type, String title, String header) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(null);
        alert.showAndWait();
    }
}
