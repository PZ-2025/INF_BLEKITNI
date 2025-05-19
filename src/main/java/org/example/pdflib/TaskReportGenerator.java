package org.example.pdflib;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskReportGenerator {

    private String logoPath;
    private List<TaskRecord> taskData;

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public void setTaskData(List<TaskRecord> taskData) {
        this.taskData = taskData;
    }

    /**
     * Generuje raport zadań i zwraca plik PDF.
     */
    public File generateReport(String outputPath,
                               PeriodType period,
                               List<String> statuses) throws Exception {
        if (taskData == null || taskData.isEmpty()) {
            throw new NoDataException("Brak danych zadań do wygenerowania raportu.");
        }

        Map<String, String> filters = new HashMap<>();
        filters.put("Okres", period.name());
        filters.put("Statusy", String.join(", ", statuses));

        return ReportGenerator.generate(
                outputPath,
                "Raport zadań",
                filters,
                logoPath,
                taskData
        );
    }

    public static class TaskRecord {
        private String name;
        private LocalDate dueDate;
        private Priority priority;
        private String assignedTo;

        public TaskRecord(String name,
                          LocalDate dueDate,
                          Priority priority,
                          String assignedTo) {
            this.name = name;
            this.dueDate = dueDate;
            this.priority = priority;
            this.assignedTo = assignedTo;
        }
        // gettery/settery…
    }

    public enum PeriodType {
        DAILY, WEEKLY, LAST_WEEK
    }

    public enum Priority {
        LOW, MEDIUM, HIGH
    }
}
