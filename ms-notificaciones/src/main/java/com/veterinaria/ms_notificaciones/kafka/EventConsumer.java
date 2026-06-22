package com.veterinaria.ms_notificaciones.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.ms_notificaciones.dto.CitaEventDto;
import com.veterinaria.ms_notificaciones.dto.ConsultaEventDto;
import com.veterinaria.ms_notificaciones.model.Notificacion;
import com.veterinaria.ms_notificaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {

    private final NotificacionRepository notificacionRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "consulta.registrada",
            groupId = "ms-notificaciones-consulta-group"
    )
    public void onConsultaRegistrada(String mensaje) {
        try {
            log.info("Notificacion: evento consulta.registrada recibido");
            ConsultaEventDto evento = objectMapper.readValue(mensaje, ConsultaEventDto.class);

            Notificacion notificacion = Notificacion.builder()
                    .tipo("CONSULTA_REGISTRADA")
                    .idDestinatario(evento.getIdMascota())
                    .mensaje("Se ha registrado una consulta para su mascota. Diagnostico: "
                            + evento.getDiagnostico())
                    .enviado(true)
                    .fechaEnvio(LocalDateTime.now())
                    .build();

            notificacionRepository.save(notificacion);
            log.info("Notificacion de consulta guardada para mascota id={}",
                    evento.getIdMascota());

        } catch (Exception e) {
            log.error("Error procesando evento consulta.registrada: {}", e.getMessage());
        }
    }

    @KafkaListener(
            topics = "cita.confirmada",
            groupId = "ms-notificaciones-cita-group"
    )
    public void onCitaConfirmada(String mensaje) {
        try {
            log.info("Notificacion: evento cita.confirmada recibido");
            CitaEventDto evento = objectMapper.readValue(mensaje, CitaEventDto.class);

            Notificacion notificacion = Notificacion.builder()
                    .tipo("CITA_CONFIRMADA")
                    .idDestinatario(evento.getIdDueno())
                    .mensaje("Su cita ha sido confirmada para: "
                            + evento.getFechaHora())
                    .enviado(true)
                    .fechaEnvio(LocalDateTime.now())
                    .build();

            notificacionRepository.save(notificacion);
            log.info("Notificacion de cita guardada para dueno id={}",
                    evento.getIdDueno());

        } catch (Exception e) {
            log.error("Error procesando evento cita.confirmada: {}", e.getMessage());
        }
    }
}