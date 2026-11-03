
package com.agenda.personal.dto;

public class ParticipanteEventoRespuestaDTO {

    private Long id;
    private Long eventoId;
    private Long contactoId;
    private Long usuarioId;

    public ParticipanteEventoRespuestaDTO() {
    }

    public ParticipanteEventoRespuestaDTO(
            Long id,
            Long eventoId,
            Long contactoId,
            Long usuarioId) {

        this.id = id;
        this.eventoId = eventoId;
        this.contactoId = contactoId;
        this.usuarioId = usuarioId;
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

    public Long getContactoId() {
        return contactoId;
    }

    public void setContactoId(Long contactoId) {
        this.contactoId = contactoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
