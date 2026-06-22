package com.veterinaria.ms_veterinarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "veterinarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private String especialidad;

    @Column(nullable = false, unique = true)
    private String email;

    private String telefono;

    private Boolean disponible = true;

    private Boolean activo = true;

    private LocalDateTime createdAt;
}
