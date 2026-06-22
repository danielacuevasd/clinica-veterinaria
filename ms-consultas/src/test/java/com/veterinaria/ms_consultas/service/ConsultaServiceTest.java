package com.veterinaria.ms_consultas.service;

import com.veterinaria.ms_consultas.dto.ConsultaEventDto;
import com.veterinaria.ms_consultas.dto.ConsultaRequestDto;
import com.veterinaria.ms_consultas.dto.ConsultaResponseDto;
import com.veterinaria.ms_consultas.kafka.ConsultaProducer;
import com.veterinaria.ms_consultas.model.Consulta;
import com.veterinaria.ms_consultas.repository.ConsultaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
@DisplayName("ConsultaService - Pruebas Unitarias (con Kafka mockeado)")
class ConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private ConsultaProducer consultaProducer;

    @InjectMocks
    private ConsultaService consultaService;

    private ConsultaRequestDto requestDtoEjemplo;
    private Consulta consultaGuardadaEjemplo;

    @BeforeEach
    void setUp() {
        requestDtoEjemplo = new ConsultaRequestDto();
        requestDtoEjemplo.setIdCita(1L);
        requestDtoEjemplo.setIdMascota(1L);
        requestDtoEjemplo.setIdVeterinario(1L);
        requestDtoEjemplo.setDiagnostico("Paciente en buen estado de salud");
        requestDtoEjemplo.setPeso(new BigDecimal("4.2"));
        requestDtoEjemplo.setTemperatura(new BigDecimal("38.5"));
        requestDtoEjemplo.setObservaciones("Control sin novedad");

        // Esto simula lo que el repository devuelve DESPUES de guardar (ya con id)
        consultaGuardadaEjemplo = Consulta.builder()
                .id(1L)
                .idCita(1L)
                .idMascota(1L)
                .idVeterinario(1L)
                .diagnostico("Paciente en buen estado de salud")
                .peso(new BigDecimal("4.2"))
                .temperatura(new BigDecimal("38.5"))
                .observaciones("Control sin novedad")
                .fecha(LocalDateTime.now())
                .build();
    }

    // save() - guarda en BD Y publica evento Kafka
    @Test
    @DisplayName("save: deberia guardar la consulta y publicar el evento en Kafka")
    void save_guardaConsultaYPublicaEventoKafka() {
        // Given
        when(consultaRepository.save(any(Consulta.class)))
                .thenReturn(consultaGuardadaEjemplo);

        // When
        ConsultaResponseDto resultado = consultaService.save(requestDtoEjemplo);

        // Then: se guardo correctamente en la BD
        assertNotNull(resultado);
        assertEquals("Paciente en buen estado de salud", resultado.getDiagnostico());
        verify(consultaRepository, times(1)).save(any(Consulta.class));

        // Then: se publico el evento en Kafka exactamente una vez
        verify(consultaProducer, times(1))
                .publicarConsultaRegistrada(any(ConsultaEventDto.class));
    }

    @Test
    @DisplayName("save: el evento Kafka publicado deberia contener los datos correctos de la consulta")
    void save_eventoKafkaContieneLosDatosCorrectos() {
        // Given
        when(consultaRepository.save(any(Consulta.class)))
                .thenReturn(consultaGuardadaEjemplo);

        // When
        consultaService.save(requestDtoEjemplo);

        // Then: capturamos el objeto EXACTO que se le paso al producer
        ArgumentCaptor<ConsultaEventDto> capturador =
                ArgumentCaptor.forClass(ConsultaEventDto.class);
        verify(consultaProducer).publicarConsultaRegistrada(capturador.capture());

        ConsultaEventDto eventoCapturado = capturador.getValue();
        assertEquals(1L, eventoCapturado.getIdConsulta());
        assertEquals(1L, eventoCapturado.getIdMascota());
        assertEquals("Paciente en buen estado de salud", eventoCapturado.getDiagnostico());
    }

    // findById() - caso exitoso y de error
    @Test
    @DisplayName("findById: deberia retornar la consulta cuando el id existe")
    void findById_cuandoExiste_retornaConsultaResponseDto() {
        // Given
        when(consultaRepository.findById(1L))
                .thenReturn(Optional.of(consultaGuardadaEjemplo));

        // When
        ConsultaResponseDto resultado = consultaService.findById(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("Paciente en buen estado de salud", resultado.getDiagnostico());
        verify(consultaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById: deberia lanzar excepcion cuando el id no existe")
    void findById_cuandoNoExiste_lanzaRuntimeException() {
        // Given
        when(consultaRepository.findById(99L)).thenReturn(Optional.empty());

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> consultaService.findById(99L));

        assertEquals("Consulta no encontrada con id: 99", ex.getMessage());
    }


    // findByMascota() - historial de consultas de una mascota

    @Test
    @DisplayName("findByMascota: deberia retornar las consultas de la mascota ordenadas por fecha")
    void findByMascota_retornaConsultasDeLaMascota() {
        // Given
        when(consultaRepository.findByIdMascotaOrderByFechaDesc(1L))
                .thenReturn(List.of(consultaGuardadaEjemplo));

        // When
        List<ConsultaResponseDto> resultado = consultaService.findByMascota(1L);

        // Then
        assertEquals(1, resultado.size());
        verify(consultaRepository, times(1)).findByIdMascotaOrderByFechaDesc(1L);
    }

    // findByVeterinario() - consultas atendidas por un veterinario
    @Test
    @DisplayName("findByVeterinario: deberia retornar las consultas del veterinario")
    void findByVeterinario_retornaConsultasDelVeterinario() {
        // Given
        when(consultaRepository.findByIdVeterinario(1L))
                .thenReturn(List.of(consultaGuardadaEjemplo));

        // When
        List<ConsultaResponseDto> resultado = consultaService.findByVeterinario(1L);

        // Then
        assertEquals(1, resultado.size());
        verify(consultaRepository, times(1)).findByIdVeterinario(1L);
    }

    // findAll() - lista completa de consultas
    @Test
    @DisplayName("findAll: deberia retornar todas las consultas")
    void findAll_retornaListaDeConsultas() {
        // Given
        when(consultaRepository.findAll())
                .thenReturn(List.of(consultaGuardadaEjemplo));

        // When
        List<ConsultaResponseDto> resultado = consultaService.findAll();

        // Then
        assertEquals(1, resultado.size());
        verify(consultaRepository, times(1)).findAll();
    }
}