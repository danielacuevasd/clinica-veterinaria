package com.veterinaria.ms_tratamientos.service;

import com.veterinaria.ms_tratamientos.dto.TratamientoRequestDto;
import com.veterinaria.ms_tratamientos.dto.TratamientoResponseDto;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TratamientoService - Pruebas Unitarias")
class TratamientoServiceTest {

    @Mock
    private TratamientoRepository tratamientoRepository;

    @InjectMocks
    private TratamientoService tratamientoService;

    private Tratamiento tratamientoEjemplo;
    private TratamientoRequestDto requestDtoEjemplo;

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

    // save() - registro de nuevo tratamiento
    @Test
    @DisplayName("save: deberia guardar el tratamiento correctamente")
    void save_guardaTratamientoCorrectamente() {
        when(tratamientoRepository.save(any(Tratamiento.class)))
                .thenReturn(tratamientoEjemplo);

        TratamientoResponseDto resultado = tratamientoService.save(requestDtoEjemplo);

        assertNotNull(resultado);
        assertEquals("Amoxicilina", resultado.getMedicamento());
        verify(tratamientoRepository, times(1)).save(any(Tratamiento.class));
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