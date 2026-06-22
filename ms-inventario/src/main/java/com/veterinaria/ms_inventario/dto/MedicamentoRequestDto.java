package com.veterinaria.ms_inventario.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MedicamentoRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotBlank(message = "La unidad es obligatoria")
    private String unidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precioUnitario;
}
