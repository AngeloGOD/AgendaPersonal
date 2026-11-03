package com.agenda.personal.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.personal.entidad.ParticipanteEvento;

public interface ParticipanteEventoRepository
        extends JpaRepository<ParticipanteEvento, Long> {

    List<ParticipanteEvento> findAllByUsuarioIdAndEventoId(Long usuarioId, Long eventoId);

    List<ParticipanteEvento> findAllByUsuarioIdAndContactoId(Long usuarioId, Long contactoId);

}