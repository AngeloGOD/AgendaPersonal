package com.agenda.personal.repositorio;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.personal.entidad.Evento;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findAllByUsuario_IdAndFechaInicioBetweenOrderByFechaInicioAsc(
            Long usuarioId,
            Instant fechaInicio,
            Instant fechaFin
    );

    Optional<Evento> findByIdAndUsuario_Id(Long id, Long usuarioId);

}