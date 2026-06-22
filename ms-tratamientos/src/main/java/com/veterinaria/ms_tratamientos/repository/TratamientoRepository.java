package com.veterinaria.ms_tratamientos.repository;

import com.veterinaria.ms_tratamientos.model.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {

    List<Tratamiento> findByIdConsulta(Long idConsulta);

    List<Tratamiento> findByIdMascota(Long idMascota);

    List<Tratamiento> findByIdMascotaOrderByCreatedAtDesc(Long idMascota);
}