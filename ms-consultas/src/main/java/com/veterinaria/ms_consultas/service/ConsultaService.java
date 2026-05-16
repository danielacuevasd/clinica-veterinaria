package com.veterinaria.ms_consultas.service;

import com.veterinaria.ms_consultas.dto.ConsultaEventDto;
import com.veterinaria.ms_consultas.dto.ConsultaRequestDto;
import com.veterinaria.ms_consultas.dto.ConsultaResponseDto;
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

        Consulta consulta = Consulta.builder()
                .idCita(dto.getIdCita())
                .idMascota(dto.getIdMascota())
                .idVeterinario(dto.getIdVeterinario())
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
