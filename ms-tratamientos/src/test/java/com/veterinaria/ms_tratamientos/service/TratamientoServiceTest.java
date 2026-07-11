package com.veterinaria.ms_tratamientos.service;

import com.veterinaria.ms_tratamientos.dto.MedicamentoDto;
import com.veterinaria.ms_tratamientos.dto.MovimientoRequestDto;
import com.veterinaria.ms_tratamientos.dto.TratamientoRequestDto;
import com.veterinaria.ms_tratamientos.dto.TratamientoResponseDto;
import com.veterinaria.ms_tratamientos.feign.InventarioClient;
import com.veterinaria.ms_tratamientos.model.Tratamiento;
import com.veterinaria.ms_tratamientos.repository.TratamientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TratamientoService - Pruebas Unitarias (con Feign mockeado)")
class TratamientoServiceTest {

    @Mock
    private TratamientoRepository tratamientoRepository;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private TratamientoService tratamientoService;

    private Tratamiento tratamientoEjemplo;
    private TratamientoRequestDto requestDtoEjemplo;
    private MedicamentoDto medicamentoConStockEjemplo;

    @BeforeEach
    void setUp() {
        tratamientoEjemplo = Tratamiento.builder()
                .id(1L)
                .idConsulta(1L)
                .idMascota(1L)
                .medicamento("Amoxicilina")
                .dosis("250mg")
                .frecuencia("Cada 12 horas")
                .duracionDias(7)
                .indicaciones("Administrar con alimento")
                .createdAt(LocalDateTime.now())
                .build();

        requestDtoEjemplo = new TratamientoRequestDto();
        requestDtoEjemplo.setIdConsulta(1L);
        requestDtoEjemplo.setIdMascota(1L);
        requestDtoEjemplo.setMedicamento("Amoxicilina");
        requestDtoEjemplo.setDosis("250mg");
        requestDtoEjemplo.setFrecuencia("Cada 12 horas");
        requestDtoEjemplo.setDuracionDias(7);
        requestDtoEjemplo.setIndicaciones("Administrar con alimento");

        // Medicamento activo con stock suficiente (happy path)
        medicamentoConStockEjemplo = new MedicamentoDto();
        medicamentoConStockEjemplo.setId(1L);
        medicamentoConStockEjemplo.setNombre("Amoxicilina");
        medicamentoConStockEjemplo.setStock(100);
        medicamentoConStockEjemplo.setActivo(true);
    }

    // findAll() - lista completa de tratamientos
    @Test
    @DisplayName("findAll: deberia retornar todos los tratamientos")
    void findAll_retornaListaDeTratamientos() {
        when(tratamientoRepository.findAll()).thenReturn(List.of(tratamientoEjemplo));

        List<TratamientoResponseDto> resultado = tratamientoService.findAll();

        assertEquals(1, resultado.size());
        verify(tratamientoRepository, times(1)).findAll();
    }

