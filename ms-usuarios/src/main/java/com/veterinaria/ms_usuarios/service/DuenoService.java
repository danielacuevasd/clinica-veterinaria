package com.veterinaria.ms_usuarios.service;

import com.veterinaria.ms_usuarios.dto.DuenoRequestDto;
import com.veterinaria.ms_usuarios.dto.DuenoResponseDto;
import com.veterinaria.ms_usuarios.model.Dueno;
import com.veterinaria.ms_usuarios.repository.DuenoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DuenoService {

    private final DuenoRepository duenoRepository;

    public List<DuenoResponseDto> findAll() {
        log.info("Obteniendo todos los duenos activos");
        return duenoRepository.findByActivoTrue()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public DuenoResponseDto findById(Long id) {
        log.info("Buscando dueno con id={}", id);
        Dueno dueno = duenoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Dueno no encontrado con id: " + id));
        return toDto(dueno);
    }

    public List<DuenoResponseDto> search(String nombre) {
        log.info("Buscando duenos con nombre={}", nombre);
        return duenoRepository
                .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
                        nombre, nombre)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public DuenoResponseDto save(DuenoRequestDto dto) {
        log.info("Registrando nuevo dueno: {}", dto.getEmail());

        if (duenoRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException(
                    "Ya existe un dueno con el email: " + dto.getEmail());
        }
        if (duenoRepository.existsByRut(dto.getRut())) {
            throw new RuntimeException(
                    "Ya existe un dueno con el RUT: " + dto.getRut());
        }

        Dueno dueno = Dueno.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .rut(dto.getRut())
                .activo(true)
                .createdAt(LocalDateTime.now())
                .build();

        return toDto(duenoRepository.save(dueno));
    }

    public DuenoResponseDto update(Long id, DuenoRequestDto dto) {
        log.info("Actualizando dueno id={}", id);
        Dueno dueno = duenoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Dueno no encontrado con id: " + id));

        dueno.setNombre(dto.getNombre());
        dueno.setApellido(dto.getApellido());
        dueno.setEmail(dto.getEmail());
        dueno.setTelefono(dto.getTelefono());
        dueno.setRut(dto.getRut());

        return toDto(duenoRepository.save(dueno));
    }

    public void delete(Long id) {
        log.info("Eliminando dueno id={}", id);
        Dueno dueno = duenoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Dueno no encontrado con id: " + id));
        dueno.setActivo(false);
        duenoRepository.save(dueno);
    }

    private DuenoResponseDto toDto(Dueno dueno) {
        return DuenoResponseDto.builder()
                .id(dueno.getId())
                .nombre(dueno.getNombre())
                .apellido(dueno.getApellido())
                .email(dueno.getEmail())
                .telefono(dueno.getTelefono())
                .rut(dueno.getRut())
                .activo(dueno.getActivo())
                .createdAt(dueno.getCreatedAt())
                .build();
    }
}