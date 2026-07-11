package com.veterinaria.ms_usuarios.service;

import com.veterinaria.ms_usuarios.dto.DuenoRequestDto;
import com.veterinaria.ms_usuarios.dto.DuenoResponseDto;
import com.veterinaria.ms_usuarios.dto.MascotaDto;
import com.veterinaria.ms_usuarios.feign.MascotaClient;
import com.veterinaria.ms_usuarios.model.Dueno;
import com.veterinaria.ms_usuarios.repository.DuenoRepository;
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
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("DuenoService - Pruebas Unitarias (con Feign mockeado)")
class DuenoServiceTest {

    @Mock
    private DuenoRepository duenoRepository;

    @Mock
    private MascotaClient mascotaClient;

    @InjectMocks
    private DuenoService duenoService;

    private Dueno duenoEjemplo;
    private DuenoRequestDto requestDtoEjemplo;

    @BeforeEach
    void setUp() {
        duenoEjemplo = Dueno.builder()
                .id(1L)
                .nombre("Maria")
                .apellido("Gonzalez")
                .email("maria@email.com")
                .telefono("912345678")
                .rut("98765432-1")
                .activo(true)
                .createdAt(LocalDateTime.now())
                .build();

        requestDtoEjemplo = new DuenoRequestDto();
        requestDtoEjemplo.setNombre("Maria");
        requestDtoEjemplo.setApellido("Gonzalez");
        requestDtoEjemplo.setEmail("maria@email.com");
        requestDtoEjemplo.setTelefono("912345678");
        requestDtoEjemplo.setRut("98765432-1");
    }

    // findById() - casos exitoso y de error
    @Test
    @DisplayName("findById: deberia retornar el dueno cuando el id existe")
    void findById_cuandoExiste_retornaDuenoResponseDto() {
        // Given: el repositorio simula encontrar el dueno con id=1
        when(duenoRepository.findById(1L)).thenReturn(Optional.of(duenoEjemplo));

        // When: se llama al metodo del Service
        DuenoResponseDto resultado = duenoService.findById(1L);

        // Then: se valida que el resultado tenga los datos esperados
        assertNotNull(resultado);
        assertEquals("Maria", resultado.getNombre());
        assertEquals("maria@email.com", resultado.getEmail());
        verify(duenoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById: deberia lanzar RuntimeException cuando el id no existe")
    void findById_cuandoNoExiste_lanzaRuntimeException() {
        // Given: el repositorio simula que NO encuentra el dueno
        when(duenoRepository.findById(99L)).thenReturn(Optional.empty());

        // When + Then: se espera que el Service lance la excepcion con el mensaje correcto
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> duenoService.findById(99L));

        assertEquals("Dueno no encontrado con id: 99", ex.getMessage());
        verify(duenoRepository, times(1)).findById(99L);
    }


    // save() - regla de negocio: email y RUT duplicados
    @Test
    @DisplayName("save: deberia guardar el dueno cuando email y RUT no existen")
    void save_cuandoDatosValidos_guardaDuenoCorrectamente() {
        // Given: ni el email ni el RUT existen previamente
        when(duenoRepository.existsByEmail(requestDtoEjemplo.getEmail())).thenReturn(false);
        when(duenoRepository.existsByRut(requestDtoEjemplo.getRut())).thenReturn(false);
        when(duenoRepository.save(any(Dueno.class))).thenReturn(duenoEjemplo);

        // When
        DuenoResponseDto resultado = duenoService.save(requestDtoEjemplo);

        // Then
        assertNotNull(resultado);
        assertEquals("maria@email.com", resultado.getEmail());
        verify(duenoRepository, times(1)).save(any(Dueno.class));
    }

    @Test
    @DisplayName("save: deberia lanzar excepcion cuando el email ya existe")
    void save_cuandoEmailYaExiste_lanzaRuntimeException() {
        // Given: el email ya esta registrado
        when(duenoRepository.existsByEmail(requestDtoEjemplo.getEmail())).thenReturn(true);

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> duenoService.save(requestDtoEjemplo));

        assertEquals("Ya existe un dueno con el email: maria@email.com", ex.getMessage());
        // El save() NUNCA debe llamarse si la validacion de negocio falla
        verify(duenoRepository, never()).save(any(Dueno.class));
    }

    @Test
    @DisplayName("save: deberia lanzar excepcion cuando el RUT ya existe")
    void save_cuandoRutYaExiste_lanzaRuntimeException() {
        // Given: el email es nuevo, pero el RUT ya esta registrado
        when(duenoRepository.existsByEmail(requestDtoEjemplo.getEmail())).thenReturn(false);
        when(duenoRepository.existsByRut(requestDtoEjemplo.getRut())).thenReturn(true);

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> duenoService.save(requestDtoEjemplo));

