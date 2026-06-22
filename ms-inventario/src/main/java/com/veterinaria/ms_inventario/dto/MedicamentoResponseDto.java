package com.veterinaria.ms_inventario.dto;

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
public class MedicamentoResponseDto {

    private Long id;
    private String nombre;
    private Integer stock;
    private String unidad;
    private BigDecimal precioUnitario;
    private Boolean activo;
    private LocalDateTime createdAt;
}
