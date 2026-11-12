package pe.edu.upeu.cinemax.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Usuario que puede iniciar sesión en el sistema. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private Long idUsuario;
    private String usuario;
    private String clave;
    private String nombre;
    private Long idPerfil;
    private String nombrePerfil;
    private String estado;

    public boolean isActivo() {
        return "Activo".equalsIgnoreCase(estado);
    }

    @Override
    public String toString() {
        return nombre == null ? usuario : nombre;
    }
}
