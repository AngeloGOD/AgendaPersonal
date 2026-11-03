
package com.agenda.personal.dto;

import java.time.Instant;

public class ExcepcionRecurrenciaRespuestaDTO {

    private Long id;
    private Long eventoId;
    private Instant fechaOriginal;
    private String tipo;
    private Instant nuevaFechaInicio;
    private Instant nuevaFechaFin;
    private String nuevaUbicacion;

    public ExcepcionRecurrenciaRespuestaDTO() {
    }

    public ExcepcionRecurrenciaRespuestaDTO(
            Long id,
            Long eventoId,
            Instant fechaOriginal,
            String tipo,
            Instant nuevaFechaInicio,
            Instant nuevaFechaFin,
            String nuevaUbicacion) {

        this.id = id;
        this.eventoId = eventoId;
        this.fechaOriginal = fechaOriginal;
        this.tipo = tipo;
        this.nuevaFechaInicio = nuevaFechaInicio;
        this.nuevaFechaFin = nuevaFechaFin;
        this.nuevaUbicacion = nuevaUbicacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public Instant getFechaOriginal() {
        return fechaOriginal;
    }

    public void setFechaOriginal(Instant fechaOriginal) {
        this.fechaOriginal = fechaOriginal;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Instant getNuevaFechaInicio() {
        return nuevaFechaInicio;
    }

    public void setNuevaFechaInicio(Instant nuevaFechaInicio) {
        this.nuevaFechaInicio = nuevaFechaInicio;
    }

    public Instant getNuevaFechaFin() {
        return nuevaFechaFin;
    }

    public void setNuevaFechaFin(Instant nuevaFechaFin) {
        this.nuevaFechaFin = nuevaFechaFin;
    }

    public String getNuevaUbicacion() {
        return nuevaUbicacion;
    }

    public void setNuevaUbicacion(String nuevaUbicacion) {
        this.nuevaUbicacion = nuevaUbicacion;
    }
}
