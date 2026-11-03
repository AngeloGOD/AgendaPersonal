
package com.agenda.personal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class RecordatorioDTO {

    @NotNull(message = "Los minutos de anticipación son obligatorios")
    @PositiveOrZero(message = "Los minutos de anticipación no pueden ser negativos")
    private Integer minutosAntes;

    @NotBlank(message = "El tipo de recordatorio es obligatorio")
    @Size(max = 20, message = "El tipo no puede superar los 20 caracteres")
    private String tipo = "NOTIFICACION";

    private boolean activo = true;

    public RecordatorioDTO() {
    }

    public Integer getMinutosAntes() {
        return minutosAntes;
    }

    public void setMinutosAntes(Integer minutosAntes) {
        this.minutosAntes = minutosAntes;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
