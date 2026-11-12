package pe.edu.upeu.cinemax.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import pe.edu.upeu.cinemax.components.Dialogos;
import pe.edu.upeu.cinemax.exception.BusinessException;
import pe.edu.upeu.cinemax.model.Sala;
import pe.edu.upeu.cinemax.service.ISalaService;
import pe.edu.upeu.cinemax.util.Formato;

public class SalaController {

    private final ISalaService service;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbxFiltroTipo;
    @FXML private Label lblConteo;
    @FXML private TableView<Sala> tblSalas;
    @FXML private TableColumn<Sala, String> colNombre;
    @FXML private TableColumn<Sala, String> colTipo;
    @FXML private TableColumn<Sala, Integer> colCapacidad;

    @FXML private Label lblFormTitulo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cbxTipo;
    @FXML private TextField txtCapacidad;
    @FXML private Label lblMensaje;

    private final ObservableList<Sala> datos = FXCollections.observableArrayList();
    private FilteredList<Sala> filtrados;
    private Long idEditando;

    public SalaController(ISalaService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));

        filtrados = new FilteredList<>(datos, s -> true);
        tblSalas.setItems(filtrados);
        tblSalas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        cbxFiltroTipo.getItems().setAll("Todos", "2D", "3D", "VIP");
        cbxFiltroTipo.getSelectionModel().selectFirst();
        cbxTipo.getItems().setAll("2D", "3D", "VIP");
        cbxTipo.getSelectionModel().selectFirst();

        txtBuscar.textProperty().addListener((o, a, b) -> aplicarFiltro());
        cbxFiltroTipo.valueProperty().addListener((o, a, b) -> aplicarFiltro());
        tblSalas.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> {
            if (b != null) cargarFormulario(b);
        });

        cargar();
        limpiarFormulario();
    }

    private void cargar() {
        datos.setAll(service.findAll());
        aplicarFiltro();
    }

    private void aplicarFiltro() {
        if (filtrados == null) return;
        String texto = Formato.normalizar(txtBuscar.getText());
        String tipo = cbxFiltroTipo.getValue();
        filtrados.setPredicate(s -> {
            boolean coincideTexto = texto.isEmpty() || Formato.normalizar(s.getNombre()).contains(texto);
            boolean coincideTipo = tipo == null || tipo.startsWith("Todos") || tipo.equalsIgnoreCase(s.getTipo());
            return coincideTexto && coincideTipo;
        });
        lblConteo.setText(filtrados.size() + " registros");
    }

    private void cargarFormulario(Sala s) {
        idEditando = s.getIdSala();
        txtNombre.setText(s.getNombre());
        cbxTipo.setValue(s.getTipo());
        txtCapacidad.setText(String.valueOf(s.getCapacidad()));
        lblFormTitulo.setText("Editar sala");
        mensaje("", false);
    }

    @FXML
    private void onNuevo() {
        limpiarFormulario();
        mensaje("", false);
    }

    @FXML
    private void onGuardar() {
        Sala sala = Sala.builder()
                .nombre(txtNombre.getText())
                .tipo(cbxTipo.getValue())
                .capacidad(Formato.entero(txtCapacidad.getText()))
                .build();
        try {
            if (idEditando == null) {
                service.save(sala);
                mensaje("Sala registrada correctamente.", false);
            } else {
                service.update(idEditando, sala);
                mensaje("Sala actualizada correctamente.", false);
            }
            cargar();
            limpiarFormulario();
        } catch (BusinessException e) {
            mensaje(e.getMessage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            mensaje("No se pudo guardar la sala.", true);
        }
    }

    @FXML
    private void onCancelar() {
        limpiarFormulario();
        mensaje("", false);
    }

    @FXML
    private void onEliminar() {
        Sala seleccionada = tblSalas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mensaje("Selecciona una sala de la tabla.", true);
            return;
        }
        if (!Dialogos.confirmar("Eliminar sala",
                "¿Seguro que deseas eliminar " + seleccionada.getNombre() + "?")) {
            return;
        }
        try {
            service.delete(seleccionada.getIdSala());
            mensaje("Sala eliminada.", false);
            cargar();
            limpiarFormulario();
        } catch (BusinessException e) {
            mensaje(e.getMessage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            mensaje("No se pudo eliminar la sala.", true);
        }
    }

    private void limpiarFormulario() {
        idEditando = null;
        tblSalas.getSelectionModel().clearSelection();
        txtNombre.clear();
        cbxTipo.getSelectionModel().selectFirst();
        txtCapacidad.clear();
        lblFormTitulo.setText("Nueva sala");
    }

    private void mensaje(String texto, boolean error) {
        lblMensaje.setText(texto == null ? "" : texto);
        lblMensaje.getStyleClass().removeAll("error", "exito");
        if (texto != null && !texto.isBlank()) {
            lblMensaje.getStyleClass().add(error ? "error" : "exito");
        }
    }
}
