
package com.agenda.personal.servicio;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.personal.entidad.Evento;
import com.agenda.personal.entidad.ExcepcionRecurrencia;
import com.agenda.personal.repositorio.EventoRepository;
import com.agenda.personal.repositorio.ExcepcionRecurrenciaRepository;

@Service
@Transactional
public class ServicioExcepcionRecurrencia {

    private final ExcepcionRecurrenciaRepository excepcionRepository;
    private final EventoRepository eventoRepository;

    public ServicioExcepcionRecurrencia(
            ExcepcionRecurrenciaRepository excepcionRepository,
            EventoRepository eventoRepository) {

        this.excepcionRepository = excepcionRepository;
        this.eventoRepository = eventoRepository;
    }

    @Transactional(readOnly = true)
    public List<ExcepcionRecurrencia> obtenerExcepciones(
            Long eventoId, Long usuarioId) {

        verificarEvento(eventoId, usuarioId);

        return excepcionRepository
                .findAllByEvento_IdOrderByFechaOriginalAsc(eventoId);
    }

    @Transactional(readOnly = true)
    public Optional<ExcepcionRecurrencia> buscarPorFechaOriginal(
            Long eventoId,
            Instant fechaOriginal,
            Long usuarioId) {

        verificarEvento(eventoId, usuarioId);

        if (fechaOriginal == null) {
            throw new IllegalArgumentException(
                    "La fecha original es obligatoria.");
        }

        return excepcionRepository
                .findByEvento_IdAndFechaOriginal(eventoId, fechaOriginal);
    }

    public ExcepcionRecurrencia guardar(
            ExcepcionRecurrencia excepcion,
            Long eventoId,
            Long usuarioId) {

        if (excepcion == null) {
            throw new IllegalArgumentException(
                    "Los datos de la excepción son obligatorios.");
        }

        validarExcepcion(excepcion);

        Evento evento = verificarEvento(eventoId, usuarioId);

        verificarFechaDuplicada(
                eventoId,
                excepcion.getFechaOriginal(),
                excepcion.getId());

        if (excepcion.getId() == null) {

            excepcion.setEvento(evento);

            return excepcionRepository.save(excepcion);
        }

        ExcepcionRecurrencia existente = excepcionRepository
                .findByIdAndEvento_Id(excepcion.getId(), eventoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La excepción no existe o no pertenece al evento."));

        existente.setFechaOriginal(excepcion.getFechaOriginal());
        existente.setTipo(excepcion.getTipo());
        existente.setNuevaFechaInicio(excepcion.getNuevaFechaInicio());
        existente.setNuevaFechaFin(excepcion.getNuevaFechaFin());
        existente.setNuevaUbicacion(excepcion.getNuevaUbicacion());

        return excepcionRepository.save(existente);
    }

    public boolean eliminar(
            Long excepcionId,
            Long eventoId,
            Long usuarioId) {

        verificarEvento(eventoId, usuarioId);

        if (excepcionId == null) {
            return false;
        }

        Optional<ExcepcionRecurrencia> excepcion =
                excepcionRepository.findByIdAndEvento_Id(
                        excepcionId, eventoId);

        if (excepcion.isEmpty()) {
            return false;
        }

        excepcionRepository.delete(excepcion.get());

        return true;
    }

    private void validarExcepcion(ExcepcionRecurrencia excepcion) {

        if (excepcion.getFechaOriginal() == null) {
            throw new IllegalArgumentException(
                    "La fecha original de la ocurrencia es obligatoria.");
        }

        if (excepcion.getTipo() == null
                || excepcion.getTipo().isBlank()) {

            throw new IllegalArgumentException(
                    "El tipo de excepción es obligatorio.");
        }

        String tipo = excepcion.getTipo()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!tipo.equals("OMITIR") && !tipo.equals("MODIFICAR")) {
            throw new IllegalArgumentException(
                    "El tipo de excepción debe ser OMITIR o MODIFICAR.");
        }

        excepcion.setTipo(tipo);

        if ("MODIFICAR".equals(tipo)) {

            if (excepcion.getNuevaFechaInicio() == null) {
                throw new IllegalArgumentException(
                        "Debes indicar la nueva fecha de inicio "
                        + "para modificar una ocurrencia.");
            }

            Instant nuevaFechaFin = excepcion.getNuevaFechaFin();

            if (nuevaFechaFin != null
                    && nuevaFechaFin.isBefore(
                            excepcion.getNuevaFechaInicio())) {

                throw new IllegalArgumentException(
                        "La nueva fecha de finalización no puede ser "
                        + "anterior a la nueva fecha de inicio.");
            }
        }

        if ("OMITIR".equals(tipo)) {

            if (excepcion.getNuevaFechaInicio() != null
                    || excepcion.getNuevaFechaFin() != null
                    || excepcion.getNuevaUbicacion() != null) {

                throw new IllegalArgumentException(
                        "Una excepción de tipo OMITIR no debe contener "
                        + "datos de modificación.");
            }
        }
    }

    private void verificarFechaDuplicada(
            Long eventoId,
            Instant fechaOriginal,
            Long excepcionActualId) {

        Optional<ExcepcionRecurrencia> existente =
                excepcionRepository
                        .findByEvento_IdAndFechaOriginal(
                                eventoId, fechaOriginal);

        if (existente.isPresent()
                && !Objects.equals(
                        existente.get().getId(), excepcionActualId)) {

            throw new IllegalArgumentException(
                    "Ya existe una excepción para esa ocurrencia del evento.");
        }
    }

    private Evento verificarEvento(Long eventoId, Long usuarioId) {

        if (eventoId == null || usuarioId == null) {
            throw new IllegalArgumentException(
                    "El evento y el usuario son obligatorios.");
        }

        return eventoRepository
                .findByIdAndUsuario_Id(eventoId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El evento no existe o no pertenece al usuario."));
    }
}
