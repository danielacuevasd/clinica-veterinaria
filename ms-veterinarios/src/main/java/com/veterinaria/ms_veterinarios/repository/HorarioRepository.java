package com.veterinaria.ms_veterinarios.repository;

import com.veterinaria.ms_veterinarios.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {

    List<Horario> findByIdVeterinario(Long idVeterinario);

    List<Horario> findByIdVeterinarioAndDiaSemana(Long idVeterinario, String diaSemana);
}