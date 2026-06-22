package com.veterinaria.ms_inventario.repository;

import com.veterinaria.ms_inventario.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    Optional<Medicamento> findByNombre(String nombre);

    List<Medicamento> findByActivoTrue();

    List<Medicamento> findByStockLessThan(Integer stock);

    List<Medicamento> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByNombre(String nombre);
}