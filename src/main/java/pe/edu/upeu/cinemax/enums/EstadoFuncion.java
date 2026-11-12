package pe.edu.upeu.cinemax.enums;

/** Estado de una función. */
public enum EstadoFuncion {

    PROGRAMADA("Programada"),
    FINALIZADA("Finalizada");

    private final String descripcion;

    EstadoFuncion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static EstadoFuncion desde(String valor) {
        if (valor == null) return PROGRAMADA;
        for (EstadoFuncion e : values()) {
            if (e.descripcion.equalsIgnoreCase(valor) || e.name().equalsIgnoreCase(valor)) {
                return e;
            }
        }
        return PROGRAMADA;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
