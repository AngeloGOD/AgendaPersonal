package com.agenda.personal.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.personal.entidad.Evento;
import com.agenda.personal.entidad.Recordatorio;
import com.agenda.personal.enums.TipoRecordatorio;
import com.agenda.personal.repositorio.EventoRepository;
import com.agenda.personal.repositorio.RecordatorioRepository;

@Service
@Transactional
public class ServicioRecordatorio {

    private final RecordatorioRepository recordatorioRepository;
    private final EventoRepository eventoRepository;

    public ServicioRecordatorio(
            RecordatorioRepository recordatorioRepository,
            EventoRepository eventoRepository) {

        this.recordatorioRepository = recordatorioRepository;
        this.eventoRepository = eventoRepository;
    }

    @Transactional(readOnly = true)
    public List<Recordatorio> obtenerRecordatorios(
            Long eventoId, Long usuarioId) {

        verificarEvento(eventoId, usuarioId);

        return recordatorioRepository
                .findAllByEvento_IdAndActivoTrue(eventoId);
    }

    public Recordatorio guardar(
            Recordatorio recordatorio,
            Long eventoId,
            Long usuarioId) {

        if (recordatorio == null) {
            throw new IllegalArgumentException(
                    "Los datos del recordatorio son obligatorios.");
        }

        validarRecordatorio(recordatorio);

        Evento evento = verificarEvento(eventoId, usuarioId);

        if (recordatorio.getId() == null) {

            recordatorio.setEvento(evento);

            return recordatorioRepository.save(recordatorio);
        }

        Recordatorio existente = recordatorioRepository
                .findByIdAndEvento_Id(recordatorio.getId(), eventoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El recordatorio no existe o no pertenece al evento."));

        existente.setMinutosAntes(recordatorio.getMinutosAntes());
        existente.setTipo(recordatorio.getTipo());
        existente.setActivo(recordatorio.isActivo());

        return recordatorioRepository.save(existente);
    }

    public boolean eliminar(
            Long recordatorioId,
            Long eventoId,
            Long usuarioId) {

        verificarEvento(eventoId, usuarioId);

        if (recordatorioId == null) {
            return false;
        }

        Optional<Recordatorio> recordatorio =
                recordatorioRepository.findByIdAndEvento_Id(
                        recordatorioId, eventoId);

        if (recordatorio.isEmpty()) {
            return false;
        }

        recordatorioRepository.delete(recordatorio.get());

        return true;
    }

    private void validarRecordatorio(Recordatorio recordatorio) {

        if (recordatorio.getMinutosAntes() == null
                || recordatorio.getMinutosAntes() < 0) {

            throw new IllegalArgumentException(
                    "Los minutos antes del evento deben ser cero o mayores.");
        }

        if (recordatorio.getTipo() == null) {
            throw new IllegalArgumentException(
                    "El tipo de recordatorio es obligatorio.");
        }

        if (recordatorio.getTipo() != TipoRecordatorio.NOTIFICACION) {
            throw new IllegalArgumentException(
                    "El tipo de recordatorio no es válido.");
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