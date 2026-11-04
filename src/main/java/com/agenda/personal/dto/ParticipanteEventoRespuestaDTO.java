package com.agenda.personal.dto;

public class ParticipanteEventoRespuestaDTO {

    private Long id;
    private Long eventoId;
    private Long contactoId;
    private Long usuarioId;
    private String estadoInvitacion;

    public ParticipanteEventoRespuestaDTO() {
    }

    public ParticipanteEventoRespuestaDTO(
            Long id,
            Long eventoId,
            Long contactoId,
            Long usuarioId,
            String estadoInvitacion) {

        this.id = id;
        this.eventoId = eventoId;
        this.contactoId = contactoId;
        this.usuarioId = usuarioId;
        this.estadoInvitacion = estadoInvitacion;
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

    public String getEstadoInvitacion() {
        return estadoInvitacion;
    }

    public void setEstadoInvitacion(String estadoInvitacion) {
        this.estadoInvitacion = estadoInvitacion;
    }
}