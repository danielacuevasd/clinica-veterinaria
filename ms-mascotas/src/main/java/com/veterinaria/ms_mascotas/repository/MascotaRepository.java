package com.veterinaria.ms_mascotas.repository;

import com.veterinaria.ms_mascotas.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    List<Mascota> findByIdDueno(Long idDueno);

    List<Mascota> findByActivoTrue();

    List<Mascota> findByEspecieIgnoreCase(String especie);

    List<Mascota> findByNombreContainingIgnoreCase(String nombre);

    List<Mascota> findByIdDuenoAndActivoTrue(Long idDueno);
}