package com.veterinaria.ms_citas.repository;

import com.veterinaria.ms_citas.model.Cita;
import com.veterinaria.ms_citas.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByIdDueno(Long idDueno);

    List<Cita> findByIdVeterinario(Long idVeterinario);

    List<Cita> findByIdMascota(Long idMascota);

    List<Cita> findByEstado(EstadoCita estado);

    List<Cita> findByIdVeterinarioAndFechaHoraBetween(
            Long idVeterinario, LocalDateTime inicio, LocalDateTime fin);

    List<Cita> findByIdDuenoAndEstado(Long idDueno, EstadoCita estado);
}
