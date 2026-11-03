
package com.agenda.personal.dto;

import java.time.LocalDate;

public class ReglaRecurrenciaRespuestaDTO {

    private Long id;
    private Long eventoId;
    private String frecuencia;
    private Integer intervalo;
    private Short[] diasSemana;
    private Integer diaMes;
    private LocalDate fechaFin;
    private Integer numeroOcurrencias;

    public ReglaRecurrenciaRespuestaDTO() {
    }

    public ReglaRecurrenciaRespuestaDTO(
            Long id,
            Long eventoId,
            String frecuencia,
            Integer intervalo,
            Short[] diasSemana,
            Integer diaMes,
            LocalDate fechaFin,
            Integer numeroOcurrencias) {

        this.id = id;
        this.eventoId = eventoId;
        this.frecuencia = frecuencia;
        this.intervalo = intervalo;
        this.diasSemana = diasSemana;
        this.diaMes = diaMes;
        this.fechaFin = fechaFin;
        this.numeroOcurrencias = numeroOcurrencias;
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

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
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
