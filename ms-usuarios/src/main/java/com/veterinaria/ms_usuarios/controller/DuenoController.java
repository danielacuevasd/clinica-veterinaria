package com.veterinaria.ms_usuarios.controller;

import com.veterinaria.ms_usuarios.dto.DuenoRequestDto;
import com.veterinaria.ms_usuarios.dto.DuenoResponseDto;
import com.veterinaria.ms_usuarios.service.DuenoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Slf4j
public class DuenoController {

    private final DuenoService duenoService;

    @GetMapping
    public ResponseEntity<List<DuenoResponseDto>> getAll() {
        return ResponseEntity.ok(duenoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DuenoResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(duenoService.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DuenoResponseDto>> search(
            @RequestParam String nombre) {
        return ResponseEntity.ok(duenoService.search(nombre));
    }

    @PostMapping
    public ResponseEntity<DuenoResponseDto> create(
            @Valid @RequestBody DuenoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(duenoService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DuenoResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody DuenoRequestDto dto) {
        return ResponseEntity.ok(duenoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        duenoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}