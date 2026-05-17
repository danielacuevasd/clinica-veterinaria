package com.veterinaria.ms_notificaciones.repository;

import com.veterinaria.ms_notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByIdDestinatario(Long idDestinatario);

    List<Notificacion> findByTipo(String tipo);

    List<Notificacion> findByEnviado(Boolean enviado);

    List<Notificacion> findByIdDestinatarioOrderByFechaEnvioDesc(Long idDestinatario);
}
