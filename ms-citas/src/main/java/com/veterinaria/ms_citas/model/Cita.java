package com.veterinaria.ms_citas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idMascota;

    @Column(nullable = false)
    private Long idVeterinario;

    @Column(nullable = false)
    private Long idDueno;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    private String motivo;

    @Enumerated(EnumType.STRING)
    private EstadoCita estado = EstadoCita.PENDIENTE;

    private LocalDateTime createdAt;
}