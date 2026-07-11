package com.veterinaria.ms_tratamientos.dto;

import lombok.Data;

@Data
public class MovimientoRequestDto {
    private Long idMedicamento;
    private String tipo; // "ENTRADA" o "SALIDA"
    private Integer cantidad;
    private String motivo;
}