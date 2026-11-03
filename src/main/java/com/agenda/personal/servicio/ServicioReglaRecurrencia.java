
package com.agenda.personal.servicio;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.personal.entidad.Evento;
import com.agenda.personal.entidad.ReglaRecurrencia;
import com.agenda.personal.repositorio.EventoRepository;
import com.agenda.personal.repositorio.ReglaRecurrenciaRepository;

@Service
@Transactional
public class ServicioReglaRecurrencia {

    private final ReglaRecurrenciaRepository reglaRepository;
    private final EventoRepository eventoRepository;

    public ServicioReglaRecurrencia(
            ReglaRecurrenciaRepository reglaRepository,
            EventoRepository eventoRepository) {

        this.reglaRepository = reglaRepository;
        this.eventoRepository = eventoRepository;
    }

    @Transactional(readOnly = true)
    public Optional<ReglaRecurrencia> buscarPorEvento(
            Long eventoId, Long usuarioId) {

        verificarEvento(eventoId, usuarioId);

        return reglaRepository.findByEvento_Id(eventoId);
    }

    public ReglaRecurrencia guardar(
            ReglaRecurrencia regla,
            Long eventoId,
            Long usuarioId) {

        if (regla == null) {
            throw new IllegalArgumentException(
                    "Los datos de la recurrencia son obligatorios.");
        }

        validarRegla(regla);

        Evento evento = verificarEvento(eventoId, usuarioId);

        Optional<ReglaRecurrencia> existente =
                reglaRepository.findByEvento_Id(eventoId);

        if (existente.isPresent()) {

            ReglaRecurrencia actual = existente.get();

            actual.setFrecuencia(regla.getFrecuencia());
            actual.setIntervalo(regla.getIntervalo());
            actual.setDiasSemana(regla.getDiasSemana());
            actual.setDiaMes(regla.getDiaMes());
            actual.setFechaFin(regla.getFechaFin());
            actual.setNumeroOcurrencias(regla.getNumeroOcurrencias());

            return reglaRepository.save(actual);
        }

        regla.setEvento(evento);

        return reglaRepository.save(regla);
    }

    public boolean eliminar(Long eventoId, Long usuarioId) {

        verificarEvento(eventoId, usuarioId);

        Optional<ReglaRecurrencia> regla =
                reglaRepository.findByEvento_Id(eventoId);

        if (regla.isEmpty()) {
            return false;
        }

        reglaRepository.delete(regla.get());

        return true;
    }

    private void validarRegla(ReglaRecurrencia regla) {

        if (regla.getFrecuencia() == null
                || regla.getFrecuencia().isBlank()) {

            throw new IllegalArgumentException(
                    "La frecuencia de recurrencia es obligatoria.");
        }

        String frecuencia = regla.getFrecuencia()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!Arrays.asList(
                "DIARIA", "SEMANAL", "MENSUAL", "ANUAL")
                .contains(frecuencia)) {

            throw new IllegalArgumentException(
                    "La frecuencia debe ser DIARIA, SEMANAL, MENSUAL o ANUAL.");
        }

        regla.setFrecuencia(frecuencia);

        if (regla.getIntervalo() == null
                || regla.getIntervalo() <= 0) {

            throw new IllegalArgumentException(
                    "El intervalo de recurrencia debe ser mayor que cero.");
        }

        if (regla.getFechaFin() != null
                && regla.getNumeroOcurrencias() != null) {

            throw new IllegalArgumentException(
                    "No puedes establecer una fecha de finalización "
                    + "y un número de ocurrencias al mismo tiempo.");
        }

        if (regla.getNumeroOcurrencias() != null
                && regla.getNumeroOcurrencias() <= 0) {

            throw new IllegalArgumentException(
                    "El número de ocurrencias debe ser mayor que cero.");
        }

        if (regla.getDiasSemana() != null) {

            for (Short dia : regla.getDiasSemana()) {

                if (dia == null || dia < 1 || dia > 7) {
                    throw new IllegalArgumentException(
                            "Los días de la semana deben estar entre 1 y 7.");
                }
            }
        }

        if (regla.getDiaMes() != null
                && (regla.getDiaMes() < 1 || regla.getDiaMes() > 31)) {

            throw new IllegalArgumentException(
                    "El día del mes debe estar entre 1 y 31.");
        }

        if ("SEMANAL".equals(frecuencia)
                && (regla.getDiasSemana() == null
                || regla.getDiasSemana().length == 0)) {

            throw new IllegalArgumentException(
                    "Debes seleccionar al menos un día de la semana "
                    + "para una recurrencia semanal.");
        }

        if ("MENSUAL".equals(frecuencia)
                && regla.getDiaMes() == null) {

            throw new IllegalArgumentException(
                    "Debes indicar el día del mes para una recurrencia mensual.");
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
