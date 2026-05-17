package com.veterinaria.ms_veterinarios.service;

import com.veterinaria.ms_veterinarios.dto.HorarioRequestDto;
import com.veterinaria.ms_veterinarios.dto.HorarioResponseDto;
import com.veterinaria.ms_veterinarios.dto.VeterinarioRequestDto;
import com.veterinaria.ms_veterinarios.dto.VeterinarioResponseDto;
import com.veterinaria.ms_veterinarios.model.Horario;
import com.veterinaria.ms_veterinarios.model.Veterinario;
import com.veterinaria.ms_veterinarios.repository.HorarioRepository;
import com.veterinaria.ms_veterinarios.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final HorarioRepository horarioRepository;

    public List<VeterinarioResponseDto> findAll() {
        log.info("Obteniendo todos los veterinarios activos");
        return veterinarioRepository.findByActivoTrue()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public VeterinarioResponseDto findById(Long id) {
        log.info("Buscando veterinario con id={}", id);
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Veterinario no encontrado con id: " + id));
        return toDto(veterinario);
    }

    public List<VeterinarioResponseDto> findDisponibles() {
        log.info("Obteniendo veterinarios disponibles");
        return veterinarioRepository.findByDisponibleTrue()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<VeterinarioResponseDto> findByEspecialidad(String especialidad) {
        log.info("Buscando veterinarios por especialidad={}", especialidad);
        return veterinarioRepository.findByEspecialidadIgnoreCase(especialidad)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<VeterinarioResponseDto> search(String nombre) {
        log.info("Buscando veterinarios por nombre={}", nombre);
        return veterinarioRepository
                .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
                        nombre, nombre)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Boolean isDisponible(Long id) {
        log.info("Verificando disponibilidad del veterinario id={}", id);
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Veterinario no encontrado con id: " + id));
        return veterinario.getDisponible();
    }

    public VeterinarioResponseDto save(VeterinarioRequestDto dto) {
        log.info("Registrando nuevo veterinario: {}", dto.getEmail());
        if (veterinarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException(
                    "Ya existe un veterinario con el email: " + dto.getEmail());
        }
        Veterinario veterinario = Veterinario.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .especialidad(dto.getEspecialidad())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .disponible(true)
                .activo(true)
                .createdAt(LocalDateTime.now())
                .build();
        return toDto(veterinarioRepository.save(veterinario));
    }

    public VeterinarioResponseDto update(Long id, VeterinarioRequestDto dto) {
        log.info("Actualizando veterinario id={}", id);
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Veterinario no encontrado con id: " + id));
        veterinario.setNombre(dto.getNombre());
        veterinario.setApellido(dto.getApellido());
        veterinario.setEspecialidad(dto.getEspecialidad());
        veterinario.setEmail(dto.getEmail());
        veterinario.setTelefono(dto.getTelefono());
        return toDto(veterinarioRepository.save(veterinario));
    }

    public VeterinarioResponseDto cambiarDisponibilidad(Long id, Boolean disponible) {
        log.info("Cambiando disponibilidad del veterinario id={} a {}", id, disponible);
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Veterinario no encontrado con id: " + id));
        veterinario.setDisponible(disponible);
        return toDto(veterinarioRepository.save(veterinario));
    }

    public void delete(Long id) {
        log.info("Eliminando veterinario id={}", id);
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Veterinario no encontrado con id: " + id));
        veterinario.setActivo(false);
        veterinarioRepository.save(veterinario);
    }

    public HorarioResponseDto addHorario(HorarioRequestDto dto) {
        log.info("Agregando horario al veterinario id={}", dto.getIdVeterinario());
        veterinarioRepository.findById(dto.getIdVeterinario())
                .orElseThrow(() -> new RuntimeException(
                        "Veterinario no encontrado con id: " + dto.getIdVeterinario()));
        Horario horario = Horario.builder()
                .idVeterinario(dto.getIdVeterinario())
                .diaSemana(dto.getDiaSemana())
                .horaInicio(dto.getHoraInicio())
                .horaFin(dto.getHoraFin())
                .build();
        return toDtoHorario(horarioRepository.save(horario));
    }

    public List<HorarioResponseDto> getHorarios(Long idVeterinario) {
        log.info("Obteniendo horarios del veterinario id={}", idVeterinario);
        return horarioRepository.findByIdVeterinario(idVeterinario)
                .stream()
                .map(this::toDtoHorario)
                .collect(Collectors.toList());
    }

    private VeterinarioResponseDto toDto(Veterinario v) {
        return VeterinarioResponseDto.builder()
                .id(v.getId())
                .nombre(v.getNombre())
                .apellido(v.getApellido())
                .especialidad(v.getEspecialidad())
                .email(v.getEmail())
                .telefono(v.getTelefono())
                .disponible(v.getDisponible())
                .activo(v.getActivo())
                .createdAt(v.getCreatedAt())
                .build();
    }

    private HorarioResponseDto toDtoHorario(Horario h) {
        return HorarioResponseDto.builder()
                .id(h.getId())
                .idVeterinario(h.getIdVeterinario())
                .diaSemana(h.getDiaSemana())
                .horaInicio(h.getHoraInicio())
                .horaFin(h.getHoraFin())
                .build();
    }
}