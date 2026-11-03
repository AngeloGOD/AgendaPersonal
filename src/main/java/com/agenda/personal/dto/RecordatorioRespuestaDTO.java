
package com.agenda.personal.dto;

public class RecordatorioRespuestaDTO {

    private Long id;
    private Long eventoId;
    private Integer minutosAntes;
    private String tipo;
    private boolean activo;

    public RecordatorioRespuestaDTO() {
    }

    public RecordatorioRespuestaDTO(
            Long id,
            Long eventoId,
            Integer minutosAntes,
            String tipo,
            boolean activo) {

        this.id = id;
        this.eventoId = eventoId;
        this.minutosAntes = minutosAntes;
        this.tipo = tipo;
        this.activo = activo;
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
