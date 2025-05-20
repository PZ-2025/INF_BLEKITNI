import org.example.database.WorkloadRepository;
import pdf.WorkloadReportGenerator;

import java.time.LocalDate;
import java.util.List;

public class TestWorkloadReportGenerator {

    public static void main(String[] args) {
        try (WorkloadRepository repo = new WorkloadRepository()) {

            LocalDate start = LocalDate.of(2025, 4, 1);
            LocalDate end   = LocalDate.of(2025, 4, 30);

            List<WorkloadReportGenerator.EmployeeWorkload> data =
                    repo.getWorkloadData(start, end);

            WorkloadReportGenerator generator = new WorkloadReportGenerator();
            generator.setWorkloadData(data);

            generator.generateReport(
                    "output/workload-report.pdf",
                    start,
                    end,
                    List.of("Kierownik", "Kasjer", "Pracownik", "Logistyk")
            );

            System.out.println("Wygenerowano raport.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
