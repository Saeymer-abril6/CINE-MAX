package pe.edu.upeu.cinemax.enums;

/** Estado de un producto de dulcería. */
public enum EstadoProducto {

    ACTIVO("Activo"),
    INACTIVO("Inactivo");

    private final String descripcion;

    EstadoProducto(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static EstadoProducto desde(String valor) {
        if (valor == null) return ACTIVO;
        for (EstadoProducto e : values()) {
            if (e.descripcion.equalsIgnoreCase(valor) || e.name().equalsIgnoreCase(valor)) {
                return e;
            }
        }
        return ACTIVO;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
