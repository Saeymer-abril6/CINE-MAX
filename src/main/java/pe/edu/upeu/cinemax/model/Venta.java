package pe.edu.upeu.cinemax.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Venta {
    public static final double IGV = 0.18;

    private int id;
    private String numeroBoleta;
    private Cliente cliente;
    private Entrada entrada;
    private Combo combo;
    private LocalDateTime fecha;

    public Venta() {
        this.fecha = LocalDateTime.now();
    }

    public Venta(int id, String numeroBoleta, Cliente cliente, Entrada entrada, Combo combo) {
        this.id = id;
        this.numeroBoleta = numeroBoleta;
        this.cliente = cliente;
        this.entrada = entrada;
        this.combo = combo;
        this.fecha = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNumeroBoleta() { return numeroBoleta; }
    public void setNumeroBoleta(String numeroBoleta) { this.numeroBoleta = numeroBoleta; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Entrada getEntrada() { return entrada; }
    public void setEntrada(Entrada entrada) { this.entrada = entrada; }
    public Combo getCombo() { return combo; }
    public void setCombo(Combo combo) { this.combo = combo; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public double getTotal() {
        return getSubtotal() + getIgv();
    }

    public double getSubtotal() {
        double total = entrada == null ? 0 : entrada.getPrecio();
        total += combo == null ? 0 : combo.getPrecio();
        return total / (1 + IGV);
    }

    public double getIgv() {
        double totalConIgv = (entrada == null ? 0 : entrada.getPrecio()) + (combo == null ? 0 : combo.getPrecio());
        return totalConIgv - getSubtotal();
    }

    public String getFechaFormateada() {
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String toBoleta() {
        String clienteNombre = cliente == null ? "CLIENTE GENERAL" : cliente.getNombre();
        String dni = cliente == null ? "-" : cliente.getDni();
        String pelicula = entrada == null ? "-" : entrada.getPelicula().getTitulo();
        String horario = entrada == null ? "-" : entrada.getHorario();
        String asiento = entrada == null ? "-" : entrada.getAsiento();
        String tipo = entrada == null ? "-" : entrada.getTipo().name();
        String comboNombre = combo == null ? "Sin combo" : combo.getNombre();
        double entradaPrecio = entrada == null ? 0 : entrada.getPrecio();
        double comboPrecio = combo == null ? 0 : combo.getPrecio();

        return "================================================\n" +
                "                 CINEMAX                      \n" +
                "        BOLETA DE VENTA ELECTRÓNICA          \n" +
                "RUC: [CONFIGURAR]          Serie: B001          \n" +
                "N°: " + numeroBoleta + "                         \n" +
                "------------------------------------------------\n" +
                "Fecha: " + getFechaFormateada() + "             \n" +
                "Cliente: " + clienteNombre + "\n" +
                "DNI: " + dni + "\n" +
                "------------------------------------------------\n" +
                String.format(Locale.US, "Entrada %-28s S/ %7.2f%n", tipo, entradaPrecio) +
                "Película: " + pelicula + "\n" +
                "Horario: " + horario + "   Asiento: " + asiento + "\n" +
                String.format(Locale.US, "Combo %-31s S/ %7.2f%n", comboNombre, comboPrecio) +
                "------------------------------------------------\n" +
                String.format(Locale.US, "Subtotal:                         S/ %7.2f%n", getSubtotal()) +
                String.format(Locale.US, "IGV (18%%):                        S/ %7.2f%n", getIgv()) +
                String.format(Locale.US, "TOTAL:                            S/ %7.2f%n", getTotal()) +
                "================================================\n" +
                "             Gracias por su compra             \n";
    }
}
