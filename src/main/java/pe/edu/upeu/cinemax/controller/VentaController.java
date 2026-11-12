package pe.edu.upeu.cinemax.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import pe.edu.upeu.cinemax.exception.BusinessException;
import pe.edu.upeu.cinemax.model.Cliente;
import pe.edu.upeu.cinemax.model.Funcion;
import pe.edu.upeu.cinemax.model.Producto;
import pe.edu.upeu.cinemax.model.Venta;
import pe.edu.upeu.cinemax.model.VentaDetalle;
import pe.edu.upeu.cinemax.service.IClienteService;
import pe.edu.upeu.cinemax.service.IFuncionService;
import pe.edu.upeu.cinemax.service.IProductoService;
import pe.edu.upeu.cinemax.service.IVentaService;
import pe.edu.upeu.cinemax.util.Formato;
import pe.edu.upeu.cinemax.util.Sesion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;

public class VentaController {

    private static final BigDecimal UNO_MAS_IGV = new BigDecimal("1.18");

    private final IVentaService ventaService;
    private final IClienteService clienteService;
    private final IProductoService productoService;
    private final IFuncionService funcionService;

    @FXML private ComboBox<Cliente> cbxCliente;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<Producto> cbxProducto;
    @FXML private ComboBox<Funcion> cbxFuncion;
    @FXML private TextField txtCantidad;

    @FXML private TableView<VentaDetalle> tblDetalle;
    @FXML private TableColumn<VentaDetalle, String> colDetProducto;
    @FXML private TableColumn<VentaDetalle, String> colDetFuncion;
    @FXML private TableColumn<VentaDetalle, Integer> colDetCantidad;
    @FXML private TableColumn<VentaDetalle, BigDecimal> colDetPrecio;
    @FXML private TableColumn<VentaDetalle, BigDecimal> colDetImporte;
    @FXML private TableColumn<VentaDetalle, BigDecimal> colDetUtilidad;

    @FXML private Label lblNeto;
    @FXML private Label lblIgv;
    @FXML private Label lblTotal;
    @FXML private Label lblMensaje;

    private final ObservableList<VentaDetalle> detalles = FXCollections.observableArrayList();

    public VentaController(IVentaService ventaService,
                           IClienteService clienteService,
                           IProductoService productoService,
                           IFuncionService funcionService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.funcionService = funcionService;
    }

    @FXML
    public void initialize() {
        colDetProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colDetFuncion.setCellValueFactory(new PropertyValueFactory<>("funcionTexto"));
        colDetCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colDetPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnit"));
        colDetImporte.setCellValueFactory(new PropertyValueFactory<>("netoTotal"));
        colDetUtilidad.setCellValueFactory(new PropertyValueFactory<>("utilidad"));
        colDetPrecio.setCellFactory(c -> celdaDinero());
        colDetImporte.setCellFactory(c -> celdaDinero());
        colDetUtilidad.setCellFactory(c -> celdaDinero());

        tblDetalle.setItems(detalles);
        tblDetalle.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        cbxCliente.setItems(FXCollections.observableArrayList(clienteService.findAll()));
        cbxProducto.setItems(FXCollections.observableArrayList(productoService.findActivos()));
        cbxFuncion.setItems(FXCollections.observableArrayList(funcionService.findAll()));
        cbxFuncion.setDisable(true);

        dpFecha.setValue(LocalDate.now());
        txtCantidad.setText("1");

        cbxProducto.valueProperty().addListener((o, a, b) -> actualizarFuncion(b));

        actualizarTotales();
        mensaje("", false);
    }

