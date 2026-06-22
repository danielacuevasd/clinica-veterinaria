package com.veterinaria.ms_inventario.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.ms_inventario.dto.ConsultaEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultaConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "consulta.registrada",
            groupId = "ms-inventario-group"
    )
    public void onConsultaRegistrada(String mensaje) {
        try {
            log.info("Evento recibido en ms-inventario: {}", mensaje);
            ConsultaEventDto evento = objectMapper.readValue(mensaje, ConsultaEventDto.class);
            log.info("Consulta registrada id={} para mascota id={}",
                    evento.getIdConsulta(), evento.getIdMascota());
            // El descuento de stock se hace manualmente via endpoint POST /inventario/movimientos
            // cuando el veterinario asigna el medicamento en ms-tratamientos
        } catch (Exception e) {
            log.error("Error procesando evento en ms-inventario: {}", e.getMessage());
        }
    }
}