
package com.agenda.personal.dto;

public class ContactoOrganizacionRespuestaDTO {

    private Long id;
    private Long contactoId;
    private Long organizacionId;

    public ContactoOrganizacionRespuestaDTO() {
    }

    public ContactoOrganizacionRespuestaDTO(
            Long id,
            Long contactoId,
            Long organizacionId) {

        this.id = id;
        this.contactoId = contactoId;
        this.organizacionId = organizacionId;
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

    public Long getOrganizacionId() {
        return organizacionId;
    }

    public void setOrganizacionId(Long organizacionId) {
        this.organizacionId = organizacionId;
    }
}
