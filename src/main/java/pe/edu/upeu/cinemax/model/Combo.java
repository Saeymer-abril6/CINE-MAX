package pe.edu.upeu.cinemax.model;

import java.util.Locale;

public class Combo {
    private final String nombre;
    private final double precio;

    public Combo(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }

    @Override
    public String toString() {
        return nombre + " - S/ " + String.format(Locale.US, "%.2f", precio);
    }
}
