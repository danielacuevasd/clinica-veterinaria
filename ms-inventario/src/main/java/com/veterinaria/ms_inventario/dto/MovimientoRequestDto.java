package com.veterinaria.ms_inventario.dto;

import com.veterinaria.ms_inventario.model.TipoMovimiento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MovimientoRequestDto {

    @NotNull(message = "El id del medicamento es obligatorio")
    private Long idMedicamento;

    @NotNull(message = "El tipo es obligatorio")
    private TipoMovimiento tipo;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    private String motivo;
}