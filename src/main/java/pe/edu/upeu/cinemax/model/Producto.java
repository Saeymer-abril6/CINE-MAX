package pe.edu.upeu.cinemax.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.cinemax.enums.EstadoProducto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Producto de dulcería o entrada del cine. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    private Long idProducto;

    @NotBlank(message = "El nombre del producto es obligatorio.")
    @Size(max = 80, message = "El nombre no debe superar 80 caracteres.")
    private String nombre;

    @NotNull(message = "Selecciona una categoría.")
    private Long idCategoria;

    @NotNull(message = "Selecciona una unidad de medida.")
    private Long idUnidadMedida;

    @NotNull(message = "El costo es obligatorio.")
    @DecimalMin(value = "0.01", message = "El costo debe ser mayor a 0.")
    private BigDecimal costo;

    @NotNull(message = "El porcentaje de utilidad es obligatorio.")
    @DecimalMin(value = "0.00", message = "El porcentaje de utilidad no puede ser negativo.")
    private BigDecimal porceUtil;

    @NotNull(message = "El stock es obligatorio.")
    @Min(value = 0, message = "El stock no puede ser negativo.")
    private Integer stock;

    private boolean controlaStock;

    @NotNull(message = "El estado es obligatorio.")
    private EstadoProducto estado;

    // Datos de apoyo para mostrar en la tabla (no se guardan en producto)
    private String nombreCategoria;
    private String nombreUnidad;

    /** Precio de venta = costo × (1 + % de utilidad). */
    public BigDecimal getPrecioVenta() {
        if (costo == null) return BigDecimal.ZERO;
        BigDecimal utilidad = porceUtil == null ? BigDecimal.ZERO : porceUtil;
        return costo.multiply(BigDecimal.ONE.add(utilidad)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return nombre == null ? "" : nombre;
    }
}
