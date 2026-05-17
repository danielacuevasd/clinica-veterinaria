package com.veterinaria.ms_usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DuenoResponseDto {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String rut;
    private Boolean activo;
    private LocalDateTime createdAt;
}