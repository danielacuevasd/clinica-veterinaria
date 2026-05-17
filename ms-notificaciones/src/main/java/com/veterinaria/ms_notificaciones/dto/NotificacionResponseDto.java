package com.veterinaria.ms_notificaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionResponseDto {

    private Long id;
    private String tipo;
    private Long idDestinatario;
    private String mensaje;
    private Boolean enviado;
    private LocalDateTime fechaEnvio;
}