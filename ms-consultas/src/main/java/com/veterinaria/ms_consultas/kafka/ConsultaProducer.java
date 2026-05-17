package com.veterinaria.ms_consultas.kafka;

import com.veterinaria.ms_consultas.dto.ConsultaEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultaProducer {

    private final KafkaTemplate<String, ConsultaEventDto> kafkaTemplate;

    private static final String TOPIC = "consulta.registrada";

    public void publicarConsultaRegistrada(ConsultaEventDto evento) {
        log.info("Publicando evento en topico '{}': consulta id={}",
                TOPIC, evento.getIdConsulta());
        kafkaTemplate.send(TOPIC, evento);
        log.info("Evento publicado exitosamente");
    }
}
