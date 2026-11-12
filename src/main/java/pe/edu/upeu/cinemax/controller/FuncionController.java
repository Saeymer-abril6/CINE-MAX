package pe.edu.upeu.cinemax.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import pe.edu.upeu.cinemax.components.Dialogos;
import pe.edu.upeu.cinemax.enums.EstadoFuncion;
import pe.edu.upeu.cinemax.exception.BusinessException;
import pe.edu.upeu.cinemax.model.Funcion;
import pe.edu.upeu.cinemax.model.Pelicula;
import pe.edu.upeu.cinemax.model.Sala;
import pe.edu.upeu.cinemax.service.IFuncionService;
import pe.edu.upeu.cinemax.service.IPeliculaService;
import pe.edu.upeu.cinemax.service.ISalaService;
import pe.edu.upeu.cinemax.util.Formato;

import java.time.LocalDate;
import java.time.LocalTime;

public class FuncionController {

    private final IFuncionService service;
    private final IPeliculaService peliculaService;
    private final ISalaService salaService;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbxFiltroEstado;
    @FXML private Label lblConteo;
    @FXML private TableView<Funcion> tblFunciones;
    @FXML private TableColumn<Funcion, String> colPelicula;
    @FXML private TableColumn<Funcion, String> colSala;
    @FXML private TableColumn<Funcion, LocalDate> colFecha;
    @FXML private TableColumn<Funcion, LocalTime> colHora;
    @FXML private TableColumn<Funcion, EstadoFuncion> colEstado;

    @FXML private Label lblFormTitulo;
    @FXML private ComboBox<Pelicula> cbxPelicula;
    @FXML private ComboBox<Sala> cbxSala;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHora;
    @FXML private ComboBox<EstadoFuncion> cbxEstado;
    @FXML private Label lblMensaje;

    private final ObservableList<Funcion> datos = FXCollections.observableArrayList();
    private FilteredList<Funcion> filtrados;
    private Long idEditando;

    public FuncionController(IFuncionService service,
                             IPeliculaService peliculaService,
                             ISalaService salaService) {
        this.service = service;
        this.peliculaService = peliculaService;
        this.salaService = salaService;
    }

    @FXML
    public void initialize() {
        colPelicula.setCellValueFactory(new PropertyValueFactory<>("nombrePelicula"));
        colSala.setCellValueFactory(new PropertyValueFactory<>("nombreSala"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colFecha.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(vacio || valor == null ? null : Formato.fecha(valor));
            }
        });
        colHora.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(LocalTime valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(vacio || valor == null ? null : Formato.hora(valor));
            }
        });

        filtrados = new FilteredList<>(datos, f -> true);
        tblFunciones.setItems(filtrados);
        tblFunciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        cbxFiltroEstado.getItems().setAll("Todos", "Programada", "Finalizada");
        cbxFiltroEstado.getSelectionModel().selectFirst();
        cbxEstado.getItems().setAll(EstadoFuncion.values());
        cbxEstado.getSelectionModel().selectFirst();
        cbxPelicula.setItems(FXCollections.observableArrayList(peliculaService.findAll()));
        cbxSala.setItems(FXCollections.observableArrayList(salaService.findAll()));
        dpFecha.setValue(LocalDate.now());

        txtBuscar.textProperty().addListener((o, a, b) -> aplicarFiltro());
        cbxFiltroEstado.valueProperty().addListener((o, a, b) -> aplicarFiltro());
        tblFunciones.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> {
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
        filtrados.setPredicate(f -> {
            boolean coincideTexto = texto.isEmpty()
                    || Formato.normalizar(f.getNombrePelicula()).contains(texto)
                    || Formato.normalizar(f.getNombreSala()).contains(texto);
            boolean coincideEstado = estado == null || estado.startsWith("Todos")
                    || f.getEstado().getDescripcion().equalsIgnoreCase(estado);
            return coincideTexto && coincideEstado;
        });
        lblConteo.setText(filtrados.size() + " registros");
    }

    private void cargarFormulario(Funcion f) {
        idEditando = f.getIdFuncion();
        seleccionarPelicula(f.getIdPelicula());
        seleccionarSala(f.getIdSala());
        dpFecha.setValue(f.getFecha());
        txtHora.setText(Formato.hora(f.getHora()));
        cbxEstado.setValue(f.getEstado());
        lblFormTitulo.setText("Editar función");
        mensaje("", false);
    }

    private void seleccionarPelicula(Long id) {
        for (Pelicula p : cbxPelicula.getItems()) {
            if (p.getIdPelicula().equals(id)) {
                cbxPelicula.setValue(p);
                return;
            }
        }
    }

    private void seleccionarSala(Long id) {
        for (Sala s : cbxSala.getItems()) {
            if (s.getIdSala().equals(id)) {
                cbxSala.setValue(s);
                return;
            }
        }
    }

    @FXML
    private void onNuevo() {
        limpiarFormulario();
        mensaje("", false);
    }

    @FXML
    private void onGuardar() {
        LocalTime hora = Formato.hora(txtHora.getText());
        if (hora == null) {
            mensaje("Escribe la hora en formato HH:mm (ej. 19:30).", true);
            return;
        }

        Pelicula pelicula = cbxPelicula.getValue();
        Sala sala = cbxSala.getValue();
        Funcion funcion = Funcion.builder()
                .idPelicula(pelicula == null ? null : pelicula.getIdPelicula())
                .idSala(sala == null ? null : sala.getIdSala())
                .fecha(dpFecha.getValue())
                .hora(hora)
                .estado(cbxEstado.getValue() == null ? EstadoFuncion.PROGRAMADA : cbxEstado.getValue())
                .build();
        try {
            if (idEditando == null) {
                service.save(funcion);
                mensaje("Función registrada correctamente.", false);
            } else {
                service.update(idEditando, funcion);
                mensaje("Función actualizada correctamente.", false);
            }
            cargar();
            limpiarFormulario();
        } catch (BusinessException e) {
            mensaje(e.getMessage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            mensaje("No se pudo guardar la función.", true);
        }
    }

    @FXML
    private void onCancelar() {
        limpiarFormulario();
        mensaje("", false);
    }

    @FXML
    private void onEliminar() {
        Funcion seleccionada = tblFunciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mensaje("Selecciona una función de la tabla.", true);
            return;
        }
        if (!Dialogos.confirmar("Eliminar función",
                "¿Seguro que deseas eliminar la función de " + seleccionada.getNombrePelicula() + "?")) {
            return;
        }
        try {
            service.delete(seleccionada.getIdFuncion());
            mensaje("Función eliminada.", false);
            cargar();
            limpiarFormulario();
        } catch (BusinessException e) {
            mensaje(e.getMessage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            mensaje("No se pudo eliminar la función.", true);
        }
    }

    private void limpiarFormulario() {
        idEditando = null;
        tblFunciones.getSelectionModel().clearSelection();
        cbxPelicula.getSelectionModel().clearSelection();
        cbxSala.getSelectionModel().clearSelection();
        dpFecha.setValue(LocalDate.now());
        txtHora.clear();
        cbxEstado.getSelectionModel().selectFirst();
        lblFormTitulo.setText("Nueva función");
    }

    private void mensaje(String texto, boolean error) {
        lblMensaje.setText(texto == null ? "" : texto);
        lblMensaje.getStyleClass().removeAll("error", "exito");
        if (texto != null && !texto.isBlank()) {
            lblMensaje.getStyleClass().add(error ? "error" : "exito");
        }
    }
}
