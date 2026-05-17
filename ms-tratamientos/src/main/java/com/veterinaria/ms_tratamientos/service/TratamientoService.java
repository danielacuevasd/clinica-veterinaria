package com.veterinaria.ms_tratamientos.service;

import com.veterinaria.ms_tratamientos.dto.TratamientoRequestDto;
import com.veterinaria.ms_tratamientos.dto.TratamientoResponseDto;
import com.veterinaria.ms_tratamientos.model.Tratamiento;
import com.veterinaria.ms_tratamientos.repository.TratamientoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TratamientoService {

    private final TratamientoRepository tratamientoRepository;

    public List<TratamientoResponseDto> findAll() {
        log.info("Obteniendo todos los tratamientos");
        return tratamientoRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public TratamientoResponseDto findById(Long id) {
        log.info("Buscando tratamiento con id={}", id);
        Tratamiento tratamiento = tratamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Tratamiento no encontrado con id: " + id));
        return toDto(tratamiento);
    }

    public List<TratamientoResponseDto> findByConsulta(Long idConsulta) {
        log.info("Buscando tratamientos de la consulta id={}", idConsulta);
        return tratamientoRepository.findByIdConsulta(idConsulta)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<TratamientoResponseDto> findByMascota(Long idMascota) {
        log.info("Buscando tratamientos de la mascota id={}", idMascota);
        return tratamientoRepository.findByIdMascotaOrderByCreatedAtDesc(idMascota)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public TratamientoResponseDto save(TratamientoRequestDto dto) {
        log.info("Creando tratamiento para consulta id={}", dto.getIdConsulta());
        Tratamiento tratamiento = Tratamiento.builder()
                .idConsulta(dto.getIdConsulta())
                .idMascota(dto.getIdMascota())
                .medicamento(dto.getMedicamento())
                .dosis(dto.getDosis())
                .frecuencia(dto.getFrecuencia())
                .duracionDias(dto.getDuracionDias())
                .indicaciones(dto.getIndicaciones())
                .createdAt(LocalDateTime.now())
                .build();
        return toDto(tratamientoRepository.save(tratamiento));
    }

    public TratamientoResponseDto update(Long id, TratamientoRequestDto dto) {
        log.info("Actualizando tratamiento id={}", id);
        Tratamiento tratamiento = tratamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Tratamiento no encontrado con id: " + id));
        tratamiento.setMedicamento(dto.getMedicamento());
        tratamiento.setDosis(dto.getDosis());
        tratamiento.setFrecuencia(dto.getFrecuencia());
        tratamiento.setDuracionDias(dto.getDuracionDias());
        tratamiento.setIndicaciones(dto.getIndicaciones());
        return toDto(tratamientoRepository.save(tratamiento));
    }

    private TratamientoResponseDto toDto(Tratamiento t) {
        return TratamientoResponseDto.builder()
                .id(t.getId())
                .idConsulta(t.getIdConsulta())
                .idMascota(t.getIdMascota())
                .medicamento(t.getMedicamento())
                .dosis(t.getDosis())
                .frecuencia(t.getFrecuencia())
                .duracionDias(t.getDuracionDias())
                .indicaciones(t.getIndicaciones())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
