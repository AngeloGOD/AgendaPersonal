
package com.agenda.personal.dto;

public class CorreoContactoRespuestaDTO {

    private Long id;
    private Long contactoId;
    private String correo;
    private String tipo;
    private boolean esPrincipal;

    public CorreoContactoRespuestaDTO() {
    }

    public CorreoContactoRespuestaDTO(
            Long id,
            Long contactoId,
            String correo,
            String tipo,
            boolean esPrincipal) {

        this.id = id;
        this.contactoId = contactoId;
        this.correo = correo;
        this.tipo = tipo;
        this.esPrincipal = esPrincipal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContactoId() {
        return contactoId;
    }

    public void setContactoId(Long contactoId) {
        this.contactoId = contactoId;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }
}
