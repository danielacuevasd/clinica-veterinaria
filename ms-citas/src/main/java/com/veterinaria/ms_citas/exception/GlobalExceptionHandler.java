package com.veterinaria.ms_citas.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        log.warn("Error de validacion: {}", errors);
        return ResponseEntity.badRequest().body(errors);
    }

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