package com.veterinaria.ms_tratamientos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TratamientoResponseDto {

    private Long id;
    private Long idConsulta;
    private Long idMascota;
    private String medicamento;
    private String dosis;
    private String frecuencia;
    private Integer duracionDias;
    private String indicaciones;
    private LocalDateTime createdAt;
}
