package com.veterinaria.ms_veterinarios.repository;

import com.veterinaria.ms_veterinarios.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    Optional<Veterinario> findByEmail(String email);

    List<Veterinario> findByActivoTrue();

    List<Veterinario> findByDisponibleTrue();

    List<Veterinario> findByEspecialidadIgnoreCase(String especialidad);

    List<Veterinario> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
            String nombre, String apellido);

    boolean existsByEmail(String email);
}