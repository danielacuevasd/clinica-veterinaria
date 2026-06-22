package com.veterinaria.ms_mascotas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialMedicoResponseDto {

    private Long id;
    private Long idMascota;
    private String descripcion;
    private LocalDate fecha;
}