    // findById() - caso exitoso y de error
    @Test
    @DisplayName("findById: deberia retornar el tratamiento cuando el id existe")
    void findById_cuandoExiste_retornaTratamientoResponseDto() {
        when(tratamientoRepository.findById(1L)).thenReturn(Optional.of(tratamientoEjemplo));

        TratamientoResponseDto resultado = tratamientoService.findById(1L);

        assertNotNull(resultado);
        assertEquals("Amoxicilina", resultado.getMedicamento());
        verify(tratamientoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById: deberia lanzar excepcion cuando el id no existe")
    void findById_cuandoNoExiste_lanzaRuntimeException() {
        when(tratamientoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tratamientoService.findById(99L));

        assertEquals("Tratamiento no encontrado con id: 99", ex.getMessage());
    }

    // findByConsulta() y findByMascota()
    @Test
    @DisplayName("findByConsulta: deberia retornar tratamientos de la consulta")
    void findByConsulta_retornaTratamientosDeLaConsulta() {
        when(tratamientoRepository.findByIdConsulta(1L))
                .thenReturn(List.of(tratamientoEjemplo));

        List<TratamientoResponseDto> resultado = tratamientoService.findByConsulta(1L);

        assertEquals(1, resultado.size());
        verify(tratamientoRepository, times(1)).findByIdConsulta(1L);
    }

    @Test
    @DisplayName("findByMascota: deberia retornar tratamientos de la mascota")
    void findByMascota_retornaTratamientosDeLaMascota() {
        when(tratamientoRepository.findByIdMascotaOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(tratamientoEjemplo));

        List<TratamientoResponseDto> resultado = tratamientoService.findByMascota(1L);

        assertEquals(1, resultado.size());
        verify(tratamientoRepository, times(1)).findByIdMascotaOrderByCreatedAtDesc(1L);
    }

    // save() - registro de nuevo tratamiento, validando y descontando stock via Feign
    @Test
    @DisplayName("save: deberia validar stock, descontarlo via Feign y guardar el tratamiento")
    void save_guardaTratamientoCorrectamente() {
        // Given
        when(inventarioClient.getMedicamentoPorNombre("Amoxicilina"))
                .thenReturn(medicamentoConStockEjemplo);
        when(tratamientoRepository.save(any(Tratamiento.class)))
                .thenReturn(tratamientoEjemplo);

        // When
        TratamientoResponseDto resultado = tratamientoService.save(requestDtoEjemplo);

        // Then
        assertNotNull(resultado);
        assertEquals("Amoxicilina", resultado.getMedicamento());
        verify(inventarioClient, times(1)).getMedicamentoPorNombre("Amoxicilina");
        verify(inventarioClient, times(1)).registrarMovimiento(any(MovimientoRequestDto.class));
        verify(tratamientoRepository, times(1)).save(any(Tratamiento.class));
    }

    @Test
    @DisplayName("save: deberia lanzar excepcion cuando el medicamento no existe o no esta activo")
    void save_cuandoMedicamentoNoActivo_lanzaRuntimeException() {
        // Given
        MedicamentoDto medicamentoInactivo = new MedicamentoDto();
        medicamentoInactivo.setId(1L);
        medicamentoInactivo.setNombre("Amoxicilina");
        medicamentoInactivo.setStock(100);
        medicamentoInactivo.setActivo(false);
        when(inventarioClient.getMedicamentoPorNombre("Amoxicilina"))
                .thenReturn(medicamentoInactivo);

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tratamientoService.save(requestDtoEjemplo));

        assertEquals("El medicamento no existe o no está activo: Amoxicilina", ex.getMessage());
        verify(inventarioClient, never()).registrarMovimiento(any());
        verify(tratamientoRepository, never()).save(any(Tratamiento.class));
    }

    @Test
    @DisplayName("save: deberia lanzar excepcion cuando el stock es insuficiente")
    void save_cuandoStockInsuficiente_lanzaRuntimeException() {
        // Given: stock menor a la duracionDias solicitada (7)
        MedicamentoDto medicamentoSinStock = new MedicamentoDto();
        medicamentoSinStock.setId(1L);
        medicamentoSinStock.setNombre("Amoxicilina");
        medicamentoSinStock.setStock(3);
        medicamentoSinStock.setActivo(true);
        when(inventarioClient.getMedicamentoPorNombre("Amoxicilina"))
                .thenReturn(medicamentoSinStock);

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tratamientoService.save(requestDtoEjemplo));

        assertTrue(ex.getMessage().contains("Stock insuficiente"));
        verify(inventarioClient, never()).registrarMovimiento(any());
        verify(tratamientoRepository, never()).save(any(Tratamiento.class));
    }

    // update() - actualizacion de datos
    @Test
    @DisplayName("update: deberia actualizar los datos del tratamiento existente")
    void update_cuandoExiste_actualizaDatosCorrectamente() {
        TratamientoRequestDto nuevoDato = new TratamientoRequestDto();
        nuevoDato.setIdConsulta(1L);
        nuevoDato.setIdMascota(1L);
        nuevoDato.setMedicamento("Amoxicilina Editada");
        nuevoDato.setDosis("500mg");
        nuevoDato.setFrecuencia("Cada 8 horas");
        nuevoDato.setDuracionDias(10);

        when(tratamientoRepository.findById(1L)).thenReturn(Optional.of(tratamientoEjemplo));
        when(tratamientoRepository.save(any(Tratamiento.class))).thenReturn(tratamientoEjemplo);

        TratamientoResponseDto resultado = tratamientoService.update(1L, nuevoDato);

        assertEquals("Amoxicilina Editada", resultado.getMedicamento());
        verify(tratamientoRepository, times(1)).save(tratamientoEjemplo);
    }

    @Test
    @DisplayName("update: deberia lanzar excepcion si el tratamiento no existe")
    void update_cuandoNoExiste_lanzaRuntimeException() {
        when(tratamientoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> tratamientoService.update(99L, requestDtoEjemplo));
        verify(tratamientoRepository, never()).save(any(Tratamiento.class));
    }
}