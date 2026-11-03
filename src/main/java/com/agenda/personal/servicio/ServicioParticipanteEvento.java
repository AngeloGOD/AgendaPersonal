
package com.agenda.personal.servicio;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.personal.entidad.ParticipanteEvento;
import com.agenda.personal.repositorio.ContactoRepository;
import com.agenda.personal.repositorio.EventoRepository;
import com.agenda.personal.repositorio.ParticipanteEventoRepository;

@Service
@Transactional
public class ServicioParticipanteEvento {

    private final ParticipanteEventoRepository participanteRepository;
    private final EventoRepository eventoRepository;
    private final ContactoRepository contactoRepository;

    public ServicioParticipanteEvento(
            ParticipanteEventoRepository participanteRepository,
            EventoRepository eventoRepository,
            ContactoRepository contactoRepository) {

        this.participanteRepository = participanteRepository;
        this.eventoRepository = eventoRepository;
        this.contactoRepository = contactoRepository;
    }

    @Transactional(readOnly = true)
    public List<ParticipanteEvento> obtenerParticipantes(
            Long eventoId, Long usuarioId) {

        verificarEvento(eventoId, usuarioId);

        return participanteRepository
                .findAllByUsuarioIdAndEventoId(usuarioId, eventoId);
    }

    public ParticipanteEvento agregarParticipante(
            Long eventoId, Long contactoId, Long usuarioId) {

        verificarEvento(eventoId, usuarioId);
        verificarContacto(contactoId, usuarioId);

        boolean existe = participanteRepository
                .findAllByUsuarioIdAndEventoId(usuarioId, eventoId)
                .stream()
                .anyMatch(participante ->
                        Objects.equals(
                                participante.getContactoId(), contactoId));

        if (existe) {
            throw new IllegalArgumentException(
                    "El contacto ya participa en este evento.");
        }

        ParticipanteEvento participante = new ParticipanteEvento();

        participante.setEventoId(eventoId);
        participante.setContactoId(contactoId);
        participante.setUsuarioId(usuarioId);

        return participanteRepository.save(participante);
    }

    public boolean quitarParticipante(
            Long eventoId, Long contactoId, Long usuarioId) {

        verificarEvento(eventoId, usuarioId);
        verificarContacto(contactoId, usuarioId);

        return participanteRepository
                .findAllByUsuarioIdAndEventoId(usuarioId, eventoId)
                .stream()
                .filter(participante ->
                        Objects.equals(
                                participante.getContactoId(), contactoId))
                .findFirst()
                .map(participante -> {
                    participanteRepository.delete(participante);
                    return true;
                })
                .orElse(false);
    }

    private void verificarEvento(Long eventoId, Long usuarioId) {

        if (eventoId == null || usuarioId == null) {
            throw new IllegalArgumentException(
                    "El evento y el usuario son obligatorios.");
        }

        if (eventoRepository
                .findByIdAndUsuario_Id(eventoId, usuarioId)
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "El evento no existe o no pertenece al usuario.");
        }
    }

    private void verificarContacto(Long contactoId, Long usuarioId) {

        if (contactoId == null || usuarioId == null) {
            throw new IllegalArgumentException(
                    "El contacto y el usuario son obligatorios.");
        }

        if (contactoRepository
                .findByIdAndUsuario_Id(contactoId, usuarioId)
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "El contacto no existe o no pertenece al usuario.");
        }
    }
}
