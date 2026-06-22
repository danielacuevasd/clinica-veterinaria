package com.veterinaria.ms_veterinarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarioResponseDto {

    private Long id;
    private String nombre;
    private String apellido;
    private String especialidad;
    private String email;
    private String telefono;
    private Boolean disponible;
    private Boolean activo;
    private LocalDateTime createdAt;
}
