package org.example.sys;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.database.UserRepository;
import org.example.gui.*;
import org.example.database.ILacz;
import javax.mail.MessagingException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login implements ILacz {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final UserRepository userRepository = new UserRepository();

    public static void attemptLogin(String username, String password, VBox root) {
        try {
            Employee employee = userRepository.znajdzPoLoginieIHasle(username, password);

            if (employee != null) {
                UserRepository.setLoggedInEmployee(employee.getId());

                String stanowisko = employee.getStanowisko();

                HelloApplication.showAlert(
                        Alert.AlertType.INFORMATION,
                        "Sukces",
                        "Zalogowano pomyślnie!",
                        "Witaj, " + employee.getName() + "!"
                );

                Stage currentStage = (Stage) root.getScene().getWindow();
                currentStage.close();
                Stage nextStage = new Stage();

                switch (stanowisko.toLowerCase()) {
                    case "admin":
                        new AdminPanel(nextStage);
                        break;
                    case "kierownik":
                        new ManagerPanel(nextStage);
                        break;
                    case "kasjer":
                        new CashierPanel(nextStage);
                        break;
                    case "logistyk":
                        new LogisticianPanel(nextStage);
                        break;
                    default:
                        HelloApplication.showAlert(
                                Alert.AlertType.WARNING,
                                "Brak panelu",
                                "Nieznana rola użytkownika",
                                "Stanowisko: " + stanowisko
                        );
                        break;
                }
            } else {
                HelloApplication.showAlert(
                        Alert.AlertType.ERROR,
                        "Błąd",
                        "Nieprawidłowe dane logowania!",
                        "Spróbuj ponownie."
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            HelloApplication.showAlert(
                    Alert.AlertType.ERROR,
                    "Błąd",
                    "Wystąpił błąd połączenia",
                    e.getMessage()
            );
        }
    }

    /**
     * Funkcja do wysyłania kodu odzyskiwania hasła.
     * Generuje losowy kod i wysyła go na podany adres e-mail wraz z dodatkowymi informacjami.
     *
     * @param email       Adres e-mail użytkownika
     */
    public static void sendResetCode(String email) {
        String resetCode = generateRandomCode(6);

        // Tu ustaw dodatkową treść e-maila
        String subject = "Kod resetowania hasła";
        String body = "Otrzymujesz ten kod w celu zresetowania hasła w systemie Stonka.\n\n"
                + "Twój kod resetujący to: " + resetCode;

        executor.submit(() -> {
            try {
                sendEmail(email, subject, body);
                Platform.runLater(() -> HelloApplication.showAlert(
                        Alert.AlertType.INFORMATION,
                        "E-mail wysłany",
                        "Kod resetowania został wysłany",
                        "Sprawdź swoją skrzynkę pocztową."
                ));
            } catch (Exception e) {
                Platform.runLater(() -> HelloApplication.showAlert(
                        Alert.AlertType.ERROR,
                        "Błąd wysyłania e-maila",
                        "Wystąpił problem z wysyłką e-maila",
                        e.getMessage()
                ));
            }
        });
    }

    /**
     * Wysyła e-mail do wskazanego odbiorcy, używając danych logowania z pliku PASS.txt.
     *
     * @param toEmail adres e-mail odbiorcy
     * @param subject temat wiadomości
     * @param body    treść wiadomości
     */
    public static void sendEmail(String toEmail, String subject, String body) {
        String filePath = "PASS.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String fromEmail = reader.readLine();
            String password = reader.readLine();

            if (fromEmail == null || password == null) {
                System.out.println("Plik PASS.txt jest niekompletny.");
                return;
            }

            EmailSender.sendEmail(toEmail, fromEmail, password, subject, body);
            System.out.println("E-mail został wysłany!");

        } catch (IOException e) {
            System.out.println("Błąd podczas odczytu pliku PASS.txt.");
            e.printStackTrace();
        } catch (MessagingException e) {
            System.out.println("Błąd podczas wysyłania e-maila.");
            e.printStackTrace();
        }
    }

    /**
     * Generuje losowy kod o podanej długości.
     *
     * @param length długość kodu
     * @return losowy kod
     */
    private static String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
}