package com.veterinaria.ms_citas.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CitaRequestDto {

    @NotNull(message = "El id de la mascota es obligatorio")
    private Long idMascota;

    @NotNull(message = "El id del veterinario es obligatorio")
    private Long idVeterinario;

    @NotNull(message = "El id del dueño es obligatorio")
    private Long idDueno;

    @NotNull(message = "La fecha y hora es obligatoria")
    private LocalDateTime fechaHora;

    private String motivo;
}