package com.veterinaria.ms_tratamientos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TratamientoRequestDto {

    @NotNull(message = "El id de la consulta es obligatorio")
    private Long idConsulta;

    @NotNull(message = "El id de la mascota es obligatorio")
    private Long idMascota;

    @NotBlank(message = "El medicamento es obligatorio")
    private String medicamento;

    @NotBlank(message = "La dosis es obligatoria")
    private String dosis;

    @NotBlank(message = "La frecuencia es obligatoria")
    private String frecuencia;

    @NotNull(message = "La duracion en dias es obligatoria")
    private Integer duracionDias;

    private String indicaciones;
}
