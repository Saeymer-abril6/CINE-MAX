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
import pe.edu.upeu.cinemax.enums.EstadoPelicula;
import pe.edu.upeu.cinemax.exception.BusinessException;
import pe.edu.upeu.cinemax.model.Pelicula;
import pe.edu.upeu.cinemax.service.IPeliculaService;
import pe.edu.upeu.cinemax.util.Formato;

public class PeliculaController {

    private final IPeliculaService service;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbxFiltroEstado;
    @FXML private Label lblConteo;
    @FXML private TableView<Pelicula> tblPeliculas;
    @FXML private TableColumn<Pelicula, String> colTitulo;
    @FXML private TableColumn<Pelicula, String> colGenero;
    @FXML private TableColumn<Pelicula, Integer> colDuracion;
    @FXML private TableColumn<Pelicula, String> colClasificacion;
    @FXML private TableColumn<Pelicula, EstadoPelicula> colEstado;

    @FXML private Label lblFormTitulo;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtGenero;
    @FXML private TextField txtDuracion;
    @FXML private ComboBox<String> cbxClasificacion;
    @FXML private ComboBox<EstadoPelicula> cbxEstado;
    @FXML private Label lblMensaje;

    private final ObservableList<Pelicula> datos = FXCollections.observableArrayList();
    private FilteredList<Pelicula> filtrados;
    private Long idEditando;

    public PeliculaController(IPeliculaService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracionMin"));
        colClasificacion.setCellValueFactory(new PropertyValueFactory<>("clasificacion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        filtrados = new FilteredList<>(datos, p -> true);
        tblPeliculas.setItems(filtrados);
        tblPeliculas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        cbxFiltroEstado.getItems().setAll("Todos", "En cartelera", "Retirada");
        cbxFiltroEstado.getSelectionModel().selectFirst();
        cbxClasificacion.getItems().setAll("APT", "+14", "+18");
        cbxClasificacion.getSelectionModel().selectFirst();
        cbxEstado.getItems().setAll(EstadoPelicula.values());
        cbxEstado.getSelectionModel().selectFirst();

        txtBuscar.textProperty().addListener((o, a, b) -> aplicarFiltro());
        cbxFiltroEstado.valueProperty().addListener((o, a, b) -> aplicarFiltro());
        tblPeliculas.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> {
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
        String estado = cbxFiltroEstado.getValue();
        filtrados.setPredicate(p -> {
            boolean coincideTexto = texto.isEmpty()
                    || Formato.normalizar(p.getTitulo()).contains(texto)
                    || Formato.normalizar(p.getGenero()).contains(texto);
            boolean coincideEstado = estado == null || estado.startsWith("Todos")
                    || p.getEstado().getDescripcion().equalsIgnoreCase(estado);
            return coincideTexto && coincideEstado;
        });
        lblConteo.setText(filtrados.size() + " registros");
    }

    private void cargarFormulario(Pelicula p) {
        idEditando = p.getIdPelicula();
        txtTitulo.setText(p.getTitulo());
        txtGenero.setText(p.getGenero());
        txtDuracion.setText(String.valueOf(p.getDuracionMin()));
        cbxClasificacion.setValue(p.getClasificacion());
        cbxEstado.setValue(p.getEstado());
        lblFormTitulo.setText("Editar película");
        mensaje("", false);
    }

    @FXML
    private void onNuevo() {
        limpiarFormulario();
        mensaje("", false);
    }

    @FXML
    private void onGuardar() {
        Pelicula pelicula = Pelicula.builder()
                .titulo(txtTitulo.getText())
                .genero(txtGenero.getText())
                .duracionMin(Formato.entero(txtDuracion.getText()))
                .clasificacion(cbxClasificacion.getValue())
                .estado(cbxEstado.getValue() == null ? EstadoPelicula.EN_CARTELERA : cbxEstado.getValue())
                .build();
        try {
            if (idEditando == null) {
                service.save(pelicula);
                mensaje("Película registrada correctamente.", false);
            } else {
                service.update(idEditando, pelicula);
                mensaje("Película actualizada correctamente.", false);
            }
            cargar();
            limpiarFormulario();
        } catch (BusinessException e) {
            mensaje(e.getMessage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            mensaje("No se pudo guardar la película.", true);
        }
    }

    @FXML
    private void onCancelar() {
        limpiarFormulario();
        mensaje("", false);
    }

    @FXML
    private void onEliminar() {
        Pelicula seleccionada = tblPeliculas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mensaje("Selecciona una película de la tabla.", true);
            return;
        }
        if (!Dialogos.confirmar("Eliminar película",
                "¿Seguro que deseas eliminar " + seleccionada.getTitulo() + "?")) {
            return;
        }
        try {
            service.delete(seleccionada.getIdPelicula());
            mensaje("Película eliminada.", false);
            cargar();
            limpiarFormulario();
        } catch (BusinessException e) {
            mensaje(e.getMessage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            mensaje("No se pudo eliminar la película.", true);
        }
    }

    private void limpiarFormulario() {
        idEditando = null;
        tblPeliculas.getSelectionModel().clearSelection();
        txtTitulo.clear();
        txtGenero.clear();
        txtDuracion.clear();
        cbxClasificacion.getSelectionModel().selectFirst();
        cbxEstado.getSelectionModel().selectFirst();
        lblFormTitulo.setText("Nueva película");
    }

    private void mensaje(String texto, boolean error) {
        lblMensaje.setText(texto == null ? "" : texto);
        lblMensaje.getStyleClass().removeAll("error", "exito");
        if (texto != null && !texto.isBlank()) {
            lblMensaje.getStyleClass().add(error ? "error" : "exito");
        }
    }
}
