package pe.edu.upeu.cinemax.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import pe.edu.upeu.cinemax.enums.TipoEntrada;
import pe.edu.upeu.cinemax.model.*;
import pe.edu.upeu.cinemax.service.ClienteService;
import pe.edu.upeu.cinemax.service.PeliculaService;
import pe.edu.upeu.cinemax.service.VentaService;

import java.util.Locale;

public class MainController {
    private final PeliculaService peliculaService = new PeliculaService();
    private final ClienteService clienteService = new ClienteService();
    private final VentaService ventaService = new VentaService();

    private final ObservableList<Pelicula> peliculas = FXCollections.observableArrayList();
    private final ObservableList<Cliente> clientes = FXCollections.observableArrayList();
    private final ObservableList<Venta> ventas = FXCollections.observableArrayList();

    private final TableView<Pelicula> tablaPeliculas = new TableView<>();
    private final TableView<Cliente> tablaClientes = new TableView<>();
    private final TableView<Venta> tablaVentas = new TableView<>();

    private ComboBox<Pelicula> cbPelicula;
    private ComboBox<Cliente> cbCliente;
    private ComboBox<TipoEntrada> cbEntrada;
    private ComboBox<String> cbHorario;
    private ComboBox<String> cbAsiento;
    private ComboBox<Combo> cbCombo;
    private TextArea areaBoleta;

    public MainController() {
        peliculas.setAll(peliculaService.listar());
        clientes.setAll(clienteService.listar());
    }

    public TabPane crearVista() {
        TabPane tabs = new TabPane();
        tabs.getTabs().add(new Tab("Venta", crearVenta()));
        tabs.getTabs().add(new Tab("Películas", crearPeliculas()));
        tabs.getTabs().add(new Tab("Clientes", crearClientes()));
        tabs.getTabs().add(new Tab("Boletas", crearBoletas()));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        return tabs;
    }

    private VBox crearVenta() {
        cbCliente = new ComboBox<>(clientes);
        cbPelicula = new ComboBox<>(peliculas);
        cbEntrada = new ComboBox<>(FXCollections.observableArrayList(TipoEntrada.values()));
        cbHorario = new ComboBox<>(FXCollections.observableArrayList("2:00 PM", "5:00 PM", "8:00 PM"));
        cbAsiento = new ComboBox<>(FXCollections.observableArrayList("A1", "A2", "A3", "B1", "B2", "B3"));
        cbCombo = new ComboBox<>(FXCollections.observableArrayList(
                new Combo("Sin combo", 0),
                new Combo("Combo 1 - Canchita + Gaseosa", 10),
                new Combo("Combo 2 - Canchita + Gaseosa grande", 15)
        ));

        cbCliente.getSelectionModel().selectFirst();
        cbPelicula.getSelectionModel().selectFirst();
        cbEntrada.getSelectionModel().selectFirst();
        cbHorario.getSelectionModel().selectFirst();
        cbAsiento.getSelectionModel().selectFirst();
        cbCombo.getSelectionModel().selectFirst();

        GridPane formulario = new GridPane();
        formulario.setHgap(12);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(20));

        formulario.add(new Label("Cliente:"), 0, 0); formulario.add(cbCliente, 1, 0);
        formulario.add(new Label("Película:"), 0, 1); formulario.add(cbPelicula, 1, 1);
        formulario.add(new Label("Entrada:"), 0, 2); formulario.add(cbEntrada, 1, 2);
        formulario.add(new Label("Horario:"), 0, 3); formulario.add(cbHorario, 1, 3);
        formulario.add(new Label("Asiento:"), 0, 4); formulario.add(cbAsiento, 1, 4);
        formulario.add(new Label("Combo:"), 0, 5); formulario.add(cbCombo, 1, 5);

        areaBoleta = new TextArea();
        areaBoleta.setEditable(false);
        areaBoleta.setPrefRowCount(14);
        areaBoleta.setPromptText("Aquí aparecerá la boleta...");

        Button vender = new Button("VENDER");
        Button limpiar = new Button("LIMPIAR");
        Button boleta = new Button("VER BOLETA");

        vender.setOnAction(e -> registrarVenta());
        boleta.setOnAction(e -> mostrarUltimaBoleta());
        limpiar.setOnAction(e -> areaBoleta.clear());

