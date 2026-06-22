package com.veterinaria.ms_mascotas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MascotaResponseDto {

    private Long id;
    private String nombre;
    private String especie;
    private String raza;
    private LocalDate fechaNacimiento;
    private Long idDueno;
    private Boolean activo;
    private LocalDateTime createdAt;
}
