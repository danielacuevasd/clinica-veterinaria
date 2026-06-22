package com.veterinaria.ms_inventario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicamentos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private String unidad;

    @Column(nullable = false)
    private BigDecimal precioUnitario;

    private Boolean activo = true;

    private LocalDateTime createdAt;
}