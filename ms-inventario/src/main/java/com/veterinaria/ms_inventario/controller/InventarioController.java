package com.veterinaria.ms_inventario.controller;

import com.veterinaria.ms_inventario.dto.MedicamentoRequestDto;
import com.veterinaria.ms_inventario.dto.MedicamentoResponseDto;
import com.veterinaria.ms_inventario.dto.MovimientoRequestDto;
import com.veterinaria.ms_inventario.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventario")
@RequiredArgsConstructor
@Slf4j
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<MedicamentoResponseDto>> getAll() {
        return ResponseEntity.ok(inventarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.findById(id));
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<MedicamentoResponseDto>> getStockBajo(
            @RequestParam(defaultValue = "10") Integer limite) {
        return ResponseEntity.ok(inventarioService.findStockBajo(limite));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MedicamentoResponseDto>> search(
            @RequestParam String nombre) {
        return ResponseEntity.ok(inventarioService.search(nombre));
    }

    @PostMapping
    public ResponseEntity<MedicamentoResponseDto> create(
            @Valid @RequestBody MedicamentoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody MedicamentoRequestDto dto) {
        return ResponseEntity.ok(inventarioService.update(id, dto));
    }

    @PostMapping("/movimientos")
    public ResponseEntity<MedicamentoResponseDto> registrarMovimiento(
            @Valid @RequestBody MovimientoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.registrarMovimiento(dto));
    }
}
