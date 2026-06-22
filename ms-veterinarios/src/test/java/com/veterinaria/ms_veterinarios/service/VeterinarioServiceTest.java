package com.veterinaria.ms_veterinarios.service;

import com.veterinaria.ms_veterinarios.dto.HorarioRequestDto;
import com.veterinaria.ms_veterinarios.dto.HorarioResponseDto;
import com.veterinaria.ms_veterinarios.dto.VeterinarioRequestDto;
import com.veterinaria.ms_veterinarios.dto.VeterinarioResponseDto;
import com.veterinaria.ms_veterinarios.model.Horario;
import com.veterinaria.ms_veterinarios.model.Veterinario;
import com.veterinaria.ms_veterinarios.repository.HorarioRepository;
import com.veterinaria.ms_veterinarios.repository.VeterinarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VeterinarioService - Pruebas Unitarias")
class VeterinarioServiceTest {

    @Mock
    private VeterinarioRepository veterinarioRepository;

    @Mock
    private HorarioRepository horarioRepository;

    @InjectMocks
    private VeterinarioService veterinarioService;

    private Veterinario veterinarioEjemplo;
    private VeterinarioRequestDto requestDtoEjemplo;

    @BeforeEach
    void setUp() {
        veterinarioEjemplo = Veterinario.builder()
                .id(1L)
                .nombre("Carlos")
                .apellido("Perez")
                .especialidad("Cirugia")
                .email("carlos@clinica.com")
                .telefono("987654321")
                .disponible(true)
                .activo(true)
                .createdAt(LocalDateTime.now())
                .build();

        requestDtoEjemplo = new VeterinarioRequestDto();
        requestDtoEjemplo.setNombre("Carlos");
        requestDtoEjemplo.setApellido("Perez");
        requestDtoEjemplo.setEspecialidad("Cirugia");
        requestDtoEjemplo.setEmail("carlos@clinica.com");
        requestDtoEjemplo.setTelefono("987654321");
    }

    // isDisponible() - el metodo que ms-citas consulta via Feign
    @Test
    @DisplayName("isDisponible: deberia retornar true cuando el veterinario esta disponible")
    void isDisponible_cuandoEstaDisponible_retornaTrue() {
        // Given
        when(veterinarioRepository.findById(1L))
                .thenReturn(Optional.of(veterinarioEjemplo));

        // When
        Boolean resultado = veterinarioService.isDisponible(1L);

        // Then
        assertTrue(resultado);
        verify(veterinarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("isDisponible: deberia retornar false cuando el veterinario no esta disponible")
    void isDisponible_cuandoNoEstaDisponible_retornaFalse() {
        // Given: el veterinario existe pero esta marcado como no disponible
        veterinarioEjemplo.setDisponible(false);
        when(veterinarioRepository.findById(1L))
                .thenReturn(Optional.of(veterinarioEjemplo));

        // When
        Boolean resultado = veterinarioService.isDisponible(1L);

        // Then
        assertFalse(resultado);
    }

    @Test
    @DisplayName("isDisponible: deberia lanzar excepcion cuando el veterinario no existe")
    void isDisponible_cuandoNoExiste_lanzaRuntimeException() {
        // Given
        when(veterinarioRepository.findById(99L)).thenReturn(Optional.empty());

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> veterinarioService.isDisponible(99L));

        assertEquals("Veterinario no encontrado con id: 99", ex.getMessage());
    }

    // cambiarDisponibilidad() - cambia el estado disponible
    @Test
    @DisplayName("cambiarDisponibilidad: deberia actualizar el campo disponible correctamente")
    void cambiarDisponibilidad_actualizaCampoCorrectamente() {
        // Given
        when(veterinarioRepository.findById(1L))
                .thenReturn(Optional.of(veterinarioEjemplo));
        when(veterinarioRepository.save(any(Veterinario.class)))
                .thenReturn(veterinarioEjemplo);

        // When
        VeterinarioResponseDto resultado =
                veterinarioService.cambiarDisponibilidad(1L, false);

        // Then
        assertFalse(veterinarioEjemplo.getDisponible());
        verify(veterinarioRepository, times(1)).save(veterinarioEjemplo);
    }

    // save() - regla de negocio: email duplicado
    @Test
    @DisplayName("save: deberia guardar el veterinario cuando el email no existe")
    void save_cuandoEmailNoExiste_guardaVeterinarioCorrectamente() {
        // Given
        when(veterinarioRepository.existsByEmail(requestDtoEjemplo.getEmail()))
                .thenReturn(false);
        when(veterinarioRepository.save(any(Veterinario.class)))
                .thenReturn(veterinarioEjemplo);

        // When
        VeterinarioResponseDto resultado = veterinarioService.save(requestDtoEjemplo);

        // Then
        assertNotNull(resultado);
        assertEquals("Carlos", resultado.getNombre());
        verify(veterinarioRepository, times(1)).save(any(Veterinario.class));
    }

    @Test
    @DisplayName("save: deberia lanzar excepcion cuando el email ya existe")
    void save_cuandoEmailYaExiste_lanzaRuntimeException() {
        // Given
        when(veterinarioRepository.existsByEmail(requestDtoEjemplo.getEmail()))
                .thenReturn(true);

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> veterinarioService.save(requestDtoEjemplo));

        assertEquals("Ya existe un veterinario con el email: carlos@clinica.com",
                ex.getMessage());
        verify(veterinarioRepository, never()).save(any(Veterinario.class));
    }

