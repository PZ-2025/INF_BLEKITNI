/*
 * Classname: Login
 * Version information: 1.1
 * Date: 2025-05-17
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.database.ILacz;
import org.example.database.UserRepository;
import org.example.gui.*;
import org.example.sys.Employee;
import javax.mail.MessagingException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Login implements ILacz {

    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private static final UserRepository userRepository = new UserRepository();

    public static void attemptLogin(String username, String password, VBox root) {
        if (username.isBlank() || password.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Brak danych", "Proszę wypełnić wszystkie pola");
            return;
        }

        Task<Employee> loginTask = createLoginTask(username, password, root);
        setupTaskHandlers(loginTask, root);
        executor.execute(loginTask);
    }

    private static Task<Employee> createLoginTask(String username, String password, VBox root) {
        return new Task<>() {
            @Override
            protected Employee call() throws Exception {
                return userRepository.znajdzPoLoginieIHasle(username, password);
            }
        };
    }

    private static void setupTaskHandlers(Task<Employee> task, VBox root) {
        task.setOnSucceeded(e -> {
            Employee employee = task.getValue();
            if (employee != null) {
                handleLoginSuccess(employee, root);
            } else {
                // Obsługa przypadku, gdy nie znaleziono użytkownika
                Platform.runLater(() ->
                        showAlert(Alert.AlertType.ERROR, "Błąd logowania",
                                "Nieprawidłowy login lub hasło. Spróbuj ponownie.")
                );
            }
        });
        task.setOnFailed(e -> handleLoginFailure(task.getException()));
    }

    private static void handleLoginSuccess(Employee employee, VBox root) {
        // Dodatkowe zabezpieczenie przed null
        if (employee == null) {
            Platform.runLater(() ->
                    showAlert(Alert.AlertType.ERROR, "Błąd logowania",
                            "Wystąpił błąd podczas logowania. Spróbuj ponownie.")
            );
            return;
        }

        Platform.runLater(() -> {
            UserRepository.setLoggedInEmployee(employee.getId());
            showSuccessAlert(employee);
            redirectToProperPanel(employee.getStanowisko().toLowerCase(), root);
        });
    }

    private static void showSuccessAlert(Employee employee) {
        HelloApplication.showAlert(
                Alert.AlertType.INFORMATION,
                "Sukces",
                "Zalogowano pomyślnie!",
                "Witaj, " + employee.getName() + "!"
        );
    }

    private static void redirectToProperPanel(String position, VBox root) {
        Platform.runLater(() -> {
            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.close();

            Stage nextStage = new Stage();
            if ("root".equalsIgnoreCase(position)) {
                new AdminPanel(nextStage);
            } else {
                switch (position.toLowerCase()) {
                    case "admin" -> new AdminPanel(nextStage);
                    case "kierownik" -> new ManagerPanel(nextStage);
                    case "kasjer" -> new CashierPanel(nextStage);
                    case "logistyk" -> new LogisticianPanel(nextStage);
                    default -> showUnknownPositionAlert(position);
                }
            }
        });
    }

    private static void handleLoginFailure(Throwable exception) {
        Platform.runLater(() -> {
            if (exception instanceof SecurityException) {
                showAlert(Alert.AlertType.ERROR, "Błąd logowania", exception.getMessage());
            } else {
                logError(exception);
                showAlert(Alert.AlertType.ERROR, "Błąd połączenia", "Problem z połączeniem do systemu");
            }
        });
    }

    private static void logError(Throwable exception) {
        System.err.println("Błąd logowania: ");
        exception.printStackTrace();
    }

    public static void sendResetCode(String email) {
        if (!EmailValidator.isValid(email)) {
            showAlert(Alert.AlertType.ERROR, "Nieprawidłowy email", "Podaj poprawny adres email");
            return;
        }

        executor.execute(() -> {
            String resetCode = generateRandomCode(6);
            EmailSender.sendResetEmail(email, resetCode);
            showSuccessAlert("Kod resetujący wysłany", "Sprawdź swoją skrzynkę pocztową");
        });
    }

    private static String generateRandomCode(int length) {
        return new Random().ints(length, 0, 36)
                .mapToObj(i -> i < 10 ? String.valueOf(i) : String.valueOf((char) (i + 55)))
                .collect(Collectors.joining());
    }

    private static void handleEmailError(MessagingException e) {
        Platform.runLater(() ->
                showAlert(Alert.AlertType.ERROR, "Błąd wysyłania", e.getMessage()));
    }

    private static void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() ->
                HelloApplication.showAlert(type, title, message, ""));
    }

    private static void showSuccessAlert(String title, String content) {
        Platform.runLater(() ->
                HelloApplication.showAlert(Alert.AlertType.INFORMATION, title, content, ""));
    }

    private static void showUnknownPositionAlert(String position) {
        HelloApplication.showAlert(
                Alert.AlertType.WARNING,
                "Brak panelu",
                "Nieznana rola użytkownika",
                "Stanowisko: " + position
        );
    }
}