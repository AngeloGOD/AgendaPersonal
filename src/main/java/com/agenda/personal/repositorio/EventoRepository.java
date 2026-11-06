package com.agenda.personal.repositorio;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.agenda.personal.entidad.Evento;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    @Query("""
            SELECT e
            FROM Evento e
            WHERE e.usuario.id = :usuarioId
              AND e.fechaInicio <= :fechaFin
              AND e.fechaFin >= :fechaInicio
            ORDER BY e.fechaInicio ASC
            """)
    List<Evento> buscarEventosEnIntervalo(
            @Param("usuarioId") Long usuarioId,
            @Param("fechaInicio") Instant fechaInicio,
            @Param("fechaFin") Instant fechaFin
    );

    Optional<Evento> findByIdAndUsuario_Id(
            Long id,
            Long usuarioId
    );
}