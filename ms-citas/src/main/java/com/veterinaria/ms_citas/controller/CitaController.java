package com.veterinaria.ms_citas.controller;

import com.veterinaria.ms_citas.dto.CitaRequestDto;
import com.veterinaria.ms_citas.dto.CitaResponseDto;
import com.veterinaria.ms_citas.model.EstadoCita;
import com.veterinaria.ms_citas.service.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/citas")
@RequiredArgsConstructor
@Slf4j
public class CitaController {

    private final CitaService citaService;

    @GetMapping
    public ResponseEntity<List<CitaResponseDto>> getAll() {
        return ResponseEntity.ok(citaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.findById(id));
    }

    @GetMapping("/dueno/{idDueno}")
    public ResponseEntity<List<CitaResponseDto>> getByDueno(
            @PathVariable Long idDueno) {
        return ResponseEntity.ok(citaService.findByDueno(idDueno));
    }

    @GetMapping("/veterinario/{idVeterinario}")
    public ResponseEntity<List<CitaResponseDto>> getByVeterinario(
            @PathVariable Long idVeterinario) {
        return ResponseEntity.ok(citaService.findByVeterinario(idVeterinario));
    }

    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<List<CitaResponseDto>> getByMascota(
            @PathVariable Long idMascota) {
        return ResponseEntity.ok(citaService.findByMascota(idMascota));
    }

    @PostMapping
    public ResponseEntity<CitaResponseDto> create(
            @Valid @RequestBody CitaRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(citaService.save(dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CitaResponseDto> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoCita estado) {
        return ResponseEntity.ok(citaService.cambiarEstado(id, estado));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        citaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
