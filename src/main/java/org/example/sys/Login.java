/*
 * Classname: Login
 * Version information: 1.2
 * Date: 2025-05-22
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
import org.example.gui.AdminPanel;
import org.example.gui.CashierPanel;
import org.example.gui.HelloApplication;
import org.example.gui.ManagerPanel;
import org.example.gui.LogisticianPanel;
import javax.mail.MessagingException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Klasa odpowiedzialna za logikę logowania użytkowników do systemu.
 * Zawiera metody do obsługi logowania, wysyłania kodów resetujących oraz zarządzania sesjami użytkowników.
 */
public class Login implements ILacz {

    private static final ExecutorService executor =
            Executors.newVirtualThreadPerTaskExecutor();
    private static final UserRepository userRepository = new UserRepository();

    /**
     * Próbuje zalogować użytkownika na podstawie podanego loginu i hasła.
     *
     * @param username nazwa użytkownika
     * @param password hasło użytkownika
     * @param root     główny kontener aplikacji
     */
    public static void attemptLogin(String username, String password, VBox root) {
        if (username.isBlank() || password.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Brak danych", "Proszę wypełnić " +
                    "wszystkie pola");
            return;
        }

        Task<Employee> loginTask = createLoginTask(username, password, root);
        setupTaskHandlers(loginTask, root);
        executor.execute(loginTask);
    }

    /**
     * Tworzy zadanie do logowania użytkownika.
     *
     * @param username nazwa użytkownika
     * @param password hasło użytkownika
     * @param root     główny kontener aplikacji
     * @return zadanie logowania
     */
    private static Task<Employee> createLoginTask(String username, String password,
                                                  VBox root) {
        return new Task<>() {
            @Override
            protected Employee call() throws Exception {
                return userRepository.findByLoginAndPassword(username, password);
            }
        };
    }

    /**
     * Ustawia obsługę zdarzeń dla zadania logowania.
     *
     * @param task zadanie logowania
     * @param root główny kontener aplikacji
     */
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

    /**
     * Obsługuje pomyślne zalogowanie użytkownika.
     *
     * @param employee obiekt pracownika
     * @param root     główny kontener aplikacji
     */
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
            redirectToProperPanel(employee.getPosition().toLowerCase(), root);
        });
    }

    /**
     * Wyświetla komunikat o pomyślnym zalogowaniu.
     *
     * @param employee obiekt pracownika
     */
    private static void showSuccessAlert(Employee employee) {
        HelloApplication.showAlert(
                Alert.AlertType.INFORMATION,
                "Sukces",
                "Zalogowano pomyślnie!",
                "Witaj, " + employee.getName() + "!"
        );
    }

    /**
     * Przekierowuje użytkownika do odpowiedniego panelu w zależności od jego stanowiska.
     *
     * @param position stanowisko użytkownika
     * @param root     główny kontener aplikacji
     */
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
                    case "kasjer" -> {
                        CashierPanel cashierPanel = new CashierPanel(nextStage);
                        // Resetuj flagę raportu przy nowym logowaniu
                        cashierPanel.getController().resetReportGeneratedFlag();
                        // Sprawdź, czy raport dzienny został już wygenerowany
                        if (cashierPanel.getController()
                                .isDailyReportGeneratedToday()) {
                            System.out.println("Raport dzienny już wygenerowany" +
                                    ", ustawianie flagi");
                            cashierPanel.getController().markReportAsGenerated();
                        }
                    }
                    case "logistyk" -> new LogisticianPanel(nextStage);
                    default -> showUnknownPositionAlert(position);
                }
            }
        });
    }

    /**
     * Obsługuje błąd logowania.
     *
     * @param exception wyjątek związany z logowaniem
     */
    private static void handleLoginFailure(Throwable exception) {
        Platform.runLater(() -> {
            if (exception instanceof SecurityException) {
                showAlert(Alert.AlertType.ERROR, "Błąd logowania", exception.getMessage());
            } else {
                logError(exception);
                showAlert(Alert.AlertType.ERROR, "Błąd połączenia", "Problem z " +
                        "połączeniem do systemu");
            }
        });
    }

    /**
     * Loguje błąd do konsoli.
     *
     * @param exception wyjątek do zalogowania
     */
    private static void logError(Throwable exception) {
        System.err.println("Błąd logowania: ");
        exception.printStackTrace();
    }

    /**
     * Wysyła kod resetujący na podany adres email.
     *
     * @param email adres email, na który ma zostać wysłany kod
     */
    public static void sendResetCode(String email) {
        if (!EmailValidator.isValid(email)) {
            showAlert(Alert.AlertType.ERROR, "Nieprawidłowy email", "Podaj poprawny" +
                    " adres email");
            return;
        }

        executor.execute(() -> {
            String resetCode = generateRandomCode(6);
            EmailSender.sendResetEmail(email, resetCode);
            showSuccessAlert("Kod resetujący wysłany", "Sprawdź swoją skrzynkę" +
                    " pocztową");
        });
    }

    /**
     * Generuje losowy kod o podanej długości.
     *
     * @param length długość kodu
     * @return losowy kod
     */
    private static String generateRandomCode(int length) {
        return new Random().ints(length, 0, 36)
                .mapToObj(i -> i < 10 ? String.valueOf(i) : String.valueOf((char) (i + 55)))
                .collect(Collectors.joining());
    }

    /**
     * Obsługuje błąd wysyłania emaila.
     *
     * @param e wyjątek związany z wysyłaniem emaila
     */
    private static void handleEmailError(MessagingException e) {
        Platform.runLater(() ->
                showAlert(Alert.AlertType.ERROR, "Błąd wysyłania", e.getMessage()));
    }

    /**
     * Wyświetla komunikat o błędzie.
     *
     * @param type    typ alertu
     * @param title   tytuł alertu
     * @param message treść alertu
     */
    private static void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() ->
                HelloApplication.showAlert(type, title, message, ""));
    }

    /**
     * Wyświetla komunikat o sukcesie.
     *
     * @param title   tytuł alertu
     * @param content treść alertu
     */
    private static void showSuccessAlert(String title, String content) {
        Platform.runLater(() ->
                HelloApplication.showAlert(Alert.AlertType.INFORMATION, title, content, ""));
    }

    /**
     * Wyświetla komunikat o nieznanym stanowisku.
     *
     * @param position stanowisko użytkownika
     */
    private static void showUnknownPositionAlert(String position) {
        HelloApplication.showAlert(
                Alert.AlertType.WARNING,
                "Brak panelu",
                "Nieznana rola użytkownika",
                "Stanowisko: " + position
        );
    }
}