package org.example.pdflib;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsReportGenerator {

    private String logoPath;
    private List<TaskRecord> taskData;

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public void setTaskData(List<TaskRecord> taskData) {
        this.taskData = taskData;
    }

    /**
     * Generuje raport statystyk i zwraca plik PDF.
     */
    public File generateReport(String outputPath,
                               LocalDate date,
                               PeriodType period,
                               List<String> departments,
                               List<Priority> priorities) throws Exception {
        if (taskData == null || taskData.isEmpty()) {
            throw new NoDataException("Brak danych statystyk do wygenerowania raportu.");
        }

        Map<String, String> filters = new HashMap<>();
        filters.put("Data", date.toString());
        filters.put("Okres", period.name());
        filters.put("Departamenty", String.join(", ", departments));
        filters.put("Priorytety",
                priorities.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(", "))
        );

        return ReportGenerator.generate(
                outputPath,
                "Raport statystyk",
                filters,
                logoPath,
                taskData
        );
    }

    public static class TaskRecord {
        private String name;
        private String department;
        private Priority priority;
        private LocalDate startDate;
        private LocalDate endDate;
        private String assignedTo;

        public TaskRecord(String name,
                          String department,
                          Priority priority,
                          LocalDate startDate,
                          LocalDate endDate,
                          String assignedTo) {
            this.name = name;
            this.department = department;
            this.priority = priority;
            this.startDate = startDate;
            this.endDate = endDate;
            this.assignedTo = assignedTo;
        }
        // gettery/settery…
    }

    public enum PeriodType {
        DAILY, WEEKLY, MONTHLY
    }

    public enum Priority {
        LOW, MEDIUM, HIGH
    }
}
