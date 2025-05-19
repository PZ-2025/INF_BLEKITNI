package org.example.pdflib;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class ReportGenerator {

    public static File generate(String outputPath,
                                String title,
                                Map<String, String> filters,
                                String logoPath,
                                List<?> data) throws IOException {

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            // Użyj wbudowanej czcionki Courier (działa w PDFBox 3.0.5)
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.COURIER);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                // 1) Tytuł
                cs.beginText();
                cs.setFont(font, 18);
                cs.newLineAtOffset(50, 780);
                cs.showText(title);
                cs.endText();

                // 2) Logo
                if (logoPath != null && !logoPath.isEmpty()) {
                    PDImageXObject logo = PDImageXObject.createFromFile(logoPath, doc);
                    float scale = 100f / logo.getWidth();
                    cs.drawImage(logo, 450, 720, logo.getWidth() * scale, logo.getHeight() * scale);
                }

                // 3) Filtry
                float y = 750;
                cs.beginText();
                cs.setFont(font, 12);
                cs.newLineAtOffset(50, y);
                for (Map.Entry<String, String> e : filters.entrySet()) {
                    cs.showText(e.getKey() + ": " + e.getValue());
                    cs.newLineAtOffset(0, -15);
                }
                cs.endText();

                // 4) Dane
                cs.beginText();
                cs.setFont(font, 12);
                cs.newLineAtOffset(50, y - filters.size() * 15 - 20);
                cs.showText("Liczba wierszy danych: " + data.size());
                cs.endText();
            }

            doc.save(outputPath);
        }

        return new File(outputPath);
    }
}
