package com.veterinaria.ms_inventario.repository;

import com.veterinaria.ms_inventario.model.MovimientoStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {

    List<MovimientoStock> findByIdMedicamentoOrderByFechaDesc(Long idMedicamento);
}