package com.agenda.personal.repositorio;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.agenda.personal.entidad.ReglaRecurrencia;

public interface ReglaRecurrenciaRepository
        extends JpaRepository<ReglaRecurrencia, Long> {

    Optional<ReglaRecurrencia> findByEvento_Id(Long eventoId);

    Optional<ReglaRecurrencia> findByIdAndEvento_Id(
            Long id,
            Long eventoId
    );

    @Query("""
            SELECT r
            FROM ReglaRecurrencia r
            JOIN FETCH r.evento e
            WHERE e.usuario.id = :usuarioId
              AND e.fechaInicio <= :fechaFin
            """)
    List<ReglaRecurrencia> buscarReglasDelUsuario(
            @Param("usuarioId") Long usuarioId,
            @Param("fechaFin") Instant fechaFin
    );
}