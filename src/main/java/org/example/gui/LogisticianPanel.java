package org.example.gui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

/**
 * Klasa reprezentująca panel logistyka w aplikacji GUI.
 */
public class LogisticianPanel {

    private final BorderPane root;
    private final Stage primaryStage;
    private final LogisticianPanelController controller;

    public LogisticianPanel(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.controller   = new LogisticianPanelController(this);

        primaryStage.setTitle("Panel logistyka");
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(450);

        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: lightblue;");

        VBox menu = createMenu();
        root.setLeft(menu);

        // domyślnie pokazujemy raporty
        showInventoryReports();

        animateFadeIn(menu, 1000);
        animateSlideDown(menu, 800);

        primaryStage.setScene(new Scene(root, 700, 450));
        primaryStage.show();
    }

    private VBox createMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setAlignment(Pos.TOP_LEFT);
        menu.setStyle("-fx-background-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10;");

        // logo
        ImageView logo = new ImageView(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/logo.png"))
        ));
        logo.setFitWidth(100);
        logo.setPreserveRatio(true);

        Button invBtn    = createStyledButton("Zarządzanie magazynem");
        Button ordBtn    = createStyledButton("Zamówienia");
        Button repBtn    = createStyledButton("Raporty magazynowe");
        Button testBtn   = createStyledButton("Test raportu");              // nowy
        Button absenceBtn= createStyledButton("Wniosek o nieobecność");
        Button logoutBtn = createStyledButton("Wyloguj", "#E74C3C");

        // podpinamy akcje
        invBtn.setOnAction(e -> showInventoryManagement());
        ordBtn.setOnAction(e -> showOrdersPanel());
        repBtn.setOnAction(e -> showInventoryReports());
        testBtn.setOnAction(e-> runLibraryTest());                          // test biblioteki
        absenceBtn.setOnAction(e-> showAbsenceRequestForm());
        logoutBtn.setOnAction(e-> logout());

        menu.getChildren().addAll(
                logo,
                invBtn,
                ordBtn,
                repBtn,
                testBtn,       // wyświetli okno testu raportu
                absenceBtn,
                logoutBtn
        );
        return menu;
    }

    private Button createStyledButton(String text) {
        return createStyledButton(text, "#2980B9");
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;");
        btn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
            st.setToX(1.1); st.setToY(1.1); st.play();
        });
        btn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
            st.setToX(1); st.setToY(1); st.play();
        });
        return btn;
    }

    private void animateFadeIn(VBox v, int dur) {
        FadeTransition ft = new FadeTransition(Duration.millis(dur), v);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }
    private void animateSlideDown(VBox v, int dur) {
        TranslateTransition tt= new TranslateTransition(Duration.millis(dur), v);
        tt.setFromY(-50); tt.setToY(0); tt.setInterpolator(Interpolator.EASE_BOTH); tt.play();
    }

    /** Ustawia zawartość głównego obszaru. */
    public void setCenterPane(javafx.scene.layout.Pane pane) {
        root.setCenter(pane);
    }

    /** Dostęp do sceny (używane przez kontroler). */
    public Stage getPrimaryStage() {
        return primaryStage;
    }


    /* === Tutaj – wszystkie _delegaty_ do kontrolera === */

    /** Zarządzanie magazynem. */
    public void showInventoryManagement()     { controller.showInventoryManagement(); }

    /** Panel zamówień. */
    public void showOrdersPanel()             { controller.showOrdersPanel(); }

    /** Raporty magazynowe. */
    public void showInventoryReports()        { controller.showInventoryReports(); }

    /** Formularz nieobecności. */
    public void showAbsenceRequestForm()      { controller.showAbsenceRequestForm(); }

    /** Test integracji z biblioteką raportów. */
    public void runLibraryTest()              { controller.runLibraryTest(); }

    /** Wylogowanie / powrót do logowania. */
    public void logout()                      { controller.logout(); }
}
