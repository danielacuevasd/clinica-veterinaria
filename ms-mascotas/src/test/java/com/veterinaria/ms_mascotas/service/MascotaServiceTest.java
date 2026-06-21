package com.veterinaria.ms_mascotas.service;

import com.veterinaria.ms_mascotas.dto.HistorialMedicoRequestDto;
import com.veterinaria.ms_mascotas.dto.HistorialMedicoResponseDto;
import com.veterinaria.ms_mascotas.dto.MascotaRequestDto;
import com.veterinaria.ms_mascotas.dto.MascotaResponseDto;
import com.veterinaria.ms_mascotas.model.HistorialMedico;
import com.veterinaria.ms_mascotas.model.Mascota;
import com.veterinaria.ms_mascotas.repository.HistorialMedicoRepository;
import com.veterinaria.ms_mascotas.repository.MascotaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MascotaService - Pruebas Unitarias")
class MascotaServiceTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private HistorialMedicoRepository historialMedicoRepository;

    @InjectMocks
    private MascotaService mascotaService;

    private Mascota mascotaEjemplo;
    private MascotaRequestDto requestDtoEjemplo;

    @BeforeEach
    void setUp() {
        mascotaEjemplo = Mascota.builder()
                .id(1L)
                .nombre("Luna")
                .especie("Gato")
                .raza("Siames")
                .fechaNacimiento(LocalDate.of(2023, 1, 10))
                .idDueno(1L)
                .activo(true)
                .createdAt(LocalDateTime.now())
                .build();

        requestDtoEjemplo = new MascotaRequestDto();
        requestDtoEjemplo.setNombre("Luna");
        requestDtoEjemplo.setEspecie("Gato");
        requestDtoEjemplo.setRaza("Siames");
        requestDtoEjemplo.setFechaNacimiento(LocalDate.of(2023, 1, 10));
        requestDtoEjemplo.setIdDueno(1L);
    }

    // findById() - casos exitoso y de error
    @Test
    @DisplayName("findById: deberia retornar la mascota cuando el id existe")
    void findById_cuandoExiste_retornaMascotaResponseDto() {
        // Given
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascotaEjemplo));

        // When
        MascotaResponseDto resultado = mascotaService.findById(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("Luna", resultado.getNombre());
        assertEquals("Gato", resultado.getEspecie());
        verify(mascotaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById: deberia lanzar excepcion cuando el id no existe")
    void findById_cuandoNoExiste_lanzaRuntimeException() {
        // Given
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> mascotaService.findById(99L));

        assertEquals("Mascota no encontrada con id: 99", ex.getMessage());
    }

    // save() - registro de nueva mascota
    @Test
    @DisplayName("save: deberia guardar la mascota correctamente")
    void save_guardaMascotaCorrectamente() {
        // Given
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascotaEjemplo);

        // When
        MascotaResponseDto resultado = mascotaService.save(requestDtoEjemplo);

        // Then
        assertNotNull(resultado);
        assertEquals("Luna", resultado.getNombre());
        assertEquals(1L, resultado.getIdDueno());
        verify(mascotaRepository, times(1)).save(any(Mascota.class));
    }

    // findByDueno() - mascotas de un dueno especifico
    @Test
    @DisplayName("findByDueno: deberia retornar las mascotas activas del dueno")
    void findByDueno_retornaMascotasDelDueno() {
        // Given
        when(mascotaRepository.findByIdDuenoAndActivoTrue(1L))
                .thenReturn(List.of(mascotaEjemplo));

        // When
        List<MascotaResponseDto> resultado = mascotaService.findByDueno(1L);

        // Then
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdDueno());
        verify(mascotaRepository, times(1)).findByIdDuenoAndActivoTrue(1L);
    }

    // delete() - eliminacion logica
    @Test
    @DisplayName("delete: deberia marcar activo=false en lugar de borrar fisicamente")
    void delete_marcaMascotaComoInactiva() {
        // Given
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascotaEjemplo));
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascotaEjemplo);

        // When
        mascotaService.delete(1L);

        // Then
        assertFalse(mascotaEjemplo.getActivo());
        verify(mascotaRepository, times(1)).save(mascotaEjemplo);
        verify(mascotaRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete: deberia lanzar excepcion si la mascota no existe")
    void delete_cuandoNoExiste_lanzaRuntimeException() {
        // Given
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        // When + Then
        assertThrows(RuntimeException.class, () -> mascotaService.delete(99L));
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    // addHistorial() - usa DOS repositorios: valida en uno, guarda en otro
    @Test
    @DisplayName("addHistorial: deberia guardar historial cuando la mascota existe")
    void addHistorial_cuandoMascotaExiste_guardaHistorialCorrectamente() {
        // Given
        HistorialMedicoRequestDto historialDto = new HistorialMedicoRequestDto();
        historialDto.setIdMascota(1L);
        historialDto.setDescripcion("Vacuna antirrabica aplicada");
        historialDto.setFecha(LocalDate.now());

        HistorialMedico historialGuardado = HistorialMedico.builder()
                .id(1L)
                .idMascota(1L)
                .descripcion("Vacuna antirrabica aplicada")
                .fecha(LocalDate.now())
                .build();

        // Primero valida que la mascota exista (MascotaRepository)
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascotaEjemplo));
        // Luego guarda en el OTRO repositorio (HistorialMedicoRepository)
        when(historialMedicoRepository.save(any(HistorialMedico.class)))
                .thenReturn(historialGuardado);

        // When
        HistorialMedicoResponseDto resultado = mascotaService.addHistorial(historialDto);

        // Then
        assertNotNull(resultado);
        assertEquals("Vacuna antirrabica aplicada", resultado.getDescripcion());
        // Verifica que se consultaron AMBOS repositorios, cada uno con su responsabilidad
        verify(mascotaRepository, times(1)).findById(1L);
        verify(historialMedicoRepository, times(1)).save(any(HistorialMedico.class));
    }

    @Test
    @DisplayName("addHistorial: deberia lanzar excepcion si la mascota no existe")
    void addHistorial_cuandoMascotaNoExiste_lanzaRuntimeException() {
        // Given
        HistorialMedicoRequestDto historialDto = new HistorialMedicoRequestDto();
        historialDto.setIdMascota(99L);
        historialDto.setDescripcion("Control rutinario");
        historialDto.setFecha(LocalDate.now());

        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> mascotaService.addHistorial(historialDto));

        assertEquals("Mascota no encontrada con id: 99", ex.getMessage());
        // Si la mascota no existe, NUNCA debe guardarse el historial
        verify(historialMedicoRepository, never()).save(any(HistorialMedico.class));
    }

    // findAll() - lista de mascotas activas
    @Test
    @DisplayName("findAll: deberia retornar todas las mascotas activas")
    void findAll_retornaListaDeMascotasActivas() {
        // Given
        when(mascotaRepository.findByActivoTrue()).thenReturn(List.of(mascotaEjemplo));

        // When
        List<MascotaResponseDto> resultado = mascotaService.findAll();

        // Then
        assertEquals(1, resultado.size());
        verify(mascotaRepository, times(1)).findByActivoTrue();
    }

    // findByEspecie() - busqueda por especie
    @Test
    @DisplayName("findByEspecie: deberia retornar mascotas de la especie indicada")
    void findByEspecie_retornaMascotasDeLaEspecie() {
        // Given
        when(mascotaRepository.findByEspecieIgnoreCase("Gato"))
                .thenReturn(List.of(mascotaEjemplo));

        // When
        List<MascotaResponseDto> resultado = mascotaService.findByEspecie("Gato");

        // Then
        assertEquals(1, resultado.size());
        assertEquals("Gato", resultado.get(0).getEspecie());
        verify(mascotaRepository, times(1)).findByEspecieIgnoreCase("Gato");
    }

    // search() - busqueda por nombre parcial
    @Test
    @DisplayName("search: deberia retornar mascotas que coincidan con el nombre buscado")
    void search_retornaMascotasQueCoincidenConNombre() {
        // Given
        when(mascotaRepository.findByNombreContainingIgnoreCase("Lun"))
                .thenReturn(List.of(mascotaEjemplo));

        // When
        List<MascotaResponseDto> resultado = mascotaService.search("Lun");

        // Then
        assertEquals(1, resultado.size());
        assertEquals("Luna", resultado.get(0).getNombre());
        verify(mascotaRepository, times(1)).findByNombreContainingIgnoreCase("Lun");
    }

    // update() - actualizacion de datos de la mascota
    @Test
    @DisplayName("update: deberia actualizar los datos de la mascota existente")
    void update_cuandoExiste_actualizaDatosCorrectamente() {
        // Given
        MascotaRequestDto nuevoDato = new MascotaRequestDto();
        nuevoDato.setNombre("Luna Editada");
        nuevoDato.setEspecie("Gato");
        nuevoDato.setRaza("Siames");
        nuevoDato.setFechaNacimiento(LocalDate.of(2023, 1, 10));
        nuevoDato.setIdDueno(1L);

        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascotaEjemplo));
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascotaEjemplo);

        // When
        MascotaResponseDto resultado = mascotaService.update(1L, nuevoDato);

        // Then
        assertEquals("Luna Editada", resultado.getNombre());
        verify(mascotaRepository, times(1)).save(mascotaEjemplo);
    }

    @Test
    @DisplayName("update: deberia lanzar excepcion si la mascota no existe")
    void update_cuandoNoExiste_lanzaRuntimeException() {
        // Given
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        // When + Then
        assertThrows(RuntimeException.class,
                () -> mascotaService.update(99L, requestDtoEjemplo));
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    // getHistorial() - historial medico de una mascota
    @Test
    @DisplayName("getHistorial: deberia retornar el historial medico de la mascota")
    void getHistorial_retornaHistorialDeLaMascota() {
        // Given
        HistorialMedico historialEjemplo = HistorialMedico.builder()
                .id(1L)
                .idMascota(1L)
                .descripcion("Vacuna antirrabica aplicada")
                .fecha(LocalDate.now())
                .build();

        when(historialMedicoRepository.findByIdMascotaOrderByFechaDesc(1L))
                .thenReturn(List.of(historialEjemplo));

        // When
        List<HistorialMedicoResponseDto> resultado = mascotaService.getHistorial(1L);

        // Then
        assertEquals(1, resultado.size());
        assertEquals("Vacuna antirrabica aplicada", resultado.get(0).getDescripcion());
        verify(historialMedicoRepository, times(1)).findByIdMascotaOrderByFechaDesc(1L);
    }
}