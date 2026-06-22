package com.veterinaria.ms_mascotas.controller;

import com.veterinaria.ms_mascotas.dto.HistorialMedicoRequestDto;
import com.veterinaria.ms_mascotas.dto.HistorialMedicoResponseDto;
import com.veterinaria.ms_mascotas.dto.MascotaRequestDto;
import com.veterinaria.ms_mascotas.dto.MascotaResponseDto;
import com.veterinaria.ms_mascotas.service.MascotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mascotas")
@RequiredArgsConstructor
@Slf4j
public class MascotaController {

    private final MascotaService mascotaService;

    @GetMapping
    public ResponseEntity<List<MascotaResponseDto>> getAll() {
        return ResponseEntity.ok(mascotaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.findById(id));
    }

    @GetMapping("/dueno/{idDueno}")
    public ResponseEntity<List<MascotaResponseDto>> getByDueno(
            @PathVariable Long idDueno) {
        return ResponseEntity.ok(mascotaService.findByDueno(idDueno));
    }

    @GetMapping("/especie/{especie}")
    public ResponseEntity<List<MascotaResponseDto>> getByEspecie(
            @PathVariable String especie) {
        return ResponseEntity.ok(mascotaService.findByEspecie(especie));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MascotaResponseDto>> search(
            @RequestParam String nombre) {
        return ResponseEntity.ok(mascotaService.search(nombre));
    }

    @PostMapping
    public ResponseEntity<MascotaResponseDto> create(
            @Valid @RequestBody MascotaRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mascotaService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MascotaResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody MascotaRequestDto dto) {
        return ResponseEntity.ok(mascotaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mascotaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/historial")
    public ResponseEntity<HistorialMedicoResponseDto> addHistorial(
            @Valid @RequestBody HistorialMedicoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mascotaService.addHistorial(dto));
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialMedicoResponseDto>> getHistorial(
            @PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.getHistorial(id));
    }
}
