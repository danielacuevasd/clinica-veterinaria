package com.veterinaria.ms_mascotas.service;

import com.veterinaria.ms_mascotas.dto.HistorialMedicoRequestDto;
import com.veterinaria.ms_mascotas.dto.HistorialMedicoResponseDto;
import com.veterinaria.ms_mascotas.dto.MascotaRequestDto;
import com.veterinaria.ms_mascotas.dto.MascotaResponseDto;
import com.veterinaria.ms_mascotas.model.HistorialMedico;
import com.veterinaria.ms_mascotas.model.Mascota;
import com.veterinaria.ms_mascotas.repository.HistorialMedicoRepository;
import com.veterinaria.ms_mascotas.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final HistorialMedicoRepository historialMedicoRepository;

    public List<MascotaResponseDto> findAll() {
        log.info("Obteniendo todas las mascotas activas");
        return mascotaRepository.findByActivoTrue()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public MascotaResponseDto findById(Long id) {
        log.info("Buscando mascota con id={}", id);
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Mascota no encontrada con id: " + id));
        return toDto(mascota);
    }

    public List<MascotaResponseDto> findByDueno(Long idDueno) {
        log.info("Buscando mascotas del dueno id={}", idDueno);
        return mascotaRepository.findByIdDuenoAndActivoTrue(idDueno)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<MascotaResponseDto> findByEspecie(String especie) {
        log.info("Buscando mascotas por especie={}", especie);
        return mascotaRepository.findByEspecieIgnoreCase(especie)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<MascotaResponseDto> search(String nombre) {
        log.info("Buscando mascotas por nombre={}", nombre);
        return mascotaRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public MascotaResponseDto save(MascotaRequestDto dto) {
        log.info("Registrando nueva mascota: {}", dto.getNombre());
        Mascota mascota = Mascota.builder()
                .nombre(dto.getNombre())
                .especie(dto.getEspecie())
                .raza(dto.getRaza())
                .fechaNacimiento(dto.getFechaNacimiento())
                .idDueno(dto.getIdDueno())
                .activo(true)
                .createdAt(LocalDateTime.now())
                .build();
        return toDto(mascotaRepository.save(mascota));
    }

    public MascotaResponseDto update(Long id, MascotaRequestDto dto) {
        log.info("Actualizando mascota id={}", id);
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Mascota no encontrada con id: " + id));
        mascota.setNombre(dto.getNombre());
        mascota.setEspecie(dto.getEspecie());
        mascota.setRaza(dto.getRaza());
        mascota.setFechaNacimiento(dto.getFechaNacimiento());
        mascota.setIdDueno(dto.getIdDueno());
        return toDto(mascotaRepository.save(mascota));
    }

    public void delete(Long id) {
        log.info("Eliminando mascota id={}", id);
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Mascota no encontrada con id: " + id));
        mascota.setActivo(false);
        mascotaRepository.save(mascota);
    }

    public HistorialMedicoResponseDto addHistorial(HistorialMedicoRequestDto dto) {
        log.info("Agregando historial a mascota id={}", dto.getIdMascota());
        mascotaRepository.findById(dto.getIdMascota())
                .orElseThrow(() -> new RuntimeException(
                        "Mascota no encontrada con id: " + dto.getIdMascota()));
        HistorialMedico historial = HistorialMedico.builder()
                .idMascota(dto.getIdMascota())
                .descripcion(dto.getDescripcion())
                .fecha(dto.getFecha())
                .build();
        return toDtoHistorial(historialMedicoRepository.save(historial));
    }

    public List<HistorialMedicoResponseDto> getHistorial(Long idMascota) {
        log.info("Obteniendo historial de mascota id={}", idMascota);
        return historialMedicoRepository
                .findByIdMascotaOrderByFechaDesc(idMascota)
                .stream()
                .map(this::toDtoHistorial)
                .collect(Collectors.toList());
    }

    private MascotaResponseDto toDto(Mascota mascota) {
        return MascotaResponseDto.builder()
                .id(mascota.getId())
                .nombre(mascota.getNombre())
                .especie(mascota.getEspecie())
                .raza(mascota.getRaza())
                .fechaNacimiento(mascota.getFechaNacimiento())
                .idDueno(mascota.getIdDueno())
                .activo(mascota.getActivo())
                .createdAt(mascota.getCreatedAt())
                .build();
    }

    private HistorialMedicoResponseDto toDtoHistorial(HistorialMedico h) {
        return HistorialMedicoResponseDto.builder()
                .id(h.getId())
                .idMascota(h.getIdMascota())
                .descripcion(h.getDescripcion())
                .fecha(h.getFecha())
                .build();
    }
}
