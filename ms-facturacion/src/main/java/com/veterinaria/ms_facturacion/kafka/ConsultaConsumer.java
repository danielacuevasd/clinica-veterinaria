package com.veterinaria.ms_facturacion.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.ms_facturacion.dto.ConsultaEventDto;
import com.veterinaria.ms_facturacion.model.Factura;
import com.veterinaria.ms_facturacion.model.EstadoFactura;
import com.veterinaria.ms_facturacion.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultaConsumer {

    private final FacturaRepository facturaRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "consulta.registrada",
            groupId = "ms-facturacion-group"
    )
    public void onConsultaRegistrada(String mensaje) {
        try {
            log.info("Evento recibido en ms-facturacion: {}", mensaje);
            ConsultaEventDto evento = objectMapper.readValue(mensaje, ConsultaEventDto.class);

            Factura factura = Factura.builder()
                    .idConsulta(evento.getIdConsulta())
                    .idMascota(evento.getIdMascota())
                    .idDueno(0L)
                    .total(BigDecimal.valueOf(25000))
                    .estado(EstadoFactura.PENDIENTE)
                    .observaciones("Factura generada automaticamente por consulta: "
                            + evento.getDiagnostico())
                    .createdAt(LocalDateTime.now())
                    .build();

            facturaRepository.save(factura);
            log.info("Factura creada para consulta id={}", evento.getIdConsulta());

        } catch (Exception e) {
            log.error("Error procesando evento en ms-facturacion: {}", e.getMessage());
        }
    }
}