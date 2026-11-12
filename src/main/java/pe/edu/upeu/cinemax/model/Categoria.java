package pe.edu.upeu.cinemax.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Categoría de productos: Snacks, Bebidas, Combos, Entradas. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    private Long idCategoria;
    private String nombre;
    private String descripcion;

    @Override
    public String toString() {
        return nombre == null ? "" : nombre;
    }
}
