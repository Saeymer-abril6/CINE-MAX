package pe.edu.upeu.cinemax.enums;

/** Estado de una película en cartelera. */
public enum EstadoPelicula {

    EN_CARTELERA("En cartelera"),
    RETIRADA("Retirada");

    private final String descripcion;

    EstadoPelicula(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static EstadoPelicula desde(String valor) {
        if (valor == null) return EN_CARTELERA;
        for (EstadoPelicula e : values()) {
            if (e.descripcion.equalsIgnoreCase(valor) || e.name().equalsIgnoreCase(valor)) {
                return e;
            }
        }
        return EN_CARTELERA;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
