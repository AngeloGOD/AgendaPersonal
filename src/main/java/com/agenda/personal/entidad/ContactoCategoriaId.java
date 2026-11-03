
package com.agenda.personal.entidad;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class ContactoCategoriaId implements Serializable {
	private static final long serialVersionUID = 1L;

    private Long contactoId;

    private Long categoriaId;

    public ContactoCategoriaId() {
    }

    public ContactoCategoriaId(Long contactoId, Long categoriaId) {
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

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }

        if (!(objeto instanceof ContactoCategoriaId)) {
            return false;
        }

        ContactoCategoriaId otro = (ContactoCategoriaId) objeto;

        return Objects.equals(contactoId, otro.contactoId)
                && Objects.equals(categoriaId, otro.categoriaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactoId, categoriaId);
    }
}
