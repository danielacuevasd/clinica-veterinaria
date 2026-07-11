package com.veterinaria.ms_inventario.service;

import com.veterinaria.ms_inventario.dto.MedicamentoRequestDto;
import com.veterinaria.ms_inventario.dto.MedicamentoResponseDto;
import com.veterinaria.ms_inventario.dto.MovimientoRequestDto;
import com.veterinaria.ms_inventario.model.Medicamento;
import com.veterinaria.ms_inventario.model.TipoMovimiento;
import com.veterinaria.ms_inventario.repository.MedicamentoRepository;
import com.veterinaria.ms_inventario.repository.MovimientoStockRepository;
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
@DisplayName("InventarioService - Pruebas Unitarias")
class InventarioServiceTest {

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @Mock
    private MovimientoStockRepository movimientoStockRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Medicamento medicamentoEjemplo;
    private MedicamentoRequestDto requestDtoEjemplo;

    @BeforeEach
    void setUp() {
        medicamentoEjemplo = Medicamento.builder()
                .id(1L)
                .nombre("Amoxicilina")
                .stock(50)
                .unidad("caja")
                .precioUnitario(new BigDecimal("12.50"))
                .activo(true)
                .createdAt(LocalDateTime.now())
                .build();

        requestDtoEjemplo = new MedicamentoRequestDto();
        requestDtoEjemplo.setNombre("Amoxicilina");
        requestDtoEjemplo.setStock(50);
        requestDtoEjemplo.setUnidad("caja");
        requestDtoEjemplo.setPrecioUnitario(new BigDecimal("12.50"));
    }

