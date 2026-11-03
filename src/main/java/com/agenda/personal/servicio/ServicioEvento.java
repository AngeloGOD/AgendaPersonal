package com.agenda.personal.servicio;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.personal.entidad.Evento;
import com.agenda.personal.entidad.Usuario;
import com.agenda.personal.repositorio.EventoRepository;
import com.agenda.personal.repositorio.UsuarioRepository;

@Service
@Transactional
public class ServicioEvento {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;

    public ServicioEvento(
            EventoRepository eventoRepository,
            UsuarioRepository usuarioRepository) {

        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Evento> obtenerEventos(
            Long usuarioId,
            Instant fechaInicio,
            Instant fechaFin) {

        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException(
                    "Las fechas del intervalo son obligatorias.");
        }

        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException(
                    "La fecha final no puede ser anterior a la inicial.");
        }

        return eventoRepository
                .findAllByUsuario_IdAndFechaInicioBetweenOrderByFechaInicioAsc(
                        usuarioId, fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public Optional<Evento> buscarPorId(Long id, Long usuarioId) {

        if (id == null || usuarioId == null) {
            return Optional.empty();
        }

        return eventoRepository.findByIdAndUsuario_Id(id, usuarioId);
    }

    public Evento guardar(Evento evento, Long usuarioId) {

        if (evento == null) {
            throw new IllegalArgumentException(
                    "Los datos del evento son obligatorios.");
        }

        if (evento.getTitulo() == null
                || evento.getTitulo().isBlank()) {

            throw new IllegalArgumentException(
                    "El título del evento es obligatorio.");
        }

        if (evento.getFechaInicio() == null
                || evento.getFechaFin() == null) {

            throw new IllegalArgumentException(
                    "Las fechas de inicio y finalización son obligatorias.");
        }

        if (evento.getFechaFin().isBefore(evento.getFechaInicio())) {
            throw new IllegalArgumentException(
                    "La fecha final no puede ser anterior a la fecha inicial.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El usuario indicado no existe."));

        // Crear un evento nuevo.
        if (evento.getId() == null) {

            evento.setUsuario(usuario);
            evento.setTitulo(evento.getTitulo().trim());

            return eventoRepository.save(evento);
        }

        // Buscar el evento comprobando su propietario.
        Evento existente = buscarPorId(evento.getId(), usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El evento no existe o no pertenece al usuario."));

        // Actualizar únicamente los datos del evento.
        existente.setTitulo(evento.getTitulo().trim());
        existente.setDescripcion(evento.getDescripcion());
        existente.setFechaInicio(evento.getFechaInicio());
        existente.setFechaFin(evento.getFechaFin());
        existente.setUbicacion(evento.getUbicacion());
        existente.setTipo(evento.getTipo());
        existente.setEstado(evento.getEstado());
        existente.setTodoElDia(evento.isTodoElDia());

        return eventoRepository.save(existente);
    }

    public boolean eliminar(Long id, Long usuarioId) {

        Optional<Evento> evento = buscarPorId(id, usuarioId);

        if (evento.isEmpty()) {
            return false;
        }

        eventoRepository.delete(evento.get());

        return true;
    }
}