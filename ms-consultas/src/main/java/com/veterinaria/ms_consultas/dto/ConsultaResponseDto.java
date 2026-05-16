package com.veterinaria.ms_consultas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaResponseDto {

    private Long id;
    private Long idCita;
    private Long idMascota;
    private Long idVeterinario;
    private String diagnostico;
    private BigDecimal peso;
    private BigDecimal temperatura;
    private String observaciones;
    private LocalDateTime fecha;
}
