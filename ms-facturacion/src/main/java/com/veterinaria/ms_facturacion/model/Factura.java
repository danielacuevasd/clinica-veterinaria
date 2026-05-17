package com.veterinaria.ms_facturacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idConsulta;

    @Column(nullable = false)
    private Long idMascota;

    @Column(nullable = false)
    private Long idDueno;

    @Column(nullable = false)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private EstadoFactura estado = EstadoFactura.PENDIENTE;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    private LocalDateTime createdAt;
}