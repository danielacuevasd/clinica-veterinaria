package com.veterinaria.ms_consultas.service;

import com.veterinaria.ms_consultas.dto.ConsultaEventDto;
import com.veterinaria.ms_consultas.dto.ConsultaRequestDto;
import com.veterinaria.ms_consultas.dto.ConsultaResponseDto;
import com.veterinaria.ms_consultas.dto.MascotaDto;
import com.veterinaria.ms_consultas.dto.VeterinarioDto;
import com.veterinaria.ms_consultas.feign.MascotaClient;
import com.veterinaria.ms_consultas.feign.VeterinarioClient;
import com.veterinaria.ms_consultas.kafka.ConsultaProducer;
import com.veterinaria.ms_consultas.model.Consulta;
import com.veterinaria.ms_consultas.repository.ConsultaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final ConsultaProducer consultaProducer;
    private final MascotaClient mascotaClient;
    private final VeterinarioClient veterinarioClient;

    public List<ConsultaResponseDto> findAll() {
        log.info("Obteniendo todas las consultas");
        return consultaRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ConsultaResponseDto findById(Long id) {
        log.info("Buscando consulta con id={}", id);
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Consulta no encontrada con id: " + id));
        return toDto(consulta);
    }

    public List<ConsultaResponseDto> findByMascota(Long idMascota) {
        log.info("Buscando consultas de la mascota id={}", idMascota);
        return consultaRepository.findByIdMascotaOrderByFechaDesc(idMascota)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ConsultaResponseDto> findByVeterinario(Long idVeterinario) {
        log.info("Buscando consultas del veterinario id={}", idVeterinario);
        return consultaRepository.findByIdVeterinario(idVeterinario)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ConsultaResponseDto save(ConsultaRequestDto dto) {
        log.info("Registrando consulta para mascota id={}", dto.getIdMascota());

        // Verificar que la mascota existe y está activa via Feign
        MascotaDto mascota = mascotaClient.getMascota(dto.getIdMascota());
        if (mascota == null || Boolean.FALSE.equals(mascota.getActivo())) {
            throw new RuntimeException(
                    "La mascota no existe o no está activa");
        }
        if (!mascota.getIdDueno().equals(dto.getIdDueno())) {
            throw new RuntimeException(
                    "La mascota no pertenece al dueño indicado");
        }

        // Verificar que el veterinario existe y está activo via Feign
        VeterinarioDto veterinario = veterinarioClient.getVeterinario(dto.getIdVeterinario());
        if (veterinario == null || Boolean.FALSE.equals(veterinario.getActivo())) {
            throw new RuntimeException(
                    "El veterinario no existe o no está activo");
        }

        Consulta consulta = Consulta.builder()
                .idCita(dto.getIdCita())
                .idMascota(dto.getIdMascota())
                .idVeterinario(dto.getIdVeterinario())
                .idDueno(dto.getIdDueno())
                .diagnostico(dto.getDiagnostico())
                .peso(dto.getPeso())
                .temperatura(dto.getTemperatura())
                .observaciones(dto.getObservaciones())
                .fecha(LocalDateTime.now())
                .build();

        Consulta guardada = consultaRepository.save(consulta);
        log.info("Consulta guardada con id={}", guardada.getId());

        // Publicar evento Kafka para que otros ms reaccionen
        ConsultaEventDto evento = ConsultaEventDto.builder()
                .idConsulta(guardada.getId())
                .idCita(guardada.getIdCita())
                .idMascota(guardada.getIdMascota())
                .idVeterinario(guardada.getIdVeterinario())
                .idDueno(guardada.getIdDueno())
                .diagnostico(guardada.getDiagnostico())
                .fecha(guardada.getFecha())
                .build();

        consultaProducer.publicarConsultaRegistrada(evento);

        return toDto(guardada);
    }

    private ConsultaResponseDto toDto(Consulta consulta) {
        return ConsultaResponseDto.builder()
                .id(consulta.getId())
                .idCita(consulta.getIdCita())
                .idMascota(consulta.getIdMascota())
                .idVeterinario(consulta.getIdVeterinario())
                .diagnostico(consulta.getDiagnostico())
                .peso(consulta.getPeso())
                .temperatura(consulta.getTemperatura())
                .observaciones(consulta.getObservaciones())
                .fecha(consulta.getFecha())
                .build();
    }
}