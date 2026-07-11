package com.veterinaria.ms_facturacion.service;

import com.veterinaria.ms_facturacion.dto.DuenoDto;
import com.veterinaria.ms_facturacion.dto.FacturaRequestDto;
import com.veterinaria.ms_facturacion.dto.FacturaResponseDto;
import com.veterinaria.ms_facturacion.feign.UsuarioClient;
import com.veterinaria.ms_facturacion.model.EstadoFactura;
import com.veterinaria.ms_facturacion.model.Factura;
import com.veterinaria.ms_facturacion.repository.FacturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FacturacionService - Pruebas Unitarias (con Feign mockeado)")
class FacturacionServiceTest {

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private FacturacionService facturacionService;

    private Factura facturaEjemplo;
    private FacturaRequestDto requestDtoEjemplo;
    private DuenoDto duenoActivoEjemplo;

    @BeforeEach
    void setUp() {
        facturaEjemplo = Factura.builder()
                .id(1L)
                .idConsulta(1L)
                .idMascota(1L)
                .idDueno(1L)
                .total(new BigDecimal("45000"))
                .estado(EstadoFactura.PENDIENTE)
                .observaciones("Consulta general")
                .createdAt(LocalDateTime.now())
                .build();

        requestDtoEjemplo = new FacturaRequestDto();
        requestDtoEjemplo.setIdConsulta(1L);
        requestDtoEjemplo.setIdMascota(1L);
        requestDtoEjemplo.setIdDueno(1L);
        requestDtoEjemplo.setTotal(new BigDecimal("45000"));
        requestDtoEjemplo.setObservaciones("Consulta general");

        // Dueno activo (happy path)
        duenoActivoEjemplo = new DuenoDto();
        duenoActivoEjemplo.setId(1L);
        duenoActivoEjemplo.setActivo(true);
    }

    // findAll() - lista completa de facturas
    @Test
    @DisplayName("findAll: deberia retornar todas las facturas")
    void findAll_retornaListaDeFacturas() {
        when(facturaRepository.findAll()).thenReturn(List.of(facturaEjemplo));

        List<FacturaResponseDto> resultado = facturacionService.findAll();

        assertEquals(1, resultado.size());
        verify(facturaRepository, times(1)).findAll();
    }

    // findById() - caso exitoso y de error
    @Test
    @DisplayName("findById: deberia retornar la factura cuando el id existe")
    void findById_cuandoExiste_retornaFacturaResponseDto() {
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(facturaEjemplo));

        FacturaResponseDto resultado = facturacionService.findById(1L);

        assertNotNull(resultado);
        assertEquals(EstadoFactura.PENDIENTE, resultado.getEstado());
        verify(facturaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById: deberia lanzar excepcion cuando el id no existe")
    void findById_cuandoNoExiste_lanzaRuntimeException() {
        when(facturaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> facturacionService.findById(99L));

        assertEquals("Factura no encontrada con id: 99", ex.getMessage());
    }

    // findByDueno() y findByEstado()
    @Test
    @DisplayName("findByDueno: deberia retornar las facturas del dueno")
    void findByDueno_retornaFacturasDelDueno() {
        when(facturaRepository.findByIdDueno(1L)).thenReturn(List.of(facturaEjemplo));

        List<FacturaResponseDto> resultado = facturacionService.findByDueno(1L);

        assertEquals(1, resultado.size());
        verify(facturaRepository, times(1)).findByIdDueno(1L);
    }

    @Test
    @DisplayName("findByEstado: deberia retornar las facturas con el estado indicado")
    void findByEstado_retornaFacturasConElEstadoIndicado() {
        when(facturaRepository.findByEstado(EstadoFactura.PENDIENTE))
                .thenReturn(List.of(facturaEjemplo));

        List<FacturaResponseDto> resultado = facturacionService.findByEstado(EstadoFactura.PENDIENTE);

        assertEquals(1, resultado.size());
        verify(facturaRepository, times(1)).findByEstado(EstadoFactura.PENDIENTE);
    }

    // save() - creacion de factura, valida dueno activo via Feign, siempre arranca en PENDIENTE
    @Test
    @DisplayName("save: deberia crear la factura con estado PENDIENTE cuando el dueno esta activo")
    void save_creaFacturaConEstadoPendiente() {
        // Given
        when(usuarioClient.getDueno(1L)).thenReturn(duenoActivoEjemplo);
        when(facturaRepository.save(any(Factura.class))).thenReturn(facturaEjemplo);

        // When
        FacturaResponseDto resultado = facturacionService.save(requestDtoEjemplo);

        // Then
        assertNotNull(resultado);
        assertEquals(EstadoFactura.PENDIENTE, resultado.getEstado());
        verify(usuarioClient, times(1)).getDueno(1L);
        verify(facturaRepository, times(1)).save(any(Factura.class));
    }

    @Test
    @DisplayName("save: deberia lanzar excepcion cuando el dueno no existe o no esta activo")
    void save_cuandoDuenoNoActivo_lanzaRuntimeException() {
        // Given
        DuenoDto duenoInactivo = new DuenoDto();
        duenoInactivo.setId(1L);
        duenoInactivo.setActivo(false);
        when(usuarioClient.getDueno(1L)).thenReturn(duenoInactivo);

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> facturacionService.save(requestDtoEjemplo));

        assertEquals("El dueño no existe o no está activo, no se puede facturar",
                ex.getMessage());
        verify(facturaRepository, never()).save(any(Factura.class));
    }

    @Test
    @DisplayName("save: deberia lanzar excepcion cuando el dueno no existe (Feign retorna null)")
    void save_cuandoDuenoNoExiste_lanzaRuntimeException() {
        // Given
        when(usuarioClient.getDueno(1L)).thenReturn(null);

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> facturacionService.save(requestDtoEjemplo));

        assertEquals("El dueño no existe o no está activo, no se puede facturar",
                ex.getMessage());
        verify(facturaRepository, never()).save(any(Factura.class));
    }

    // cambiarEstado() - actualizacion de estado de una factura
    @Test
    @DisplayName("cambiarEstado: deberia actualizar el estado de PENDIENTE a PAGADA")
    void cambiarEstado_actualizaEstadoCorrectamente() {
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(facturaEjemplo));
        when(facturaRepository.save(any(Factura.class))).thenReturn(facturaEjemplo);

        FacturaResponseDto resultado = facturacionService.cambiarEstado(1L, EstadoFactura.PAGADA);

        assertEquals(EstadoFactura.PAGADA, facturaEjemplo.getEstado());
        verify(facturaRepository, times(1)).save(facturaEjemplo);
    }

    @Test
    @DisplayName("cambiarEstado: deberia lanzar excepcion si la factura no existe")
    void cambiarEstado_cuandoNoExiste_lanzaRuntimeException() {
        when(facturaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> facturacionService.cambiarEstado(99L, EstadoFactura.PAGADA));
        verify(facturaRepository, never()).save(any(Factura.class));
    }
}