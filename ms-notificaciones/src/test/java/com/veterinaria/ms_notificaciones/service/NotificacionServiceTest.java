package com.veterinaria.ms_notificaciones.service;

import com.veterinaria.ms_notificaciones.dto.NotificacionResponseDto;
import com.veterinaria.ms_notificaciones.model.Notificacion;
import com.veterinaria.ms_notificaciones.repository.NotificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificacionService - Pruebas Unitarias")
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    private Notificacion notificacionEjemplo;

    @BeforeEach
    void setUp() {
        notificacionEjemplo = Notificacion.builder()
                .id(1L)
                .tipo("CONSULTA_REGISTRADA")
                .idDestinatario(1L)
                .mensaje("Su mascota tuvo una consulta registrada")
                .enviado(true)
                .fechaEnvio(LocalDateTime.now())
                .build();
    }

    // findAll() - lista completa de notificaciones
    @Test
    @DisplayName("findAll: deberia retornar todas las notificaciones")
    void findAll_retornaListaDeNotificaciones() {
        // Given
        when(notificacionRepository.findAll()).thenReturn(List.of(notificacionEjemplo));

        // When
        List<NotificacionResponseDto> resultado = notificacionService.findAll();

        // Then
        assertEquals(1, resultado.size());
        assertEquals("CONSULTA_REGISTRADA", resultado.get(0).getTipo());
        verify(notificacionRepository, times(1)).findAll();
    }

    // findByDestinatario() - notificaciones de un dueno especifico
    @Test
    @DisplayName("findByDestinatario: deberia retornar las notificaciones del destinatario ordenadas por fecha")
    void findByDestinatario_retornaNotificacionesDelDestinatario() {
        // Given
        when(notificacionRepository.findByIdDestinatarioOrderByFechaEnvioDesc(1L))
                .thenReturn(List.of(notificacionEjemplo));

        // When
        List<NotificacionResponseDto> resultado =
                notificacionService.findByDestinatario(1L);

        // Then
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdDestinatario());
        verify(notificacionRepository, times(1))
                .findByIdDestinatarioOrderByFechaEnvioDesc(1L);
    }

    // findByTipo() - notificaciones filtradas por tipo de evento
    @Test
    @DisplayName("findByTipo: deberia retornar las notificaciones del tipo indicado")
    void findByTipo_retornaNotificacionesDelTipoIndicado() {
        // Given
        when(notificacionRepository.findByTipo("CONSULTA_REGISTRADA"))
                .thenReturn(List.of(notificacionEjemplo));

        // When
        List<NotificacionResponseDto> resultado =
                notificacionService.findByTipo("CONSULTA_REGISTRADA");

        // Then
        assertEquals(1, resultado.size());
        assertEquals("CONSULTA_REGISTRADA", resultado.get(0).getTipo());
        verify(notificacionRepository, times(1)).findByTipo("CONSULTA_REGISTRADA");
    }
}