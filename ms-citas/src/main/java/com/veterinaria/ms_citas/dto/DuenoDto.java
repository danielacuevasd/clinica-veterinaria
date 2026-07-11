package com.veterinaria.ms_citas.dto;

import lombok.Data;

@Data
public class DuenoDto {
    private Long id;
    private String nombre;
    private String apellido;
    private Boolean activo;
}