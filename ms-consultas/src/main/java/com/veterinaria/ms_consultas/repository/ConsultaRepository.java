package com.veterinaria.ms_consultas.repository;

import com.veterinaria.ms_consultas.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByIdMascota(Long idMascota);

    List<Consulta> findByIdVeterinario(Long idVeterinario);

    List<Consulta> findByIdCita(Long idCita);

    List<Consulta> findByIdMascotaOrderByFechaDesc(Long idMascota);
}