        HBox botones = new HBox(10, vender, boleta, limpiar);
        VBox contenido = new VBox(15,
                titulo("VENTA DE ENTRADA"), formulario,
                titulo("BOLETA"), areaBoleta, botones);
        contenido.setPadding(new Insets(20));
        VBox.setVgrow(areaBoleta, Priority.ALWAYS);
        return contenido;
    }

    private VBox crearPeliculas() {
        TextField titulo = new TextField();
        TextField genero = new TextField();
        TextField duracion = new TextField();
        TextField clasificacion = new TextField();
        titulo.setPromptText("Título"); genero.setPromptText("Género");
        duracion.setPromptText("Duración"); clasificacion.setPromptText("Clasificación");

        TableColumn<Pelicula, String> cTitulo = new TableColumn<>("Título");
        cTitulo.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTitulo()));
        TableColumn<Pelicula, String> cGenero = new TableColumn<>("Género");
        cGenero.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getGenero()));
        TableColumn<Pelicula, String> cDuracion = new TableColumn<>("Duración");
        cDuracion.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getDuracion())));
        TableColumn<Pelicula, String> cClas = new TableColumn<>("Clasificación");
        cClas.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getClasificacion()));
        tablaPeliculas.getColumns().setAll(cTitulo, cGenero, cDuracion, cClas);
        tablaPeliculas.setItems(peliculas);

        Button agregar = new Button("Agregar");
        Button editar = new Button("Editar");
        Button eliminar = new Button("Eliminar");

        agregar.setOnAction(e -> {
            try {
                peliculaService.crear(titulo.getText(), genero.getText(), Integer.parseInt(duracion.getText()), clasificacion.getText());
                peliculas.setAll(peliculaService.listar());
                cbPelicula.setItems(peliculas);
                limpiarCampos(titulo, genero, duracion, clasificacion);
            } catch (NumberFormatException ex) { aviso("La duración debe ser un número."); }
        });

        editar.setOnAction(e -> {
            Pelicula p = tablaPeliculas.getSelectionModel().getSelectedItem();
            if (p == null) { aviso("Seleccione una película."); return; }
            try {
                p.setTitulo(titulo.getText()); p.setGenero(genero.getText());
                p.setDuracion(Integer.parseInt(duracion.getText())); p.setClasificacion(clasificacion.getText());
                peliculaService.actualizar(p); tablaPeliculas.refresh();
            } catch (NumberFormatException ex) { aviso("La duración debe ser un número."); }
        });

        eliminar.setOnAction(e -> {
            Pelicula p = tablaPeliculas.getSelectionModel().getSelectedItem();
            if (p != null) { peliculaService.eliminar(p.getId()); peliculas.setAll(peliculaService.listar()); cbPelicula.setItems(peliculas); }
        });

        tablaPeliculas.setOnMouseClicked(e -> {
            Pelicula p = tablaPeliculas.getSelectionModel().getSelectedItem();
            if (p != null) { titulo.setText(p.getTitulo()); genero.setText(p.getGenero()); duracion.setText(String.valueOf(p.getDuracion())); clasificacion.setText(p.getClasificacion()); }
        });

        GridPane campos = new GridPane(); campos.setHgap(8); campos.setVgap(8); campos.add(titulo,0,0); campos.add(genero,1,0); campos.add(duracion,2,0); campos.add(clasificacion,3,0);
        HBox botones = new HBox(8, agregar, editar, eliminar);
        VBox box = new VBox(12, titulo("CRUD DE PELÍCULAS"), campos, tablaPeliculas, botones);
        box.setPadding(new Insets(20)); VBox.setVgrow(tablaPeliculas, Priority.ALWAYS); return box;
    }

    private VBox crearClientes() {
        TextField dni = new TextField(); TextField nombre = new TextField(); TextField telefono = new TextField();
        dni.setPromptText("DNI"); nombre.setPromptText("Nombre"); telefono.setPromptText("Teléfono");

        TableColumn<Cliente, String> cDni = new TableColumn<>("DNI");
        cDni.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDni()));
        TableColumn<Cliente, String> cNombre = new TableColumn<>("Nombre");
        cNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNombre()));
        TableColumn<Cliente, String> cTel = new TableColumn<>("Teléfono");
        cTel.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTelefono()));
        tablaClientes.getColumns().setAll(cDni, cNombre, cTel); tablaClientes.setItems(clientes);

        Button agregar = new Button("Agregar"); Button editar = new Button("Editar"); Button eliminar = new Button("Eliminar");
        agregar.setOnAction(e -> { clienteService.crear(dni.getText(), nombre.getText(), telefono.getText()); clientes.setAll(clienteService.listar()); cbCliente.setItems(clientes); limpiarCampos(dni,nombre,telefono); });
        editar.setOnAction(e -> { Cliente c = tablaClientes.getSelectionModel().getSelectedItem(); if(c==null){aviso("Seleccione un cliente.");return;} c.setDni(dni.getText()); c.setNombre(nombre.getText()); c.setTelefono(telefono.getText()); clienteService.actualizar(c); tablaClientes.refresh(); });
        eliminar.setOnAction(e -> { Cliente c = tablaClientes.getSelectionModel().getSelectedItem(); if(c!=null){clienteService.eliminar(c.getId()); clientes.setAll(clienteService.listar()); cbCliente.setItems(clientes);} });
        tablaClientes.setOnMouseClicked(e -> { Cliente c=tablaClientes.getSelectionModel().getSelectedItem(); if(c!=null){dni.setText(c.getDni());nombre.setText(c.getNombre());telefono.setText(c.getTelefono());} });

        HBox campos = new HBox(8, dni, nombre, telefono); HBox botones = new HBox(8,agregar,editar,eliminar);
        VBox box = new VBox(12,titulo("CRUD DE CLIENTES"),campos,tablaClientes,botones); box.setPadding(new Insets(20)); VBox.setVgrow(tablaClientes,Priority.ALWAYS); return box;
    }

    private VBox crearBoletas() {
        TableColumn<Venta,String> cNumero = new TableColumn<>("Boleta");
        cNumero.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNumeroBoleta()));
        TableColumn<Venta,String> cCliente = new TableColumn<>("Cliente");
        cCliente.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCliente().getNombre()));
        TableColumn<Venta,String> cTotal = new TableColumn<>("Total");
        cTotal.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.format(Locale.US,"S/ %.2f",d.getValue().getTotal())));
        TableColumn<Venta,String> cFecha = new TableColumn<>("Fecha");
        cFecha.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getFechaFormateada()));
        tablaVentas.getColumns().setAll(cNumero,cCliente,cTotal,cFecha); tablaVentas.setItems(ventas);
        Button ver = new Button("Ver boleta"); Button eliminar = new Button("Eliminar");
        ver.setOnAction(e -> { Venta v=tablaVentas.getSelectionModel().getSelectedItem(); if(v!=null) areaBoleta.setText(v.toBoleta()); });
        eliminar.setOnAction(e -> { Venta v=tablaVentas.getSelectionModel().getSelectedItem(); if(v!=null){ventaService.eliminar(v.getId());ventas.setAll(ventaService.listar());} });
        HBox botones=new HBox(8,ver,eliminar);
        VBox box=new VBox(12,titulo("BOLETAS DE VENTA"),tablaVentas,botones); box.setPadding(new Insets(20)); VBox.setVgrow(tablaVentas,Priority.ALWAYS); return box;
    }

    private void registrarVenta() {
        if (cbPelicula.getValue() == null || cbCliente.getValue() == null) { aviso("Seleccione cliente y película."); return; }
        Entrada entrada = new Entrada(cbPelicula.getValue(), cbHorario.getValue(), cbAsiento.getValue(), cbEntrada.getValue());
        Venta venta = ventaService.registrar(new Venta(0,"",cbCliente.getValue(),entrada,cbCombo.getValue()));
        ventas.setAll(ventaService.listar());
        areaBoleta.setText(venta.toBoleta());
    }

    private void mostrarUltimaBoleta() {
        if (!ventas.isEmpty()) areaBoleta.setText(ventas.get(ventas.size()-1).toBoleta());
        else aviso("Todavía no hay ventas.");
    }

    private Label titulo(String texto) { Label l=new Label(texto); l.setFont(Font.font(18)); return l; }
    private void limpiarCampos(TextField... campos) { for(TextField c:campos)c.clear(); }
    private void aviso(String texto) { new Alert(Alert.AlertType.INFORMATION,texto,ButtonType.OK).showAndWait(); }
}
