package com.veterinaria.ms_consultas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idCita;

    @Column(nullable = false)
    private Long idMascota;

    @Column(nullable = false)
    private Long idVeterinario;

    @Column(nullable = false)
    private String diagnostico;

    private BigDecimal peso;

    private BigDecimal temperatura;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    private LocalDateTime fecha;
}
