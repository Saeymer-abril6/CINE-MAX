package pe.edu.upeu.cinemax.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import pe.edu.upeu.cinemax.components.Dialogos;
import pe.edu.upeu.cinemax.exception.BusinessException;
import pe.edu.upeu.cinemax.model.Cliente;
import pe.edu.upeu.cinemax.service.IClienteService;
import pe.edu.upeu.cinemax.util.Formato;

public class ClienteController {

    private final IClienteService service;

    @FXML private TextField txtBuscar;
    @FXML private Label lblConteo;
    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, String> colDni;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colDireccion;
    @FXML private TableColumn<Cliente, String> colCelular;

    @FXML private Label lblFormTitulo;
    @FXML private TextField txtDni;
    @FXML private TextField txtNombreCliente;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtCelular;
    @FXML private Label lblMensaje;

    private final ObservableList<Cliente> datos = FXCollections.observableArrayList();
    private FilteredList<Cliente> filtrados;
    private String dniEditando;

    public ClienteController(IClienteService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colCelular.setCellValueFactory(new PropertyValueFactory<>("celular"));

        filtrados = new FilteredList<>(datos, c -> true);
        tblClientes.setItems(filtrados);
        tblClientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        txtBuscar.textProperty().addListener((o, a, b) -> aplicarFiltro());
        tblClientes.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> {
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
        filtrados.setPredicate(c -> texto.isEmpty()
                || Formato.normalizar(c.getDni()).contains(texto)
                || Formato.normalizar(c.getNombre()).contains(texto));
        lblConteo.setText(filtrados.size() + " registros");
    }

    private void cargarFormulario(Cliente c) {
        dniEditando = c.getDni();
        txtDni.setText(c.getDni());
        txtDni.setDisable(true);
        txtNombreCliente.setText(c.getNombre());
        txtDireccion.setText(c.getDireccion());
        txtCelular.setText(c.getCelular());
        lblFormTitulo.setText("Editar cliente");
        mensaje("", false);
    }

    @FXML
    private void onNuevo() {
        limpiarFormulario();
        mensaje("", false);
    }

    @FXML
    private void onGuardar() {
        Cliente cliente = Cliente.builder()
                .dni(txtDni.getText())
                .nombre(txtNombreCliente.getText())
                .direccion(txtDireccion.getText())
                .celular(txtCelular.getText())
                .build();
        try {
            if (dniEditando == null) {
                service.save(cliente);
                mensaje("Cliente registrado correctamente.", false);
            } else {
                service.update(dniEditando, cliente);
                mensaje("Cliente actualizado correctamente.", false);
            }
            cargar();
            limpiarFormulario();
        } catch (BusinessException e) {
            mensaje(e.getMessage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            mensaje("No se pudo guardar el cliente.", true);
        }
    }

    @FXML
    private void onCancelar() {
        limpiarFormulario();
        mensaje("", false);
    }

    @FXML
    private void onEliminar() {
        Cliente seleccionado = tblClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensaje("Selecciona un cliente de la tabla.", true);
            return;
        }
        if (!Dialogos.confirmar("Eliminar cliente",
                "¿Seguro que deseas eliminar a " + seleccionado.getNombre() + "?")) {
            return;
        }
        try {
            service.delete(seleccionado.getDni());
            mensaje("Cliente eliminado.", false);
            cargar();
            limpiarFormulario();
        } catch (BusinessException e) {
            mensaje(e.getMessage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            mensaje("No se pudo eliminar el cliente.", true);
        }
    }

    private void limpiarFormulario() {
        dniEditando = null;
        tblClientes.getSelectionModel().clearSelection();
        txtDni.clear();
        txtDni.setDisable(false);
        txtNombreCliente.clear();
        txtDireccion.clear();
        txtCelular.clear();
        lblFormTitulo.setText("Nuevo cliente");
    }

    private void mensaje(String texto, boolean error) {
        lblMensaje.setText(texto == null ? "" : texto);
        lblMensaje.getStyleClass().removeAll("error", "exito");
        if (texto != null && !texto.isBlank()) {
            lblMensaje.getStyleClass().add(error ? "error" : "exito");
        }
    }
}
