package pe.edu.upeu.cinemax;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pe.edu.upeu.cinemax.config.AppContext;
import pe.edu.upeu.cinemax.config.DatabaseConfig;

/**
 * Punto de entrada de CineMax.
 *
 * <p>Esta clase NO extiende de {@link Application} a propósito: así el botón
 * "Run" del IDE funciona sin el error
 * <em>"JavaFX runtime components are missing"</em>. La ventana JavaFX real
 * está en la clase interna {@link Ventana}.</p>
 */
public class App {

    private static Stage ventana;

    /** Ventana JavaFX: abre el login y cierra la base al salir. */
    public static class Ventana extends Application {

        @Override
        public void start(Stage stage) {
            ventana = stage;
            AppContext.getInstance();
            mostrarLogin();
        }

        @Override
        public void stop() {
            DatabaseConfig.shutdown();
        }
    }

    public static void main(String[] args) {
        Application.launch(Ventana.class, args);
    }

    public static void mostrarLogin() {
        cargar("/view/login.fxml", "CineMax · Iniciar sesión", 440, 440, false);
    }

    public static void mostrarPrincipal() {
        cargar("/view/main.fxml", "CineMax · Ventas de entradas y dulcería", 1180, 730, true);
    }

    private static void cargar(String vista, String titulo, double ancho, double alto, boolean redimensionable) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(vista));
            loader.setControllerFactory(AppContext.getInstance()::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root, ancho, alto);
            scene.getStylesheets().add(App.class.getResource("/css/app.css").toExternalForm());

            ventana.setTitle(titulo);
            ventana.setScene(scene);
            ventana.setResizable(redimensionable);
            ventana.setMinWidth(redimensionable ? 950 : 0);
            ventana.setMinHeight(redimensionable ? 620 : 0);
            ventana.centerOnScreen();
            ventana.show();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo abrir la vista " + vista, e);
        }
    }
}
