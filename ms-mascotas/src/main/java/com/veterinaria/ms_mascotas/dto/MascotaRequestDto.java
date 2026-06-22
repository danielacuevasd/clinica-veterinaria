package com.veterinaria.ms_mascotas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MascotaRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La especie es obligatoria")
    private String especie;

    private String raza;

    private LocalDate fechaNacimiento;

    @NotNull(message = "El id del dueño es obligatorio")
    private Long idDueno;
}
