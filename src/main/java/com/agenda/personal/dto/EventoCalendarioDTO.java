package com.agenda.personal.dto;

import java.time.Instant;

import com.agenda.personal.entidad.Evento;

public class EventoCalendarioDTO extends EventoRespuestaDTO {

    private boolean recurrente;
    private Instant fechaOriginal;

    public EventoCalendarioDTO(
            Evento evento,
            Instant fechaInicio,
            Instant fechaFin,
            String ubicacion,
            boolean recurrente,
            Instant fechaOriginal) {

        super(
                evento.getId(),
                evento.getTitulo(),
                evento.getDescripcion(),
                fechaInicio,
                fechaFin,
                ubicacion,
                evento.getTipo().name(),
                evento.getEstado().name(),
                evento.isTodoElDia(),
                evento.getFechaCreacion(),
                evento.getFechaActualizacion()
        );

        this.recurrente = recurrente;
        this.fechaOriginal = fechaOriginal;
    }

    public boolean isRecurrente() {
        return recurrente;
    }

    public void setRecurrente(boolean recurrente) {
        this.recurrente = recurrente;
    }

    public Instant getFechaOriginal() {
        return fechaOriginal;
    }

    public void setFechaOriginal(Instant fechaOriginal) {
        this.fechaOriginal = fechaOriginal;
    }
}