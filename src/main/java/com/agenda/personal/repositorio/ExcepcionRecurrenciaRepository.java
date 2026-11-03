package com.agenda.personal.repositorio;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.personal.entidad.ExcepcionRecurrencia;

public interface ExcepcionRecurrenciaRepository
        extends JpaRepository<ExcepcionRecurrencia, Long> {

    List<ExcepcionRecurrencia> findAllByEvento_IdOrderByFechaOriginalAsc(
            Long eventoId
    );

    Optional<ExcepcionRecurrencia> findByIdAndEvento_Id(
            Long id,
            Long eventoId
    );

    Optional<ExcepcionRecurrencia> findByEvento_IdAndFechaOriginal(
            Long eventoId,
            Instant fechaOriginal
    );

}