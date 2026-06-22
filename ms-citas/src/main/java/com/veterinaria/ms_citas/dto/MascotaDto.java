package com.veterinaria.ms_citas.dto;

import lombok.Data;

@Data
public class MascotaDto {
    private Long id;
    private String nombre;
    private String especie;
    private Long idDueno;
    private Boolean activo;
}
