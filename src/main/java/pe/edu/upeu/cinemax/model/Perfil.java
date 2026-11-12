package pe.edu.upeu.cinemax.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Perfil de acceso: Administrador o Vendedor. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Perfil {

    private Long idPerfil;
    private String nombre;

    @Override
    public String toString() {
        return nombre == null ? "" : nombre;
    }
}
