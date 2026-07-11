package com.veterinaria.ms_citas.service;

import com.veterinaria.ms_citas.dto.CitaRequestDto;
import com.veterinaria.ms_citas.dto.CitaResponseDto;
import com.veterinaria.ms_citas.dto.DuenoDto;
import com.veterinaria.ms_citas.dto.MascotaDto;
import com.veterinaria.ms_citas.feign.MascotaClient;
import com.veterinaria.ms_citas.feign.UsuarioClient;
import com.veterinaria.ms_citas.feign.VeterinarioClient;
import com.veterinaria.ms_citas.model.Cita;
import com.veterinaria.ms_citas.model.EstadoCita;
import com.veterinaria.ms_citas.repository.CitaRepository;
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
@DisplayName("CitaService - Pruebas Unitarias (con Feign mockeado)")
class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private VeterinarioClient veterinarioClient;

    @Mock
    private MascotaClient mascotaClient;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private CitaService citaService;

    private CitaRequestDto requestDtoEjemplo;
    private Cita citaEjemplo;
    private MascotaDto mascotaDtoEjemplo;
    private DuenoDto duenoActivoEjemplo;

    @BeforeEach
    void setUp() {
        requestDtoEjemplo = new CitaRequestDto();
        requestDtoEjemplo.setIdMascota(1L);
        requestDtoEjemplo.setIdVeterinario(1L);
        requestDtoEjemplo.setIdDueno(1L);
        requestDtoEjemplo.setFechaHora(LocalDateTime.now().plusDays(1));
        requestDtoEjemplo.setMotivo("Control rutinario");

        citaEjemplo = Cita.builder()
                .id(1L)
                .idMascota(1L)
                .idVeterinario(1L)
                .idDueno(1L)
                .fechaHora(LocalDateTime.now().plusDays(1))
                .motivo("Control rutinario")
                .estado(EstadoCita.PENDIENTE)
                .createdAt(LocalDateTime.now())
                .build();

        // Esta mascota SI pertenece al dueno 1L (happy path)
        mascotaDtoEjemplo = new MascotaDto();
        mascotaDtoEjemplo.setId(1L);
        mascotaDtoEjemplo.setNombre("Luna");
        mascotaDtoEjemplo.setIdDueno(1L);
        mascotaDtoEjemplo.setActivo(true);

        // Dueno activo (happy path)
        duenoActivoEjemplo = new DuenoDto();
        duenoActivoEjemplo.setId(1L);
        duenoActivoEjemplo.setActivo(true);
    }


    // save() - happy path: veterinario disponible + mascota valida + dueno activo
    @Test
    @DisplayName("save: deberia crear la cita cuando vet esta disponible, mascota es del dueno y dueno esta activo")
    void save_cuandoTodoValido_creaCitaCorrectamente() {
        // Given: Feign simula que el veterinario SI esta disponible
        when(veterinarioClient.isDisponible(1L)).thenReturn(true);
        // Given: Feign simula que la mascota existe y pertenece al dueno correcto
        when(mascotaClient.getMascota(1L)).thenReturn(mascotaDtoEjemplo);
        // Given: Feign simula que el dueno existe y esta activo
        when(usuarioClient.getDueno(1L)).thenReturn(duenoActivoEjemplo);
        when(citaRepository.save(any(Cita.class))).thenReturn(citaEjemplo);

        // When
        CitaResponseDto resultado = citaService.save(requestDtoEjemplo);

        // Then
        assertNotNull(resultado);
        assertEquals(EstadoCita.PENDIENTE, resultado.getEstado());
        // Verifica que SI se llamo a los tres servicios externos via Feign
        verify(veterinarioClient, times(1)).isDisponible(1L);
        verify(mascotaClient, times(1)).getMascota(1L);
        verify(usuarioClient, times(1)).getDueno(1L);
        verify(citaRepository, times(1)).save(any(Cita.class));
    }

    // save() - reglas de negocio que dependen de la respuesta de Feign
    @Test
    @DisplayName("save: deberia lanzar excepcion cuando el veterinario no esta disponible")
    void save_cuandoVeterinarioNoDisponible_lanzaRuntimeException() {
        // Given: Feign simula que el veterinario NO esta disponible
        when(veterinarioClient.isDisponible(1L)).thenReturn(false);

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> citaService.save(requestDtoEjemplo));

        assertEquals("El veterinario no está disponible para citas", ex.getMessage());
        // Si el veterinario no esta disponible, NUNCA debe consultarse la mascota ni guardar
        verify(mascotaClient, never()).getMascota(any());
        verify(usuarioClient, never()).getDueno(any());
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("save: deberia lanzar excepcion cuando la mascota no pertenece al dueno indicado")
    void save_cuandoMascotaNoPerteneceADueno_lanzaRuntimeException() {
        // Given: el veterinario SI esta disponible
        when(veterinarioClient.isDisponible(1L)).thenReturn(true);

        // Given: la mascota existe, pero pertenece a OTRO dueno (id=2L, no 1L)
        MascotaDto mascotaDeOtroDueno = new MascotaDto();
        mascotaDeOtroDueno.setId(1L);
        mascotaDeOtroDueno.setNombre("Luna");
        mascotaDeOtroDueno.setIdDueno(2L);
        mascotaDeOtroDueno.setActivo(true);

        when(mascotaClient.getMascota(1L)).thenReturn(mascotaDeOtroDueno);

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> citaService.save(requestDtoEjemplo));

        assertEquals("La mascota no pertenece al dueño indicado", ex.getMessage());
        // La cita NUNCA debe guardarse si la mascota es de otro dueno
        verify(usuarioClient, never()).getDueno(any());
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("save: deberia lanzar excepcion cuando el dueno no existe o no esta activo")
    void save_cuandoDuenoNoActivo_lanzaRuntimeException() {
        // Given: veterinario disponible y mascota valida
        when(veterinarioClient.isDisponible(1L)).thenReturn(true);
        when(mascotaClient.getMascota(1L)).thenReturn(mascotaDtoEjemplo);

        // Given: el dueno existe pero esta inactivo
        DuenoDto duenoInactivo = new DuenoDto();
        duenoInactivo.setId(1L);
        duenoInactivo.setActivo(false);
        when(usuarioClient.getDueno(1L)).thenReturn(duenoInactivo);

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> citaService.save(requestDtoEjemplo));

        assertEquals("El dueño no existe o no está activo", ex.getMessage());
        verify(citaRepository, never()).save(any(Cita.class));
    }

    // findById() - caso exitoso y de error
    @Test
    @DisplayName("findById: deberia retornar la cita cuando el id existe")
    void findById_cuandoExiste_retornaCitaResponseDto() {
        // Given
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEjemplo));

        // When
        CitaResponseDto resultado = citaService.findById(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(EstadoCita.PENDIENTE, resultado.getEstado());
        verify(citaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById: deberia lanzar excepcion cuando el id no existe")
    void findById_cuandoNoExiste_lanzaRuntimeException() {
        // Given
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> citaService.findById(99L));

        assertEquals("Cita no encontrada con id: 99", ex.getMessage());
    }


    // cambiarEstado() - actualiza el estado de una cita existente
    @Test
    @DisplayName("cambiarEstado: deberia actualizar el estado de PENDIENTE a CONFIRMADA")
    void cambiarEstado_actualizaEstadoCorrectamente() {
        // Given
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEjemplo));
        when(citaRepository.save(any(Cita.class))).thenReturn(citaEjemplo);

        // When
        CitaResponseDto resultado = citaService.cambiarEstado(1L, EstadoCita.CONFIRMADA);

        // Then
        assertEquals(EstadoCita.CONFIRMADA, citaEjemplo.getEstado());
        verify(citaRepository, times(1)).save(citaEjemplo);
    }

    // cancelar() - cambia el estado a CANCELADA
    @Test
    @DisplayName("cancelar: deberia cambiar el estado de la cita a CANCELADA")
    void cancelar_cambiaEstadoACancelada() {
        // Given
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEjemplo));
        when(citaRepository.save(any(Cita.class))).thenReturn(citaEjemplo);

        // When
        citaService.cancelar(1L);

        // Then
        assertEquals(EstadoCita.CANCELADA, citaEjemplo.getEstado());
        verify(citaRepository, times(1)).save(citaEjemplo);
    }

    @Test
    @DisplayName("cancelar: deberia lanzar excepcion si la cita no existe")
    void cancelar_cuandoNoExiste_lanzaRuntimeException() {
        // Given
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        // When + Then
        assertThrows(RuntimeException.class, () -> citaService.cancelar(99L));
        verify(citaRepository, never()).save(any(Cita.class));
    }

    // findAll() - lista completa de citas
    @Test
    @DisplayName("findAll: deberia retornar todas las citas")
    void findAll_retornaListaDeCitas() {
        // Given
        when(citaRepository.findAll()).thenReturn(List.of(citaEjemplo));

        // When
        List<CitaResponseDto> resultado = citaService.findAll();

        // Then
        assertEquals(1, resultado.size());
        verify(citaRepository, times(1)).findAll();
    }

    // findByDueno() - citas de un dueno especifico
    @Test
    @DisplayName("findByDueno: deberia retornar las citas del dueno")
    void findByDueno_retornaCitasDelDueno() {
        // Given
        when(citaRepository.findByIdDueno(1L)).thenReturn(List.of(citaEjemplo));

        // When
        List<CitaResponseDto> resultado = citaService.findByDueno(1L);

        // Then
        assertEquals(1, resultado.size());
        verify(citaRepository, times(1)).findByIdDueno(1L);
    }

    // findByVeterinario() - citas de un veterinario especifico
    @Test
    @DisplayName("findByVeterinario: deberia retornar las citas del veterinario")
    void findByVeterinario_retornaCitasDelVeterinario() {
        // Given
        when(citaRepository.findByIdVeterinario(1L)).thenReturn(List.of(citaEjemplo));

        // When
        List<CitaResponseDto> resultado = citaService.findByVeterinario(1L);

        // Then
        assertEquals(1, resultado.size());
        verify(citaRepository, times(1)).findByIdVeterinario(1L);
    }

    // findByMascota() - citas de una mascota especifica
    @Test
    @DisplayName("findByMascota: deberia retornar las citas de la mascota")
    void findByMascota_retornaCitasDeLaMascota() {
        // Given
        when(citaRepository.findByIdMascota(1L)).thenReturn(List.of(citaEjemplo));

        // When
        List<CitaResponseDto> resultado = citaService.findByMascota(1L);

        // Then
        assertEquals(1, resultado.size());
        verify(citaRepository, times(1)).findByIdMascota(1L);
    }
}