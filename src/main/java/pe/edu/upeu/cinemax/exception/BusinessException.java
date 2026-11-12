package pe.edu.upeu.cinemax.exception;

/** Error de negocio con un mensaje que se muestra al usuario. */
public class BusinessException extends RuntimeException {

    public BusinessException(String mensaje) {
        super(mensaje);
    }
}
