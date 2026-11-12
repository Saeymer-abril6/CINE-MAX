package pe.edu.upeu.cinemax.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pe.edu.upeu.cinemax.App;
import pe.edu.upeu.cinemax.exception.BusinessException;
import pe.edu.upeu.cinemax.model.Usuario;
import pe.edu.upeu.cinemax.service.IUsuarioService;
import pe.edu.upeu.cinemax.util.Sesion;

import java.util.Optional;

public class LoginController {

    private final IUsuarioService service;

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;
    @FXML private Label lblError;

    public LoginController(IUsuarioService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        lblError.setText("");
        lblError.getStyleClass().removeAll("error", "exito");
        Platform.runLater(txtUsuario::requestFocus);
    }

    @FXML
    private void onIngresar() {
        lblError.setText("");
        try {
            Optional<Usuario> usuario = service.login(txtUsuario.getText(), txtClave.getText());
            if (usuario.isEmpty()) {
                lblError.setText("Usuario o clave incorrectos.");
                lblError.getStyleClass().setAll("error");
                txtClave.clear();
                txtClave.requestFocus();
                return;
            }
            Sesion.iniciar(usuario.get());
            App.mostrarPrincipal();
        } catch (BusinessException e) {
            lblError.setText(e.getMessage());
            lblError.getStyleClass().setAll("error");
        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("No se pudo conectar con la base de datos.");
            lblError.getStyleClass().setAll("error");
        }
    }

    @FXML
    private void onSalir() {
        Platform.exit();
    }
}
