package pe.edu.upeu.cinemax.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Línea de detalle de una venta. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaDetalle {

    private Long idVentaDetalle;
    private Long idVenta;
    private Long idProducto;
    private Long idFuncion;
    private BigDecimal precioUnit;
    private BigDecimal porceUtil;
    private Integer cantidad;
    private BigDecimal netoTotal;
    private BigDecimal utilidad;

    // Datos de apoyo para mostrar en la tabla (no se guardan en venta_detalle)
    private String nombreProducto;
    private String descripcionFuncion;
    private boolean controlaStock;

    public String getFuncionTexto() {
        return descripcionFuncion == null || descripcionFuncion.isBlank() ? "—" : descripcionFuncion;
    }
}
