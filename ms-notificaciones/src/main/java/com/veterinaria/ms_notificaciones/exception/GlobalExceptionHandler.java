package com.veterinaria.ms_notificaciones.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(
            RuntimeException ex) {
        String mensaje = ex.getMessage() != null ? ex.getMessage() : "";
        String mensajeLower = mensaje.toLowerCase();
        if (mensajeLower.contains("no encontrad")) {
            log.warn("Recurso no encontrado: {}", mensaje);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", mensaje));
        }
        if (mensajeLower.contains("ya existe")
                || (mensajeLower.contains("ya est") && mensajeLower.contains("uso"))) {
            log.warn("Recurso duplicado: {}", mensaje);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", mensaje));
        }
        log.error("Error de negocio: {}", mensaje);
        return ResponseEntity.badRequest()
                .body(Map.of("error", mensaje));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
        log.error("Error inesperado: {}", ex.getMessage());
        return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error interno del servidor"));
    }
}
