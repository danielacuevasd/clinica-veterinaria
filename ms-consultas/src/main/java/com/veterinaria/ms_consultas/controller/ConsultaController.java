package com.veterinaria.ms_consultas.controller;

import com.veterinaria.ms_consultas.dto.ConsultaRequestDto;
import com.veterinaria.ms_consultas.dto.ConsultaResponseDto;
import com.veterinaria.ms_consultas.service.ConsultaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultas")
@RequiredArgsConstructor
@Slf4j
public class ConsultaController {

    private final ConsultaService consultaService;

    @GetMapping
    public ResponseEntity<List<ConsultaResponseDto>> getAll() {
        return ResponseEntity.ok(consultaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(consultaService.findById(id));
    }

    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<List<ConsultaResponseDto>> getByMascota(
            @PathVariable Long idMascota) {
        return ResponseEntity.ok(consultaService.findByMascota(idMascota));
    }

    @GetMapping("/veterinario/{idVeterinario}")
    public ResponseEntity<List<ConsultaResponseDto>> getByVeterinario(
            @PathVariable Long idVeterinario) {
        return ResponseEntity.ok(consultaService.findByVeterinario(idVeterinario));
    }

    @PostMapping
    public ResponseEntity<ConsultaResponseDto> create(
            @Valid @RequestBody ConsultaRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(consultaService.save(dto));
    }
}
