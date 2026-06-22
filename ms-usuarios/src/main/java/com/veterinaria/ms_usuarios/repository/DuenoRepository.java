package com.veterinaria.ms_usuarios.repository;

import com.veterinaria.ms_usuarios.model.Dueno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DuenoRepository extends JpaRepository<Dueno, Long> {

    Optional<Dueno> findByEmail(String email);

    Optional<Dueno> findByRut(String rut);

    List<Dueno> findByActivoTrue();

    List<Dueno> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
            String nombre, String apellido);

    boolean existsByEmail(String email);

    boolean existsByRut(String rut);
}