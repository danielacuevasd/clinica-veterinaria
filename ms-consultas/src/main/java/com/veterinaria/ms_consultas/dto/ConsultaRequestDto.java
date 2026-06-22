package com.veterinaria.ms_consultas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConsultaRequestDto {

    @NotNull(message = "El id de la cita es obligatorio")
    private Long idCita;

    @NotNull(message = "El id de la mascota es obligatorio")
    private Long idMascota;

    @NotNull(message = "El id del veterinario es obligatorio")
    private Long idVeterinario;

    @NotNull(message = "El id del dueño es obligatorio")
    private Long idDueno;

    @NotBlank(message = "El diagnostico es obligatorio")
    private String diagnostico;

    private BigDecimal peso;

    private BigDecimal temperatura;

    private String observaciones;
}
