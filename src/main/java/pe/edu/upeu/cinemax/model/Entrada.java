package pe.edu.upeu.cinemax.model;

import pe.edu.upeu.cinemax.enums.TipoEntrada;

public class Entrada {
    private final Pelicula pelicula;
    private final String horario;
    private final String asiento;
    private final TipoEntrada tipo;

    public Entrada(Pelicula pelicula, String horario, String asiento, TipoEntrada tipo) {
        this.pelicula = pelicula;
        this.horario = horario;
        this.asiento = asiento;
        this.tipo = tipo;
    }

    public Pelicula getPelicula() { return pelicula; }
    public String getHorario() { return horario; }
    public String getAsiento() { return asiento; }
    public TipoEntrada getTipo() { return tipo; }
    public double getPrecio() { return tipo.getPrecio(); }
}
