
package com.agenda.personal.dto;

public class ContactoCategoriaRespuestaDTO {

    private Long contactoId;
    private Long categoriaId;

    public ContactoCategoriaRespuestaDTO() {
    }

    public ContactoCategoriaRespuestaDTO(
            Long contactoId,
            Long categoriaId) {

        this.contactoId = contactoId;
        this.categoriaId = categoriaId;
    }

    public Long getContactoId() {
        return contactoId;
    }

    public void setContactoId(Long contactoId) {
        this.contactoId = contactoId;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
}
