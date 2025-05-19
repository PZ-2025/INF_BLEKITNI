package org.example.pdflib;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gui/hello-view.fxml"));
        Parent root = loader.load();

        System.out.println("FXML wczytany: " + root.getClass().getSimpleName());

        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Generator Raportów");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
