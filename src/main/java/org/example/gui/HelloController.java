package org.example.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.pdflib.StatsReportGenerator;
import org.example.pdflib.TaskReportGenerator;
import org.example.pdflib.NoDataException;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class HelloController {

    @FXML private Label welcomeText;
    @FXML private Button generateButton;

    @FXML
    private void initialize() {
        System.out.println("✅ Kontroler zainicjalizowany. generateButton = "
                + (generateButton != null));
        // dodatkowo podmień handler na czysto:
        generateButton.setOnAction(e -> {
            System.out.println("🔥 Ręczny handler wywołany!");
        });
    }

    @FXML
    private void onGenerateReportsClicked() {
        System.out.println("Klik! Generuję raporty...");      // <— debug
        try {
            // utwórz katalog reports, jeśli nie istnieje
            File dir = new File("reports");
            if (!dir.exists()) dir.mkdirs();

            // === Raport statystyk ===
            StatsReportGenerator statsGen = new StatsReportGenerator();
            statsGen.setLogoPath(getClass().getResource("/logo.png").getPath());
            statsGen.setTaskData(List.of(
                    new StatsReportGenerator.TaskRecord(
                            "Inwentaryzacja", "Magazyn",
                            StatsReportGenerator.Priority.MEDIUM,
                            LocalDate.now().minusDays(1),
                            LocalDate.now(),
                            "Jan Kowalski"
                    )
            ));
            File statsPdf = statsGen.generateReport(
                    "reports/stats_report.pdf",
                    LocalDate.now(),
                    StatsReportGenerator.PeriodType.DAILY,
                    List.of("Magazyn"),
                    List.of(StatsReportGenerator.Priority.MEDIUM)
            );

            // === Raport zadań ===
            TaskReportGenerator taskGen = new TaskReportGenerator();
            taskGen.setLogoPath(getClass().getResource("/logo.png").getPath());
            taskGen.setTaskData(List.of(
                    new TaskReportGenerator.TaskRecord(
                            "Sprzątanie",
                            LocalDate.now().plusDays(2),
                            null,
                            "Anna Nowak"
                    )
            ));
            File tasksPdf = taskGen.generateReport(
                    "reports/tasks_report.pdf",
                    TaskReportGenerator.PeriodType.LAST_WEEK,
                    List.of("W trakcie")
            );

            showAlert("Sukces",
                    "Wygenerowano:\n" +
                            statsPdf.getName() + "\n" +
                            tasksPdf.getName()
            );
        } catch (NoDataException nde) {
            showAlert("Brak danych", nde.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Błąd", ex.toString());
        }
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
