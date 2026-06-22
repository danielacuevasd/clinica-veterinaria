package com.veterinaria.ms_facturacion.controller;

import com.veterinaria.ms_facturacion.dto.FacturaRequestDto;
import com.veterinaria.ms_facturacion.dto.FacturaResponseDto;
import com.veterinaria.ms_facturacion.model.EstadoFactura;
import com.veterinaria.ms_facturacion.service.FacturacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facturas")
@RequiredArgsConstructor
@Slf4j
public class FacturacionController {

    private final FacturacionService facturacionService;

    @GetMapping
    public ResponseEntity<List<FacturaResponseDto>> getAll() {
        return ResponseEntity.ok(facturacionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(facturacionService.findById(id));
    }

    @GetMapping("/dueno/{idDueno}")
    public ResponseEntity<List<FacturaResponseDto>> getByDueno(
            @PathVariable Long idDueno) {
        return ResponseEntity.ok(facturacionService.findByDueno(idDueno));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<FacturaResponseDto>> getByEstado(
            @PathVariable EstadoFactura estado) {
        return ResponseEntity.ok(facturacionService.findByEstado(estado));
    }

    @PostMapping
    public ResponseEntity<FacturaResponseDto> create(
            @Valid @RequestBody FacturaRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(facturacionService.save(dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<FacturaResponseDto> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoFactura estado) {
        return ResponseEntity.ok(facturacionService.cambiarEstado(id, estado));
    }
}