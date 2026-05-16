package com.veterinaria.ms_consultas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaEventDto {

    private Long idConsulta;
    private Long idCita;
    private Long idMascota;
    private Long idVeterinario;
    private String diagnostico;
    private LocalDateTime fecha;
}
