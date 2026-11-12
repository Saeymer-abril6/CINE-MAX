package pe.edu.upeu.cinemax.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import pe.edu.upeu.cinemax.components.Dialogos;
import pe.edu.upeu.cinemax.enums.EstadoProducto;
import pe.edu.upeu.cinemax.exception.BusinessException;
import pe.edu.upeu.cinemax.model.Categoria;
import pe.edu.upeu.cinemax.model.Producto;
import pe.edu.upeu.cinemax.model.UnidadMedida;
import pe.edu.upeu.cinemax.service.IProductoService;
import pe.edu.upeu.cinemax.util.Formato;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProductoController {

    private final IProductoService service;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbxFiltroCategoria;
    @FXML private Label lblConteo;
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colCosto;
    @FXML private TableColumn<Producto, BigDecimal> colUtilidad;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;

    @FXML private Label lblFormTitulo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<Categoria> cbxCategoria;
    @FXML private ComboBox<UnidadMedida> cbxUnidad;
    @FXML private TextField txtCosto;
    @FXML private TextField txtUtilidad;
    @FXML private TextField txtStock;
    @FXML private CheckBox chkControlaStock;
    @FXML private ComboBox<EstadoProducto> cbxEstado;
    @FXML private Label lblMensaje;

    private final ObservableList<Producto> datos = FXCollections.observableArrayList();
    private FilteredList<Producto> filtrados;
    private Long idEditando;

    public ProductoController(IProductoService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("nombreCategoria"));
        colCosto.setCellValueFactory(new PropertyValueFactory<>("costo"));
        colUtilidad.setCellValueFactory(new PropertyValueFactory<>("porceUtil"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colCosto.setCellFactory(c -> celdaDinero());
        colPrecio.setCellFactory(c -> celdaDinero());
        colUtilidad.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(vacio || valor == null ? null : Formato.porcentaje(valor));
            }
        });

        filtrados = new FilteredList<>(datos, p -> true);
        tblProductos.setItems(filtrados);
        tblProductos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        cbxCategoria.setItems(FXCollections.observableArrayList(service.categorias()));
        cbxUnidad.setItems(FXCollections.observableArrayList(service.unidades()));
        cbxEstado.getItems().setAll(EstadoProducto.values());
        cbxEstado.getSelectionModel().selectFirst();
        chkControlaStock.setSelected(true);

        cbxFiltroCategoria.getItems().add("Todas");
        service.categorias().forEach(c -> cbxFiltroCategoria.getItems().add(c.getNombre()));
        cbxFiltroCategoria.getSelectionModel().selectFirst();

        txtBuscar.textProperty().addListener((o, a, b) -> aplicarFiltro());
        cbxFiltroCategoria.valueProperty().addListener((o, a, b) -> aplicarFiltro());
        cbxCategoria.valueProperty().addListener((o, a, b) -> actualizarControlStock());
        tblProductos.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> {
            if (b != null) cargarFormulario(b);
        });

        cargar();
        limpiarFormulario();
    }

    private TableCell<Producto, BigDecimal> celdaDinero() {
        return new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(vacio || valor == null ? null : Formato.dinero(valor));
            }
        };
    }

    private void cargar() {
        datos.setAll(service.findAll());
        aplicarFiltro();
    }

    private void aplicarFiltro() {
        if (filtrados == null) return;
        String texto = Formato.normalizar(txtBuscar.getText());
        String categoria = cbxFiltroCategoria.getValue();
        filtrados.setPredicate(p -> {
            boolean coincideTexto = texto.isEmpty() || Formato.normalizar(p.getNombre()).contains(texto);
            boolean coincideCategoria = categoria == null || categoria.startsWith("Todas")
                    || categoria.equalsIgnoreCase(p.getNombreCategoria());
            return coincideTexto && coincideCategoria;
        });
        lblConteo.setText(filtrados.size() + " registros");
    }

    private void cargarFormulario(Producto p) {
        idEditando = p.getIdProducto();
        txtNombre.setText(p.getNombre());
        seleccionarCategoria(p.getIdCategoria());
        seleccionarUnidad(p.getIdUnidadMedida());
        txtCosto.setText(p.getCosto() == null ? "" : p.getCosto().toPlainString());
        txtUtilidad.setText(p.getPorceUtil() == null ? ""
                : p.getPorceUtil().multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString());
        txtStock.setText(String.valueOf(p.getStock()));
        chkControlaStock.setSelected(p.isControlaStock());
        cbxEstado.setValue(p.getEstado());
        actualizarControlStock();
        lblFormTitulo.setText("Editar producto");
        mensaje("", false);
    }

    private void seleccionarCategoria(Long id) {
        for (Categoria c : cbxCategoria.getItems()) {
            if (c.getIdCategoria().equals(id)) {
                cbxCategoria.setValue(c);
                return;
            }
        }
    }

    private void seleccionarUnidad(Long id) {
        for (UnidadMedida u : cbxUnidad.getItems()) {
            if (u.getIdUnidadMedida().equals(id)) {
                cbxUnidad.setValue(u);
                return;
            }
        }
    }

    private void actualizarControlStock() {
        Categoria categoria = cbxCategoria.getValue();
        boolean esEntrada = categoria != null && "Entradas".equalsIgnoreCase(categoria.getNombre());
        if (esEntrada) {
            chkControlaStock.setSelected(false);
            chkControlaStock.setDisable(true);
            txtStock.setDisable(true);
            txtStock.setText("0");
        } else {
            chkControlaStock.setDisable(false);
            txtStock.setDisable(!chkControlaStock.isSelected());
        }
    }

    @FXML
    private void onNuevo() {
        limpiarFormulario();
        mensaje("", false);
    }

    @FXML
    private void onGuardar() {
        BigDecimal porcentaje = Formato.decimal(txtUtilidad.getText())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        Categoria categoria = cbxCategoria.getValue();
        UnidadMedida unidad = cbxUnidad.getValue();

        Producto producto = Producto.builder()
                .nombre(txtNombre.getText())
                .idCategoria(categoria == null ? null : categoria.getIdCategoria())
                .idUnidadMedida(unidad == null ? null : unidad.getIdUnidadMedida())
                .costo(Formato.decimal(txtCosto.getText()))
                .porceUtil(porcentaje)
                .stock(Formato.entero(txtStock.getText()))
                .controlaStock(chkControlaStock.isSelected())
                .estado(cbxEstado.getValue() == null ? EstadoProducto.ACTIVO : cbxEstado.getValue())
                .build();
        try {
            if (idEditando == null) {
                service.save(producto);
                mensaje("Producto registrado correctamente.", false);
            } else {
                service.update(idEditando, producto);
                mensaje("Producto actualizado correctamente.", false);
            }
            cargar();
            limpiarFormulario();
        } catch (BusinessException e) {
            mensaje(e.getMessage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            mensaje("No se pudo guardar el producto.", true);
        }
    }

    @FXML
    private void onCancelar() {
        limpiarFormulario();
        mensaje("", false);
    }

    @FXML
    private void onEliminar() {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensaje("Selecciona un producto de la tabla.", true);
            return;
        }
        if (!Dialogos.confirmar("Eliminar producto",
                "¿Seguro que deseas eliminar " + seleccionado.getNombre() + "?")) {
            return;
        }
        try {
            service.delete(seleccionado.getIdProducto());
            mensaje("Producto eliminado.", false);
            cargar();
            limpiarFormulario();
        } catch (BusinessException e) {
            mensaje(e.getMessage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            mensaje("No se pudo eliminar el producto.", true);
        }
    }

    private void limpiarFormulario() {
        idEditando = null;
        tblProductos.getSelectionModel().clearSelection();
        txtNombre.clear();
        cbxCategoria.getSelectionModel().clearSelection();
        cbxUnidad.getSelectionModel().clearSelection();
        txtCosto.clear();
        txtUtilidad.setText("50");
        txtStock.setText("0");
        chkControlaStock.setSelected(true);
        chkControlaStock.setDisable(false);
        txtStock.setDisable(false);
        cbxEstado.getSelectionModel().selectFirst();
        lblFormTitulo.setText("Nuevo producto");
    }

    private void mensaje(String texto, boolean error) {
        lblMensaje.setText(texto == null ? "" : texto);
        lblMensaje.getStyleClass().removeAll("error", "exito");
        if (texto != null && !texto.isBlank()) {
            lblMensaje.getStyleClass().add(error ? "error" : "exito");
        }
    }
}
