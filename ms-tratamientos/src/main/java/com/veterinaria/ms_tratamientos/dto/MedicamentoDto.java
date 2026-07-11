package com.veterinaria.ms_tratamientos.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MedicamentoDto {
    private Long id;
    private String nombre;
    private Integer stock;
    private String unidad;
    private BigDecimal precioUnitario;
    private Boolean activo;
}