    // registrarMovimiento() - ENTRADA, SALIDA y stock insuficiente
    @Test
    @DisplayName("registrarMovimiento: ENTRADA deberia aumentar el stock correctamente")
    void registrarMovimiento_entrada_aumentaStockCorrectamente() {
        // Given
        MovimientoRequestDto movimientoDto = new MovimientoRequestDto();
        movimientoDto.setIdMedicamento(1L);
        movimientoDto.setTipo(TipoMovimiento.ENTRADA);
        movimientoDto.setCantidad(20);
        movimientoDto.setMotivo("Reposicion de stock");

        when(medicamentoRepository.findById(1L))
                .thenReturn(Optional.of(medicamentoEjemplo));
        when(medicamentoRepository.save(any(Medicamento.class)))
                .thenReturn(medicamentoEjemplo);

        // When
        MedicamentoResponseDto resultado = inventarioService.registrarMovimiento(movimientoDto);

        // Then: 50 (stock inicial) + 20 (entrada) = 70
        assertEquals(70, medicamentoEjemplo.getStock());
        verify(medicamentoRepository, times(1)).save(medicamentoEjemplo);
        verify(movimientoStockRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("registrarMovimiento: SALIDA deberia disminuir el stock cuando hay suficiente")
    void registrarMovimiento_salidaConStockSuficiente_disminuyeStockCorrectamente() {
        // Given
        MovimientoRequestDto movimientoDto = new MovimientoRequestDto();
        movimientoDto.setIdMedicamento(1L);
        movimientoDto.setTipo(TipoMovimiento.SALIDA);
        movimientoDto.setCantidad(30);
        movimientoDto.setMotivo("Uso en consulta");

        when(medicamentoRepository.findById(1L))
                .thenReturn(Optional.of(medicamentoEjemplo));
        when(medicamentoRepository.save(any(Medicamento.class)))
                .thenReturn(medicamentoEjemplo);

        // When
        MedicamentoResponseDto resultado = inventarioService.registrarMovimiento(movimientoDto);

        // Then: 50 (stock inicial) - 30 (salida) = 20
        assertEquals(20, medicamentoEjemplo.getStock());
        verify(medicamentoRepository, times(1)).save(medicamentoEjemplo);
        verify(movimientoStockRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("registrarMovimiento: SALIDA deberia lanzar excepcion si el stock es insuficiente")
    void registrarMovimiento_salidaConStockInsuficiente_lanzaRuntimeException() {
        // Given: se intenta sacar mas stock del disponible (50)
        MovimientoRequestDto movimientoDto = new MovimientoRequestDto();
        movimientoDto.setIdMedicamento(1L);
        movimientoDto.setTipo(TipoMovimiento.SALIDA);
        movimientoDto.setCantidad(100);
        movimientoDto.setMotivo("Uso en consulta");

        when(medicamentoRepository.findById(1L))
                .thenReturn(Optional.of(medicamentoEjemplo));

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> inventarioService.registrarMovimiento(movimientoDto));

        assertEquals("Stock insuficiente. Stock actual: 50", ex.getMessage());
        // Si el stock es insuficiente, NUNCA debe guardarse el movimiento
        verify(medicamentoRepository, never()).save(any(Medicamento.class));
        verify(movimientoStockRepository, never()).save(any());
    }

    @Test
    @DisplayName("registrarMovimiento: deberia lanzar excepcion si el medicamento no existe")
    void registrarMovimiento_medicamentoNoExiste_lanzaRuntimeException() {
        // Given
        MovimientoRequestDto movimientoDto = new MovimientoRequestDto();
        movimientoDto.setIdMedicamento(99L);
        movimientoDto.setTipo(TipoMovimiento.ENTRADA);
        movimientoDto.setCantidad(10);

        when(medicamentoRepository.findById(99L)).thenReturn(Optional.empty());

        // When + Then
        assertThrows(RuntimeException.class,
                () -> inventarioService.registrarMovimiento(movimientoDto));
        verify(movimientoStockRepository, never()).save(any());
    }

    // findAll() - lista de medicamentos activos
    @Test
    @DisplayName("findAll: deberia retornar todos los medicamentos activos")
    void findAll_retornaListaDeMedicamentosActivos() {
        when(medicamentoRepository.findByActivoTrue())
                .thenReturn(List.of(medicamentoEjemplo));

        List<MedicamentoResponseDto> resultado = inventarioService.findAll();

        assertEquals(1, resultado.size());
        verify(medicamentoRepository, times(1)).findByActivoTrue();
    }

    // findById() - caso exitoso y de error
    @Test
    @DisplayName("findById: deberia retornar el medicamento cuando el id existe")
    void findById_cuandoExiste_retornaMedicamentoResponseDto() {
        when(medicamentoRepository.findById(1L))
                .thenReturn(Optional.of(medicamentoEjemplo));

        MedicamentoResponseDto resultado = inventarioService.findById(1L);

        assertNotNull(resultado);
        assertEquals("Amoxicilina", resultado.getNombre());
        verify(medicamentoRepository, times(1)).findById(1L);
    }

    // findByNombreExacto() - usado por otros microservicios via Feign para validar stock
    @Test
    @DisplayName("findByNombreExacto: deberia retornar el medicamento cuando el nombre existe exactamente")
    void findByNombreExacto_cuandoExiste_retornaMedicamentoResponseDto() {
        when(medicamentoRepository.findByNombre("Amoxicilina"))
                .thenReturn(Optional.of(medicamentoEjemplo));

        MedicamentoResponseDto resultado = inventarioService.findByNombreExacto("Amoxicilina");

        assertNotNull(resultado);
        assertEquals("Amoxicilina", resultado.getNombre());
        verify(medicamentoRepository, times(1)).findByNombre("Amoxicilina");
    }

    @Test
    @DisplayName("findByNombreExacto: deberia lanzar excepcion cuando el nombre no existe")
    void findByNombreExacto_cuandoNoExiste_lanzaRuntimeException() {
        when(medicamentoRepository.findByNombre("Inexistente"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> inventarioService.findByNombreExacto("Inexistente"));

        assertEquals("Medicamento no encontrado con nombre: Inexistente", ex.getMessage());
    }

    @Test
    @DisplayName("findById: deberia lanzar excepcion cuando el id no existe")
    void findById_cuandoNoExiste_lanzaRuntimeException() {
        when(medicamentoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> inventarioService.findById(99L));

        assertEquals("Medicamento no encontrado con id: 99", ex.getMessage());
    }

    // findStockBajo() y search()
    @Test
    @DisplayName("findStockBajo: deberia retornar medicamentos con stock menor al limite")
    void findStockBajo_retornaMedicamentosConStockBajo() {
        when(medicamentoRepository.findByStockLessThan(10))
                .thenReturn(List.of(medicamentoEjemplo));

        List<MedicamentoResponseDto> resultado = inventarioService.findStockBajo(10);

        assertEquals(1, resultado.size());
        verify(medicamentoRepository, times(1)).findByStockLessThan(10);
    }

    @Test
    @DisplayName("search: deberia retornar medicamentos que coincidan con el nombre buscado")
    void search_retornaMedicamentosQueCoincidenConNombre() {
        when(medicamentoRepository.findByNombreContainingIgnoreCase("Amox"))
                .thenReturn(List.of(medicamentoEjemplo));

        List<MedicamentoResponseDto> resultado = inventarioService.search("Amox");

        assertEquals(1, resultado.size());
        verify(medicamentoRepository, times(1)).findByNombreContainingIgnoreCase("Amox");
    }

    // save() - regla de negocio: nombre duplicado
    @Test
    @DisplayName("save: deberia guardar el medicamento cuando el nombre no existe")
    void save_cuandoNombreNoExiste_guardaMedicamentoCorrectamente() {
        when(medicamentoRepository.existsByNombre("Amoxicilina")).thenReturn(false);
        when(medicamentoRepository.save(any(Medicamento.class)))
                .thenReturn(medicamentoEjemplo);

        MedicamentoResponseDto resultado = inventarioService.save(requestDtoEjemplo);

        assertNotNull(resultado);
        assertEquals("Amoxicilina", resultado.getNombre());
        verify(medicamentoRepository, times(1)).save(any(Medicamento.class));
    }

    @Test
    @DisplayName("save: deberia lanzar excepcion cuando el nombre ya existe")
    void save_cuandoNombreYaExiste_lanzaRuntimeException() {
        when(medicamentoRepository.existsByNombre("Amoxicilina")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> inventarioService.save(requestDtoEjemplo));

        assertEquals("Ya existe un medicamento con el nombre: Amoxicilina", ex.getMessage());
        verify(medicamentoRepository, never()).save(any(Medicamento.class));
    }

    // update() - actualizacion de datos
    @Test
    @DisplayName("update: deberia actualizar los datos del medicamento existente")
    void update_cuandoExiste_actualizaDatosCorrectamente() {
        MedicamentoRequestDto nuevoDato = new MedicamentoRequestDto();
        nuevoDato.setNombre("Amoxicilina Editada");
        nuevoDato.setStock(50);
        nuevoDato.setUnidad("frasco");
        nuevoDato.setPrecioUnitario(new BigDecimal("15.00"));

        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamentoEjemplo));
        when(medicamentoRepository.save(any(Medicamento.class))).thenReturn(medicamentoEjemplo);

        MedicamentoResponseDto resultado = inventarioService.update(1L, nuevoDato);

        assertEquals("Amoxicilina Editada", resultado.getNombre());
        verify(medicamentoRepository, times(1)).save(medicamentoEjemplo);
    }

    @Test
    @DisplayName("update: deberia lanzar excepcion si el medicamento no existe")
    void update_cuandoNoExiste_lanzaRuntimeException() {
        when(medicamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> inventarioService.update(99L, requestDtoEjemplo));
        verify(medicamentoRepository, never()).save(any(Medicamento.class));
    }
}