        assertEquals("Ya existe un dueno con el RUT: 98765432-1", ex.getMessage());
        verify(duenoRepository, never()).save(any(Dueno.class));
    }

    // findAll() - lista de duenos activos
    @Test
    @DisplayName("findAll: deberia retornar solo duenos activos")
    void findAll_retornaListaDeDuenosActivos() {
        // Given
        when(duenoRepository.findByActivoTrue()).thenReturn(List.of(duenoEjemplo));

        // When
        List<DuenoResponseDto> resultado = duenoService.findAll();

        // Then
        assertEquals(1, resultado.size());
        assertEquals("Maria", resultado.get(0).getNombre());
        verify(duenoRepository, times(1)).findByActivoTrue();
    }

    // delete() - eliminacion logica (no borra fisicamente), ahora validando via Feign
    // que el dueno no tenga mascotas activas registradas
    @Test
    @DisplayName("delete: deberia marcar activo=false cuando el dueno no tiene mascotas activas")
    void delete_marcaDuenoComoInactivo() {
        // Given
        when(duenoRepository.findById(1L)).thenReturn(Optional.of(duenoEjemplo));
        when(mascotaClient.getMascotasByDueno(1L)).thenReturn(List.of()); // sin mascotas
        when(duenoRepository.save(any(Dueno.class))).thenReturn(duenoEjemplo);

        // When
        duenoService.delete(1L);

        // Then: se verifica que el dueno fue marcado inactivo antes de guardar
        assertFalse(duenoEjemplo.getActivo());
        verify(mascotaClient, times(1)).getMascotasByDueno(1L);
        verify(duenoRepository, times(1)).save(duenoEjemplo);
        // Confirma que NUNCA se llama a un metodo de borrado fisico
        verify(duenoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete: deberia lanzar excepcion si el dueno tiene mascotas activas registradas")
    void delete_cuandoTieneMascotasActivas_lanzaRuntimeException() {
        // Given: el dueno existe, pero tiene una mascota activa
        when(duenoRepository.findById(1L)).thenReturn(Optional.of(duenoEjemplo));

        MascotaDto mascotaActiva = new MascotaDto();
        mascotaActiva.setId(1L);
        mascotaActiva.setIdDueno(1L);
        mascotaActiva.setActivo(true);
        when(mascotaClient.getMascotasByDueno(1L)).thenReturn(List.of(mascotaActiva));

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> duenoService.delete(1L));

        assertEquals("No se puede eliminar el dueño: tiene mascotas activas registradas",
                ex.getMessage());
        // Nunca debe marcarse inactivo si tiene mascotas activas
        verify(duenoRepository, never()).save(any(Dueno.class));
    }

    @Test
    @DisplayName("delete: deberia permitir eliminar si las mascotas del dueno estan todas inactivas")
    void delete_cuandoMascotasEstanInactivas_marcaDuenoComoInactivo() {
        // Given: el dueno tiene mascotas registradas, pero todas inactivas
        when(duenoRepository.findById(1L)).thenReturn(Optional.of(duenoEjemplo));

        MascotaDto mascotaInactiva = new MascotaDto();
        mascotaInactiva.setId(1L);
        mascotaInactiva.setIdDueno(1L);
        mascotaInactiva.setActivo(false);
        when(mascotaClient.getMascotasByDueno(1L)).thenReturn(List.of(mascotaInactiva));
        when(duenoRepository.save(any(Dueno.class))).thenReturn(duenoEjemplo);

        // When
        duenoService.delete(1L);

        // Then
        assertFalse(duenoEjemplo.getActivo());
        verify(duenoRepository, times(1)).save(duenoEjemplo);
    }

    @Test
    @DisplayName("delete: deberia lanzar excepcion si el dueno no existe")
    void delete_cuandoNoExiste_lanzaRuntimeException() {
        // Given
        when(duenoRepository.findById(99L)).thenReturn(Optional.empty());

        // When + Then
        assertThrows(RuntimeException.class, () -> duenoService.delete(99L));
        verify(duenoRepository, never()).save(any(Dueno.class));
    }

    // update() - actualizacion de datos del dueno
    @Test
    @DisplayName("update: deberia actualizar los datos del dueno existente")
    void update_cuandoExiste_actualizaDatosCorrectamente() {
        // Given
        DuenoRequestDto nuevoDato = new DuenoRequestDto();
        nuevoDato.setNombre("Maria Editada");
        nuevoDato.setApellido("Gonzalez");
        nuevoDato.setEmail("maria@email.com");
        nuevoDato.setTelefono("999999999");
        nuevoDato.setRut("98765432-1");

        when(duenoRepository.findById(1L)).thenReturn(Optional.of(duenoEjemplo));
        when(duenoRepository.save(any(Dueno.class))).thenReturn(duenoEjemplo);

        // When
        DuenoResponseDto resultado = duenoService.update(1L, nuevoDato);

        // Then
        assertEquals("Maria Editada", resultado.getNombre());
        assertEquals("999999999", resultado.getTelefono());
        verify(duenoRepository, times(1)).save(duenoEjemplo);
    }

    @Test
    @DisplayName("update: deberia lanzar excepcion si el dueno no existe")
    void update_cuandoNoExiste_lanzaRuntimeException() {
        // Given
        when(duenoRepository.findById(99L)).thenReturn(Optional.empty());

        // When + Then
        assertThrows(RuntimeException.class,
                () -> duenoService.update(99L, requestDtoEjemplo));
        verify(duenoRepository, never()).save(any(Dueno.class));
    }

    // search() - busqueda por nombre o apellido parcial
    @Test
    @DisplayName("search: deberia retornar duenos que coincidan con el nombre o apellido")
    void search_retornaDuenosQueCoincidenConNombre() {
        // Given
        when(duenoRepository
                .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase("Mar", "Mar"))
                .thenReturn(List.of(duenoEjemplo));

        // When
        List<DuenoResponseDto> resultado = duenoService.search("Mar");

        // Then
        assertEquals(1, resultado.size());
        assertEquals("Maria", resultado.get(0).getNombre());
        verify(duenoRepository, times(1))
                .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase("Mar", "Mar");
    }
}