    private TableCell<VentaDetalle, BigDecimal> celdaDinero() {
        return new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(vacio || valor == null ? null : Formato.dinero(valor));
            }
        };
    }

    private void actualizarFuncion(Producto producto) {
        boolean esEntrada = esEntrada(producto);
        cbxFuncion.setDisable(!esEntrada);
        if (!esEntrada) {
            cbxFuncion.getSelectionModel().clearSelection();
        }
    }

    private boolean esEntrada(Producto producto) {
        return producto != null && "Entradas".equalsIgnoreCase(producto.getNombreCategoria());
    }

    @FXML
    private void onAgregarDetalle() {
        Producto producto = cbxProducto.getValue();
        if (producto == null) {
            mensaje("Selecciona un producto o entrada.", true);
            return;
        }

        int cantidad = Formato.entero(txtCantidad.getText());
        if (cantidad <= 0) {
            mensaje("La cantidad debe ser mayor a 0.", true);
            return;
        }

        Funcion funcion = cbxFuncion.getValue();
        if (esEntrada(producto) && funcion == null) {
            mensaje("Selecciona la función para la entrada.", true);
            return;
        }
        if (!esEntrada(producto)) {
            funcion = null;
        }

        int enCarrito = detalles.stream()
                .filter(d -> d.getIdProducto().equals(producto.getIdProducto()))
                .mapToInt(VentaDetalle::getCantidad)
                .sum();
        if (producto.isControlaStock() && enCarrito + cantidad > producto.getStock()) {
            mensaje("Stock insuficiente para " + producto.getNombre()
                    + ". Disponible: " + producto.getStock() + ".", true);
            return;
        }

        BigDecimal precio = producto.getPrecioVenta();
        BigDecimal neto = precio.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal utilidad = precio.subtract(producto.getCosto())
                .multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);

        detalles.add(VentaDetalle.builder()
                .idProducto(producto.getIdProducto())
                .nombreProducto(producto.getNombre())
                .idFuncion(funcion == null ? null : funcion.getIdFuncion())
                .descripcionFuncion(funcion == null ? null : descripcionFuncion(funcion))
                .precioUnit(precio)
                .porceUtil(producto.getPorceUtil())
                .cantidad(cantidad)
                .netoTotal(neto)
                .utilidad(utilidad)
                .controlaStock(producto.isControlaStock())
                .build());

        actualizarTotales();
        mensaje("", false);
        txtCantidad.setText("1");
    }

    private String descripcionFuncion(Funcion f) {
        return "F" + f.getIdFuncion() + " · " + f.getNombrePelicula()
                + " · " + Formato.fecha(f.getFecha()) + " " + Formato.hora(f.getHora());
    }

    @FXML
    private void onQuitarDetalle() {
        VentaDetalle seleccionado = tblDetalle.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensaje("Selecciona una línea del detalle.", true);
            return;
        }
        detalles.remove(seleccionado);
        actualizarTotales();
        mensaje("", false);
    }

    private void actualizarTotales() {
        BigDecimal total = detalles.stream()
                .map(VentaDetalle::getNetoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal neto = total.divide(UNO_MAS_IGV, 2, RoundingMode.HALF_UP);
        BigDecimal igv = total.subtract(neto).setScale(2, RoundingMode.HALF_UP);

        lblNeto.setText(Formato.dinero(neto));
        lblIgv.setText(Formato.dinero(igv));
        lblTotal.setText(Formato.dinero(total));
    }

    @FXML
    private void onRegistrar() {
        if (cbxCliente.getValue() == null) {
            mensaje("Selecciona un cliente.", true);
            return;
        }
        if (detalles.isEmpty()) {
            mensaje("Agrega al menos un producto o entrada.", true);
            return;
        }

        Venta venta = Venta.builder()
                .dniCliente(cbxCliente.getValue().getDni())
                .idUsuario(Sesion.getUsuario() == null ? null : Sesion.getUsuario().getIdUsuario())
                .fechaVenta(dpFecha.getValue())
                .build();

        try {
            Venta guardada = ventaService.registrar(venta, new ArrayList<>(detalles));
            limpiarFormulario();
            cbxProducto.setItems(FXCollections.observableArrayList(productoService.findActivos()));
            mensaje("Venta registrada con el código V" + guardada.getIdVenta()
                    + " por " + Formato.dinero(guardada.getPrecioTotal()) + ".", false);
        } catch (BusinessException e) {
            mensaje(e.getMessage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            mensaje("No se pudo registrar la venta.", true);
        }
    }

    @FXML
    private void onLimpiar() {
        limpiarFormulario();
        mensaje("", false);
    }

    private void limpiarFormulario() {
        detalles.clear();
        tblDetalle.getSelectionModel().clearSelection();
        cbxCliente.getSelectionModel().clearSelection();
        cbxProducto.getSelectionModel().clearSelection();
        cbxFuncion.getSelectionModel().clearSelection();
        cbxFuncion.setDisable(true);
        dpFecha.setValue(LocalDate.now());
        txtCantidad.setText("1");
        actualizarTotales();
    }

    private void mensaje(String texto, boolean error) {
        lblMensaje.setText(texto == null ? "" : texto);
        lblMensaje.getStyleClass().removeAll("error", "exito");
        if (texto != null && !texto.isBlank()) {
            lblMensaje.getStyleClass().add(error ? "error" : "exito");
        }
    }
}
