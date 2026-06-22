package com.veterinaria.ms_citas.dto;

import com.veterinaria.ms_citas.model.EstadoCita;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitaResponseDto {

    private Long id;
    private Long idMascota;
    private Long idVeterinario;
    private Long idDueno;
    private LocalDateTime fechaHora;
    private String motivo;
    private EstadoCita estado;
    private LocalDateTime createdAt;
}
