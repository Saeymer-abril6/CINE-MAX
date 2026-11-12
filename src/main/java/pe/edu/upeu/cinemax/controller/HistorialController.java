package pe.edu.upeu.cinemax.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import pe.edu.upeu.cinemax.components.Dialogos;
import pe.edu.upeu.cinemax.exception.BusinessException;
import pe.edu.upeu.cinemax.model.Venta;
import pe.edu.upeu.cinemax.model.VentaDetalle;
import pe.edu.upeu.cinemax.service.IVentaService;
import pe.edu.upeu.cinemax.util.Formato;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HistorialController {

    private final IVentaService service;

    @FXML private TextField txtBuscarVenta;
    @FXML private Label lblConteoVentas;
    @FXML private Label lblResumen;
    @FXML private Label lblMensajeHistorial;
    @FXML private TableView<Venta> tblVentas;
    @FXML private TableColumn<Venta, Long> colVentaId;
    @FXML private TableColumn<Venta, String> colVentaCliente;
    @FXML private TableColumn<Venta, LocalDate> colVentaFecha;
    @FXML private TableColumn<Venta, BigDecimal> colVentaTotal;

    @FXML private TableView<VentaDetalle> tblHistorialDetalle;
    @FXML private TableColumn<VentaDetalle, String> colHistProducto;
    @FXML private TableColumn<VentaDetalle, String> colHistFuncion;
    @FXML private TableColumn<VentaDetalle, Integer> colHistCantidad;
    @FXML private TableColumn<VentaDetalle, BigDecimal> colHistImporte;

    private final ObservableList<Venta> datos = FXCollections.observableArrayList();
    private FilteredList<Venta> filtrados;

    public HistorialController(IVentaService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        colVentaId.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colVentaCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colVentaFecha.setCellValueFactory(new PropertyValueFactory<>("fechaVenta"));
        colVentaTotal.setCellValueFactory(new PropertyValueFactory<>("precioTotal"));
        colVentaTotal.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(vacio || valor == null ? null : Formato.dinero(valor));
            }
        });
        colVentaFecha.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(vacio || valor == null ? null : Formato.fecha(valor));
            }
        });

        colHistProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colHistFuncion.setCellValueFactory(new PropertyValueFactory<>("funcionTexto"));
        colHistCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colHistImporte.setCellValueFactory(new PropertyValueFactory<>("netoTotal"));
        colHistImporte.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(vacio || valor == null ? null : Formato.dinero(valor));
            }
        });

        filtrados = new FilteredList<>(datos, v -> true);
        tblVentas.setItems(filtrados);
        tblVentas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tblHistorialDetalle.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        txtBuscarVenta.textProperty().addListener((o, a, b) -> aplicarFiltro());
        tblVentas.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> {
            if (b == null) {
                limpiarDetalle();
            } else {
                cargarDetalle(b);
            }
        });

        cargar();
        limpiarDetalle();
    }

    private void cargar() {
        datos.setAll(service.findAll());
        aplicarFiltro();
    }

    private void aplicarFiltro() {
        if (filtrados == null) return;
        String texto = Formato.normalizar(txtBuscarVenta.getText());
        filtrados.setPredicate(v -> texto.isEmpty()
                || Formato.normalizar(v.getNombreCliente()).contains(texto)
                || Formato.normalizar(String.valueOf(v.getIdVenta())).contains(texto));

        BigDecimal total = filtrados.stream()
                .map(Venta::getPrecioTotal)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblConteoVentas.setText(filtrados.size() + " ventas");
        lblResumen.setText("Total vendido: " + Formato.dinero(total));
    }

    private void cargarDetalle(Venta venta) {
        tblHistorialDetalle.setItems(FXCollections.observableArrayList(
                service.findDetalles(venta.getIdVenta())));
    }

    private void limpiarDetalle() {
        tblHistorialDetalle.setItems(FXCollections.observableArrayList());
    }

    @FXML
    private void onRefrescar() {
        cargar();
        limpiarDetalle();
        mensaje("", false);
    }

    @FXML
    private void onEliminarVenta() {
        Venta seleccionada = tblVentas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mensaje("Selecciona una venta de la tabla.", true);
            return;
        }
        if (!Dialogos.confirmar("Eliminar venta",
                "¿Seguro que deseas eliminar la venta V" + seleccionada.getIdVenta()
                        + "? El stock de los productos será devuelto.")) {
            return;
        }
        try {
            service.eliminar(seleccionada.getIdVenta());
            cargar();
            limpiarDetalle();
            mensaje("Venta eliminada y stock actualizado.", false);
        } catch (BusinessException e) {
            mensaje(e.getMessage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            mensaje("No se pudo eliminar la venta.", true);
        }
    }

    private void mensaje(String texto, boolean error) {
        lblMensajeHistorial.setText(texto == null ? "" : texto);
        lblMensajeHistorial.getStyleClass().removeAll("error", "exito");
        if (texto != null && !texto.isBlank()) {
            lblMensajeHistorial.getStyleClass().add(error ? "error" : "exito");
        }
    }
}
