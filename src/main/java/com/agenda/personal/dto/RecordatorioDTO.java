package com.agenda.personal.dto;

import com.agenda.personal.enums.TipoRecordatorio;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class RecordatorioDTO {

    @NotNull(message = "Los minutos de anticipación son obligatorios")
    @PositiveOrZero(message = "Los minutos de anticipación no pueden ser negativos")
    private Integer minutosAntes;

    @NotNull(message = "El tipo de recordatorio es obligatorio")
    private TipoRecordatorio tipo = TipoRecordatorio.NOTIFICACION;

    private boolean activo = true;

    public RecordatorioDTO() {
    }

    public Integer getMinutosAntes() {
        return minutosAntes;
    }

    public void setMinutosAntes(Integer minutosAntes) {
        this.minutosAntes = minutosAntes;
    }

    public TipoRecordatorio getTipo() {
        return tipo;
    }

    public void setTipo(TipoRecordatorio tipo) {
        this.tipo = tipo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}