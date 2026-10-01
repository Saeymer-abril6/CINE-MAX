package pe.edu.upeu.cinemax;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pe.edu.upeu.cinemax.controller.MainController;

public class CineMax extends Application {
    @Override
    public void start(Stage stage) {
        MainController controller = new MainController();
        Scene scene = new Scene(controller.crearVista(), 950, 620);
        stage.setTitle("CineMax");
        stage.setScene(scene);
        stage.setMinWidth(850);
        stage.setMinHeight(560);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
