
package com.agenda.personal.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ExcepcionRecurrenciaDTO {

    @NotNull(message = "La fecha original es obligatoria")
    private Instant fechaOriginal;

    @NotBlank(message = "El tipo de excepción es obligatorio")
    @Size(max = 20, message = "El tipo no puede superar los 20 caracteres")
    private String tipo;

    private Instant nuevaFechaInicio;

    private Instant nuevaFechaFin;

    @Size(max = 255, message = "La ubicación no puede superar los 255 caracteres")
    private String nuevaUbicacion;

    public ExcepcionRecurrenciaDTO() {
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
