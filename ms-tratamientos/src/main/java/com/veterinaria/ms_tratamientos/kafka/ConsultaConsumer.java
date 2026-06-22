package com.veterinaria.ms_tratamientos.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.ms_tratamientos.dto.ConsultaEventDto;
import com.veterinaria.ms_tratamientos.model.Tratamiento;
import com.veterinaria.ms_tratamientos.repository.TratamientoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultaConsumer {

    private final TratamientoRepository tratamientoRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "consulta.registrada",
            groupId = "ms-tratamientos-group"
    )
    public void onConsultaRegistrada(String mensaje) {
        try {
            log.info("Mensaje recibido del topico 'consulta.registrada': {}", mensaje);
            ConsultaEventDto evento = objectMapper.readValue(mensaje, ConsultaEventDto.class);
            log.info("Evento deserializado: consulta id={}", evento.getIdConsulta());

            Tratamiento tratamiento = Tratamiento.builder()
                    .idConsulta(evento.getIdConsulta())
                    .idMascota(evento.getIdMascota())
                    .medicamento("Pendiente de asignar")
                    .dosis("Pendiente")
                    .frecuencia("Pendiente")
                    .duracionDias(0)
                    .indicaciones("Tratamiento generado automaticamente desde consulta: "
                            + evento.getDiagnostico())
                    .createdAt(LocalDateTime.now())
                    .build();

            tratamientoRepository.save(tratamiento);
            log.info("Tratamiento inicial creado para consulta id={}", evento.getIdConsulta());

        } catch (Exception e) {
            log.error("Error procesando evento de consulta: {}", e.getMessage());
        }
    }
}