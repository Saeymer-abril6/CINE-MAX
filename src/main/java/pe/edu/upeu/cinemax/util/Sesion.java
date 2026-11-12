package pe.edu.upeu.cinemax.util;

import pe.edu.upeu.cinemax.model.Usuario;

/** Guarda el usuario que inició sesión. */
public final class Sesion {

    private static Usuario usuario;

    private Sesion() {
    }

    public static void iniciar(Usuario u) {
        usuario = u;
    }

    public static Usuario getUsuario() {
        return usuario;
    }

    public static void cerrar() {
        usuario = null;
    }
}
