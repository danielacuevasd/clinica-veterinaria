package com.veterinaria.ms_tratamientos.controller;

import com.veterinaria.ms_tratamientos.dto.TratamientoRequestDto;
import com.veterinaria.ms_tratamientos.dto.TratamientoResponseDto;
import com.veterinaria.ms_tratamientos.service.TratamientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tratamientos")
@RequiredArgsConstructor
@Slf4j
public class TratamientoController {

    private final TratamientoService tratamientoService;

    @GetMapping
    public ResponseEntity<List<TratamientoResponseDto>> getAll() {
        return ResponseEntity.ok(tratamientoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TratamientoResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tratamientoService.findById(id));
    }

    @GetMapping("/consulta/{idConsulta}")
    public ResponseEntity<List<TratamientoResponseDto>> getByConsulta(
            @PathVariable Long idConsulta) {
        return ResponseEntity.ok(tratamientoService.findByConsulta(idConsulta));
    }

    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<List<TratamientoResponseDto>> getByMascota(
            @PathVariable Long idMascota) {
        return ResponseEntity.ok(tratamientoService.findByMascota(idMascota));
    }

    @PostMapping
    public ResponseEntity<TratamientoResponseDto> create(
            @Valid @RequestBody TratamientoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tratamientoService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TratamientoResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody TratamientoRequestDto dto) {
        return ResponseEntity.ok(tratamientoService.update(id, dto));
    }
}
