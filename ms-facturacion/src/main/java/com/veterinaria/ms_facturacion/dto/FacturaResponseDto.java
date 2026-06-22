package com.veterinaria.ms_facturacion.dto;

import com.veterinaria.ms_facturacion.model.EstadoFactura;
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
public class FacturaResponseDto {

    private Long id;
    private Long idConsulta;
    private Long idMascota;
    private Long idDueno;
    private BigDecimal total;
    private EstadoFactura estado;
    private String observaciones;
    private LocalDateTime createdAt;
}
