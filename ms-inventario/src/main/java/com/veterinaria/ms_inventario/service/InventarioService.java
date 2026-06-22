package com.veterinaria.ms_inventario.service;

import com.veterinaria.ms_inventario.dto.MedicamentoRequestDto;
import com.veterinaria.ms_inventario.dto.MedicamentoResponseDto;
import com.veterinaria.ms_inventario.dto.MovimientoRequestDto;
import com.veterinaria.ms_inventario.model.Medicamento;
import com.veterinaria.ms_inventario.model.MovimientoStock;
import com.veterinaria.ms_inventario.model.TipoMovimiento;
import com.veterinaria.ms_inventario.repository.MedicamentoRepository;
import com.veterinaria.ms_inventario.repository.MovimientoStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioService {

    private final MedicamentoRepository medicamentoRepository;
    private final MovimientoStockRepository movimientoStockRepository;

    public List<MedicamentoResponseDto> findAll() {
        log.info("Obteniendo todos los medicamentos activos");
        return medicamentoRepository.findByActivoTrue()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public MedicamentoResponseDto findById(Long id) {
        log.info("Buscando medicamento id={}", id);
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Medicamento no encontrado con id: " + id));
        return toDto(medicamento);
    }

    public List<MedicamentoResponseDto> findStockBajo(Integer limite) {
        log.info("Buscando medicamentos con stock menor a {}", limite);
        return medicamentoRepository.findByStockLessThan(limite)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<MedicamentoResponseDto> search(String nombre) {
        log.info("Buscando medicamentos por nombre={}", nombre);
        return medicamentoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public MedicamentoResponseDto save(MedicamentoRequestDto dto) {
        log.info("Registrando medicamento: {}", dto.getNombre());
        if (medicamentoRepository.existsByNombre(dto.getNombre())) {
            throw new RuntimeException(
                    "Ya existe un medicamento con el nombre: " + dto.getNombre());
        }
        Medicamento medicamento = Medicamento.builder()
                .nombre(dto.getNombre())
                .stock(dto.getStock())
                .unidad(dto.getUnidad())
                .precioUnitario(dto.getPrecioUnitario())
                .activo(true)
                .createdAt(LocalDateTime.now())
                .build();
        return toDto(medicamentoRepository.save(medicamento));
    }

    public MedicamentoResponseDto update(Long id, MedicamentoRequestDto dto) {
        log.info("Actualizando medicamento id={}", id);
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Medicamento no encontrado con id: " + id));
        medicamento.setNombre(dto.getNombre());
        medicamento.setUnidad(dto.getUnidad());
        medicamento.setPrecioUnitario(dto.getPrecioUnitario());
        return toDto(medicamentoRepository.save(medicamento));
    }

    public MedicamentoResponseDto registrarMovimiento(MovimientoRequestDto dto) {
        log.info("Registrando movimiento de stock para medicamento id={}", dto.getIdMedicamento());
        Medicamento medicamento = medicamentoRepository.findById(dto.getIdMedicamento())
                .orElseThrow(() -> new RuntimeException(
                        "Medicamento no encontrado con id: " + dto.getIdMedicamento()));

        if (dto.getTipo() == TipoMovimiento.SALIDA) {
            if (medicamento.getStock() < dto.getCantidad()) {
                throw new RuntimeException("Stock insuficiente. Stock actual: "
                        + medicamento.getStock());
            }
            medicamento.setStock(medicamento.getStock() - dto.getCantidad());
        } else {
            medicamento.setStock(medicamento.getStock() + dto.getCantidad());
        }

        medicamentoRepository.save(medicamento);

        MovimientoStock movimiento = MovimientoStock.builder()
                .idMedicamento(dto.getIdMedicamento())
                .tipo(dto.getTipo())
                .cantidad(dto.getCantidad())
                .motivo(dto.getMotivo())
                .fecha(LocalDateTime.now())
                .build();
        movimientoStockRepository.save(movimiento);

        log.info("Movimiento registrado. Stock actual: {}", medicamento.getStock());
        return toDto(medicamento);
    }

    private MedicamentoResponseDto toDto(Medicamento m) {
        return MedicamentoResponseDto.builder()
                .id(m.getId())
                .nombre(m.getNombre())
                .stock(m.getStock())
                .unidad(m.getUnidad())
                .precioUnitario(m.getPrecioUnitario())
                .activo(m.getActivo())
                .createdAt(m.getCreatedAt())
                .build();
    }
}