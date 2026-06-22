package com.veterinaria.ms_facturacion.repository;

import com.veterinaria.ms_facturacion.model.EstadoFactura;
import com.veterinaria.ms_facturacion.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    List<Factura> findByIdDueno(Long idDueno);

    List<Factura> findByIdConsulta(Long idConsulta);

    List<Factura> findByEstado(EstadoFactura estado);

    List<Factura> findByIdDuenoAndEstado(Long idDueno, EstadoFactura estado);
}
