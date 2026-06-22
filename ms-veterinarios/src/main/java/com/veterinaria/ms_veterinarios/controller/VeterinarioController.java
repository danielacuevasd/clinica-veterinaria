package com.veterinaria.ms_veterinarios.controller;

import com.veterinaria.ms_veterinarios.dto.HorarioRequestDto;
import com.veterinaria.ms_veterinarios.dto.HorarioResponseDto;
import com.veterinaria.ms_veterinarios.dto.VeterinarioRequestDto;
import com.veterinaria.ms_veterinarios.dto.VeterinarioResponseDto;
import com.veterinaria.ms_veterinarios.service.VeterinarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/veterinarios")
@RequiredArgsConstructor
@Slf4j
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    @GetMapping
    public ResponseEntity<List<VeterinarioResponseDto>> getAll() {
        return ResponseEntity.ok(veterinarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.findById(id));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<VeterinarioResponseDto>> getDisponibles() {
        return ResponseEntity.ok(veterinarioService.findDisponibles());
    }

    @GetMapping("/{id}/disponible")
    public ResponseEntity<Boolean> isDisponible(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.isDisponible(id));
    }

    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<VeterinarioResponseDto>> getByEspecialidad(
            @PathVariable String especialidad) {
        return ResponseEntity.ok(veterinarioService.findByEspecialidad(especialidad));
    }

    @GetMapping("/search")
    public ResponseEntity<List<VeterinarioResponseDto>> search(
            @RequestParam String nombre) {
        return ResponseEntity.ok(veterinarioService.search(nombre));
    }

    @PostMapping
    public ResponseEntity<VeterinarioResponseDto> create(
            @Valid @RequestBody VeterinarioRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(veterinarioService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody VeterinarioRequestDto dto) {
        return ResponseEntity.ok(veterinarioService.update(id, dto));
    }

    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<VeterinarioResponseDto> cambiarDisponibilidad(
            @PathVariable Long id,
            @RequestParam Boolean disponible) {
        return ResponseEntity.ok(
                veterinarioService.cambiarDisponibilidad(id, disponible));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        veterinarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/horarios")
    public ResponseEntity<HorarioResponseDto> addHorario(
            @Valid @RequestBody HorarioRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(veterinarioService.addHorario(dto));
    }

    @GetMapping("/{id}/horarios")
    public ResponseEntity<List<HorarioResponseDto>> getHorarios(
            @PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.getHorarios(id));
    }
}