package com.veterinaria.ms_citas.service;

import com.veterinaria.ms_citas.dto.CitaRequestDto;
import com.veterinaria.ms_citas.dto.CitaResponseDto;
import com.veterinaria.ms_citas.dto.DuenoDto;
import com.veterinaria.ms_citas.dto.MascotaDto;
import com.veterinaria.ms_citas.dto.VeterinarioDto;
import com.veterinaria.ms_citas.feign.MascotaClient;
import com.veterinaria.ms_citas.feign.UsuarioClient;
import com.veterinaria.ms_citas.feign.VeterinarioClient;
import com.veterinaria.ms_citas.model.Cita;
import com.veterinaria.ms_citas.model.EstadoCita;
import com.veterinaria.ms_citas.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CitaService {

    private final CitaRepository citaRepository;
    private final VeterinarioClient veterinarioClient;
    private final MascotaClient mascotaClient;
    private final UsuarioClient usuarioClient;

    public List<CitaResponseDto> findAll() {
        log.info("Obteniendo todas las citas");
        return citaRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CitaResponseDto findById(Long id) {
        log.info("Buscando cita con id={}", id);
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Cita no encontrada con id: " + id));
        return toDto(cita);
    }

    public List<CitaResponseDto> findByDueno(Long idDueno) {
        log.info("Buscando citas del dueno id={}", idDueno);
        return citaRepository.findByIdDueno(idDueno)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<CitaResponseDto> findByVeterinario(Long idVeterinario) {
        log.info("Buscando citas del veterinario id={}", idVeterinario);
        return citaRepository.findByIdVeterinario(idVeterinario)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<CitaResponseDto> findByMascota(Long idMascota) {
        log.info("Buscando citas de la mascota id={}", idMascota);
        return citaRepository.findByIdMascota(idMascota)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CitaResponseDto save(CitaRequestDto dto) {
        log.info("Creando cita para mascota={} con vet={}",
                dto.getIdMascota(), dto.getIdVeterinario());

        // Verificar que el veterinario existe y está disponible via Feign
        Boolean disponible = veterinarioClient.isDisponible(dto.getIdVeterinario());
        if (!disponible) {
            throw new RuntimeException(
                    "El veterinario no está disponible para citas");
        }

        // Verificar que la mascota existe via Feign
        MascotaDto mascota = mascotaClient.getMascota(dto.getIdMascota());
        if (!mascota.getIdDueno().equals(dto.getIdDueno())) {
            throw new RuntimeException(
                    "La mascota no pertenece al dueño indicado");
        }

        // Verificar que el dueño existe y está activo via Feign
        DuenoDto dueno = usuarioClient.getDueno(dto.getIdDueno());
        if (dueno == null || Boolean.FALSE.equals(dueno.getActivo())) {
            throw new RuntimeException(
                    "El dueño no existe o no está activo");
        }

        Cita cita = Cita.builder()
                .idMascota(dto.getIdMascota())
                .idVeterinario(dto.getIdVeterinario())
                .idDueno(dto.getIdDueno())
                .fechaHora(dto.getFechaHora())
                .motivo(dto.getMotivo())
                .estado(EstadoCita.PENDIENTE)
                .createdAt(LocalDateTime.now())
                .build();

        Cita guardada = citaRepository.save(cita);
        log.info("Cita creada con id={}", guardada.getId());
        return toDto(guardada);
    }

    public CitaResponseDto cambiarEstado(Long id, EstadoCita estado) {
        log.info("Cambiando estado de cita id={} a {}", id, estado);
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Cita no encontrada con id: " + id));
        cita.setEstado(estado);
        return toDto(citaRepository.save(cita));
    }

    public void cancelar(Long id) {
        log.info("Cancelando cita id={}", id);
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Cita no encontrada con id: " + id));
        cita.setEstado(EstadoCita.CANCELADA);
        citaRepository.save(cita);
    }

    private CitaResponseDto toDto(Cita cita) {
        return CitaResponseDto.builder()
                .id(cita.getId())
                .idMascota(cita.getIdMascota())
                .idVeterinario(cita.getIdVeterinario())
                .idDueno(cita.getIdDueno())
                .fechaHora(cita.getFechaHora())
                .motivo(cita.getMotivo())
                .estado(cita.getEstado())
                .createdAt(cita.getCreatedAt())
                .build();
    }
}