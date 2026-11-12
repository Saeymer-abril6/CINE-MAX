package pe.edu.upeu.cinemax.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Unidad de medida de un producto. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnidadMedida {

    private Long idUnidadMedida;
    private String nombre;
    private String abreviatura;

    @Override
    public String toString() {
        return nombre == null ? "" : nombre;
    }
}
