package com.veterinaria.ms_facturacion.service;

import com.veterinaria.ms_facturacion.dto.DuenoDto;
import com.veterinaria.ms_facturacion.dto.FacturaRequestDto;
import com.veterinaria.ms_facturacion.dto.FacturaResponseDto;
import com.veterinaria.ms_facturacion.feign.UsuarioClient;
import com.veterinaria.ms_facturacion.model.EstadoFactura;
import com.veterinaria.ms_facturacion.model.Factura;
import com.veterinaria.ms_facturacion.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacturacionService {

    private final FacturaRepository facturaRepository;
    private final UsuarioClient usuarioClient;

    public List<FacturaResponseDto> findAll() {
        log.info("Obteniendo todas las facturas");
        return facturaRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public FacturaResponseDto findById(Long id) {
        log.info("Buscando factura id={}", id);
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Factura no encontrada con id: " + id));
        return toDto(factura);
    }

    public List<FacturaResponseDto> findByDueno(Long idDueno) {
        log.info("Buscando facturas del dueno id={}", idDueno);
        return facturaRepository.findByIdDueno(idDueno)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<FacturaResponseDto> findByEstado(EstadoFactura estado) {
        log.info("Buscando facturas con estado={}", estado);
        return facturaRepository.findByEstado(estado)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public FacturaResponseDto save(FacturaRequestDto dto) {
        log.info("Creando factura para consulta id={}", dto.getIdConsulta());

        // Verificar que el dueño existe y está activo via Feign antes de facturar
        DuenoDto dueno = usuarioClient.getDueno(dto.getIdDueno());
        if (dueno == null || Boolean.FALSE.equals(dueno.getActivo())) {
            throw new RuntimeException(
                    "El dueño no existe o no está activo, no se puede facturar");
        }

        Factura factura = Factura.builder()
                .idConsulta(dto.getIdConsulta())
                .idMascota(dto.getIdMascota())
                .idDueno(dto.getIdDueno())
                .total(dto.getTotal())
                .estado(EstadoFactura.PENDIENTE)
                .observaciones(dto.getObservaciones())
                .createdAt(LocalDateTime.now())
                .build();
        return toDto(facturaRepository.save(factura));
    }

    public FacturaResponseDto cambiarEstado(Long id, EstadoFactura estado) {
        log.info("Cambiando estado de factura id={} a {}", id, estado);
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Factura no encontrada con id: " + id));
        factura.setEstado(estado);
        return toDto(facturaRepository.save(factura));
    }

    private FacturaResponseDto toDto(Factura f) {
        return FacturaResponseDto.builder()
                .id(f.getId())
                .idConsulta(f.getIdConsulta())
                .idMascota(f.getIdMascota())
                .idDueno(f.getIdDueno())
                .total(f.getTotal())
                .estado(f.getEstado())
                .observaciones(f.getObservaciones())
                .createdAt(f.getCreatedAt())
                .build();
    }
}