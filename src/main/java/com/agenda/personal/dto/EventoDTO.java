
package com.agenda.personal.dto;

import java.time.Instant;

import com.agenda.personal.enums.EstadoEvento;
import com.agenda.personal.enums.TipoEvento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EventoDTO {

    @NotBlank(message = "El título del evento es obligatorio")
    @Size(max = 180, message = "El título no puede superar los 180 caracteres")
    private String titulo;

    private String descripcion;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private Instant fechaInicio;

    @NotNull(message = "La fecha de finalización es obligatoria")
    private Instant fechaFin;

    @Size(max = 255, message = "La ubicación no puede superar los 255 caracteres")
    private String ubicacion;

    @NotNull(message = "El tipo de evento es obligatorio")
    private TipoEvento tipo = TipoEvento.OTRO;

    @NotNull(message = "El estado del evento es obligatorio")
    private EstadoEvento estado = EstadoEvento.PROGRAMADO;

    private boolean todoElDia = false;

    public EventoDTO() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Instant getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Instant fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Instant getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Instant fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public EstadoEvento getEstado() {
        return estado;
    }

    public void setEstado(EstadoEvento estado) {
        this.estado = estado;
    }

    public boolean isTodoElDia() {
        return todoElDia;
    }

    public void setTodoElDia(boolean todoElDia) {
        this.todoElDia = todoElDia;
    }
}
