package pe.edu.upeu.cinemax.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Sala del cine: 2D, 3D o VIP. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sala {

    private Long idSala;

    @NotBlank(message = "El nombre de la sala es obligatorio.")
    @Size(max = 30, message = "El nombre no debe superar 30 caracteres.")
    private String nombre;

    @NotBlank(message = "El tipo de sala es obligatorio.")
    private String tipo;

    @NotNull(message = "La capacidad es obligatoria.")
    @Min(value = 1, message = "La capacidad debe ser mayor a 0.")
    private Integer capacidad;

    @Override
    public String toString() {
        return nombre == null ? "" : nombre + " · " + tipo;
    }
}
