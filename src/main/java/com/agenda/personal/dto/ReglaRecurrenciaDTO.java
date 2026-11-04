package com.agenda.personal.dto;

import java.time.LocalDate;

import com.agenda.personal.enums.FrecuenciaRecurrencia;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ReglaRecurrenciaDTO {

    @NotNull(message = "La frecuencia es obligatoria")
    private FrecuenciaRecurrencia frecuencia;

    @NotNull(message = "El intervalo es obligatorio")
    @Positive(message = "El intervalo debe ser mayor que cero")
    private Integer intervalo = 1;

    private Short[] diasSemana;

    @Min(value = 1, message = "El día del mes debe ser como mínimo 1")
    @Max(value = 31, message = "El día del mes no puede superar 31")
    private Integer diaMes;

    private LocalDate fechaFin;

    @Positive(message = "El número de ocurrencias debe ser mayor que cero")
    private Integer numeroOcurrencias;

    public ReglaRecurrenciaDTO() {
    }

    public FrecuenciaRecurrencia getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(FrecuenciaRecurrencia frecuencia) {
        this.frecuencia = frecuencia;
    }

    public Integer getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Integer intervalo) {
        this.intervalo = intervalo;
    }

    public Short[] getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(Short[] diasSemana) {
        this.diasSemana = diasSemana;
    }

    public Integer getDiaMes() {
        return diaMes;
    }

    public void setDiaMes(Integer diaMes) {
        this.diaMes = diaMes;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getNumeroOcurrencias() {
        return numeroOcurrencias;
    }

    public void setNumeroOcurrencias(Integer numeroOcurrencias) {
        this.numeroOcurrencias = numeroOcurrencias;
    }
}