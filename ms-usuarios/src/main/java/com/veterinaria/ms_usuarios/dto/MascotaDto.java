package com.veterinaria.ms_usuarios.dto;

import lombok.Data;

@Data
public class MascotaDto {
    private Long id;
    private String nombre;
    private Long idDueno;
    private Boolean activo;
}