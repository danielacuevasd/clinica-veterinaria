package com.veterinaria.ms_citas.dto;

import lombok.Data;

@Data
public class VeterinarioDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String especialidad;
    private Boolean disponible;
}