package com.veterinaria.ms_notificaciones.service;

import com.veterinaria.ms_notificaciones.dto.NotificacionResponseDto;
import com.veterinaria.ms_notificaciones.model.Notificacion;
import com.veterinaria.ms_notificaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public List<NotificacionResponseDto> findAll() {
        log.info("Obteniendo todas las notificaciones");
        return notificacionRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<NotificacionResponseDto> findByDestinatario(Long idDestinatario) {
        log.info("Buscando notificaciones del destinatario id={}", idDestinatario);
        return notificacionRepository
                .findByIdDestinatarioOrderByFechaEnvioDesc(idDestinatario)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<NotificacionResponseDto> findByTipo(String tipo) {
        log.info("Buscando notificaciones de tipo={}", tipo);
        return notificacionRepository.findByTipo(tipo)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private NotificacionResponseDto toDto(Notificacion n) {
        return NotificacionResponseDto.builder()
                .id(n.getId())
                .tipo(n.getTipo())
                .idDestinatario(n.getIdDestinatario())
                .mensaje(n.getMensaje())
                .enviado(n.getEnviado())
                .fechaEnvio(n.getFechaEnvio())
                .build();
    }
}