package com.veterinaria.ms_inventario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_stock")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idMedicamento;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipo;

    @Column(nullable = false)
    private Integer cantidad;

    private String motivo;

    private LocalDateTime fecha;
}