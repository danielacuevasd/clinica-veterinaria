package com.veterinaria.ms_veterinarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class HorarioRequestDto {

    @NotNull(message = "El id del veterinario es obligatorio")
    private Long idVeterinario;

    @NotBlank(message = "El dia de la semana es obligatorio")
    private String diaSemana;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;
}
