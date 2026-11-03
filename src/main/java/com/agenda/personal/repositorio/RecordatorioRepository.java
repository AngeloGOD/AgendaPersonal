package com.agenda.personal.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.personal.entidad.Recordatorio;

public interface RecordatorioRepository extends JpaRepository<Recordatorio, Long> {

    List<Recordatorio> findAllByEvento_IdAndActivoTrue(Long eventoId);

    Optional<Recordatorio> findByIdAndEvento_Id(Long id, Long eventoId);

}