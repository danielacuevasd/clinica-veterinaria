package com.veterinaria.ms_notificaciones.controller;

import com.veterinaria.ms_notificaciones.dto.NotificacionResponseDto;
import com.veterinaria.ms_notificaciones.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
@Slf4j
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<List<NotificacionResponseDto>> getAll() {
        return ResponseEntity.ok(notificacionService.findAll());
    }

    @GetMapping("/destinatario/{idDestinatario}")
    public ResponseEntity<List<NotificacionResponseDto>> getByDestinatario(
            @PathVariable Long idDestinatario) {
        return ResponseEntity.ok(
                notificacionService.findByDestinatario(idDestinatario));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<NotificacionResponseDto>> getByTipo(
            @PathVariable String tipo) {
        return ResponseEntity.ok(notificacionService.findByTipo(tipo));
    }
}
