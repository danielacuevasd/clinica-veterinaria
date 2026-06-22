package com.veterinaria.ms_facturacion.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FacturaRequestDto {

    @NotNull(message = "El id de la consulta es obligatorio")
    private Long idConsulta;

    @NotNull(message = "El id de la mascota es obligatorio")
    private Long idMascota;

    @NotNull(message = "El id del dueno es obligatorio")
    private Long idDueno;

    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.0", message = "El total no puede ser negativo")
    private BigDecimal total;

    private String observaciones;
}