    // findById() - caso exitoso y de error
    @Test
    @DisplayName("findById: deberia retornar el veterinario cuando el id existe")
    void findById_cuandoExiste_retornaVeterinarioResponseDto() {
        // Given
        when(veterinarioRepository.findById(1L))
                .thenReturn(Optional.of(veterinarioEjemplo));

        // When
        VeterinarioResponseDto resultado = veterinarioService.findById(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("Carlos", resultado.getNombre());
        assertEquals("Cirugia", resultado.getEspecialidad());
        verify(veterinarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById: deberia lanzar excepcion cuando el id no existe")
    void findById_cuandoNoExiste_lanzaRuntimeException() {
        // Given
        when(veterinarioRepository.findById(99L)).thenReturn(Optional.empty());

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> veterinarioService.findById(99L));

        assertEquals("Veterinario no encontrado con id: 99", ex.getMessage());
    }

    // delete() - eliminacion logica
    @Test
    @DisplayName("delete: deberia marcar activo=false en lugar de borrar fisicamente")
    void delete_marcaVeterinarioComoInactivo() {
        // Given
        when(veterinarioRepository.findById(1L))
                .thenReturn(Optional.of(veterinarioEjemplo));
        when(veterinarioRepository.save(any(Veterinario.class)))
                .thenReturn(veterinarioEjemplo);

        // When
        veterinarioService.delete(1L);

        // Then
        assertFalse(veterinarioEjemplo.getActivo());
        verify(veterinarioRepository, times(1)).save(veterinarioEjemplo);
        verify(veterinarioRepository, never()).deleteById(any());
    }

    // findAll(), findDisponibles(), findByEspecialidad(), search()
    @Test
    @DisplayName("findAll: deberia retornar todos los veterinarios activos")
    void findAll_retornaListaDeVeterinariosActivos() {
        // Given
        when(veterinarioRepository.findByActivoTrue())
                .thenReturn(List.of(veterinarioEjemplo));

        // When
        List<VeterinarioResponseDto> resultado = veterinarioService.findAll();

        // Then
        assertEquals(1, resultado.size());
        verify(veterinarioRepository, times(1)).findByActivoTrue();
    }

    @Test
    @DisplayName("findDisponibles: deberia retornar solo veterinarios disponibles")
    void findDisponibles_retornaVeterinariosDisponibles() {
        // Given
        when(veterinarioRepository.findByDisponibleTrue())
                .thenReturn(List.of(veterinarioEjemplo));

        // When
        List<VeterinarioResponseDto> resultado = veterinarioService.findDisponibles();

        // Then
        assertEquals(1, resultado.size());
        verify(veterinarioRepository, times(1)).findByDisponibleTrue();
    }

    @Test
    @DisplayName("findByEspecialidad: deberia retornar veterinarios de la especialidad indicada")
    void findByEspecialidad_retornaVeterinariosDeLaEspecialidad() {
        // Given
        when(veterinarioRepository.findByEspecialidadIgnoreCase("Cirugia"))
                .thenReturn(List.of(veterinarioEjemplo));

        // When
        List<VeterinarioResponseDto> resultado =
                veterinarioService.findByEspecialidad("Cirugia");

        // Then
        assertEquals(1, resultado.size());
        verify(veterinarioRepository, times(1)).findByEspecialidadIgnoreCase("Cirugia");
    }

    @Test
    @DisplayName("search: deberia retornar veterinarios que coincidan con el nombre o apellido")
    void search_retornaVeterinariosQueCoincidenConNombre() {
        // Given
        when(veterinarioRepository
                .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase("Car", "Car"))
                .thenReturn(List.of(veterinarioEjemplo));

        // When
        List<VeterinarioResponseDto> resultado = veterinarioService.search("Car");

        // Then
        assertEquals(1, resultado.size());
        verify(veterinarioRepository, times(1))
                .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase("Car", "Car");
    }

    // update() - actualizacion de datos del veterinario
    @Test
    @DisplayName("update: deberia actualizar los datos del veterinario existente")
    void update_cuandoExiste_actualizaDatosCorrectamente() {
        // Given
        VeterinarioRequestDto nuevoDato = new VeterinarioRequestDto();
        nuevoDato.setNombre("Carlos Editado");
        nuevoDato.setApellido("Perez");
        nuevoDato.setEspecialidad("Cirugia");
        nuevoDato.setEmail("carlos@clinica.com");
        nuevoDato.setTelefono("999999999");

        when(veterinarioRepository.findById(1L))
                .thenReturn(Optional.of(veterinarioEjemplo));
        when(veterinarioRepository.save(any(Veterinario.class)))
                .thenReturn(veterinarioEjemplo);

        // When
        VeterinarioResponseDto resultado = veterinarioService.update(1L, nuevoDato);

        // Then
        assertEquals("Carlos Editado", resultado.getNombre());
        verify(veterinarioRepository, times(1)).save(veterinarioEjemplo);
    }

    @Test
    @DisplayName("update: deberia lanzar excepcion si el veterinario no existe")
    void update_cuandoNoExiste_lanzaRuntimeException() {
        // Given
        when(veterinarioRepository.findById(99L)).thenReturn(Optional.empty());

        // When + Then
        assertThrows(RuntimeException.class,
                () -> veterinarioService.update(99L, requestDtoEjemplo));
        verify(veterinarioRepository, never()).save(any(Veterinario.class));
    }

    // addHorario() - usa el SEGUNDO repositorio: HorarioRepository
    @Test
    @DisplayName("addHorario: deberia guardar el horario cuando el veterinario existe")
    void addHorario_cuandoVeterinarioExiste_guardaHorarioCorrectamente() {
        // Given
        HorarioRequestDto horarioDto = new HorarioRequestDto();
        horarioDto.setIdVeterinario(1L);
        horarioDto.setDiaSemana("LUNES");
        horarioDto.setHoraInicio(LocalTime.of(9, 0));
        horarioDto.setHoraFin(LocalTime.of(18, 0));

        Horario horarioGuardado = Horario.builder()
                .id(1L)
                .idVeterinario(1L)
                .diaSemana("LUNES")
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(18, 0))
                .build();

        // Primero valida que el veterinario exista (VeterinarioRepository)
        when(veterinarioRepository.findById(1L))
                .thenReturn(Optional.of(veterinarioEjemplo));
        // Luego guarda en el OTRO repositorio (HorarioRepository)
        when(horarioRepository.save(any(Horario.class))).thenReturn(horarioGuardado);

        // When
        HorarioResponseDto resultado = veterinarioService.addHorario(horarioDto);

        // Then
        assertNotNull(resultado);
        assertEquals("LUNES", resultado.getDiaSemana());
        verify(veterinarioRepository, times(1)).findById(1L);
        verify(horarioRepository, times(1)).save(any(Horario.class));
    }

    @Test
    @DisplayName("addHorario: deberia lanzar excepcion si el veterinario no existe")
    void addHorario_cuandoVeterinarioNoExiste_lanzaRuntimeException() {
        // Given
        HorarioRequestDto horarioDto = new HorarioRequestDto();
        horarioDto.setIdVeterinario(99L);
        horarioDto.setDiaSemana("LUNES");
        horarioDto.setHoraInicio(LocalTime.of(9, 0));
        horarioDto.setHoraFin(LocalTime.of(18, 0));

        when(veterinarioRepository.findById(99L)).thenReturn(Optional.empty());

        // When + Then
        assertThrows(RuntimeException.class,
                () -> veterinarioService.addHorario(horarioDto));
        verify(horarioRepository, never()).save(any(Horario.class));
    }

    // getHorarios() - horarios de un veterinario especifico
    @Test
    @DisplayName("getHorarios: deberia retornar los horarios del veterinario")
    void getHorarios_retornaHorariosDelVeterinario() {
        // Given
        Horario horarioEjemplo = Horario.builder()
                .id(1L)
                .idVeterinario(1L)
                .diaSemana("LUNES")
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(18, 0))
                .build();

        when(horarioRepository.findByIdVeterinario(1L))
                .thenReturn(List.of(horarioEjemplo));

        // When
        List<HorarioResponseDto> resultado = veterinarioService.getHorarios(1L);

        // Then
        assertEquals(1, resultado.size());
        assertEquals("LUNES", resultado.get(0).getDiaSemana());
        verify(horarioRepository, times(1)).findByIdVeterinario(1L);
    }
}