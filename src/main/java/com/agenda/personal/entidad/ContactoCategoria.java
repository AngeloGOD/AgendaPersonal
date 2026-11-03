
package com.agenda.personal.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "contacto_categoria")
public class ContactoCategoria {

    @EmbeddedId
    private ContactoCategoriaId id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    // Constructor vacío
    public ContactoCategoria() {
    }

    public ContactoCategoria(ContactoCategoriaId id, Long usuarioId) {
        this.id = id;
        this.usuarioId = usuarioId;
    }

    // Getters y setters
    public ContactoCategoriaId getId() {
        return id;
    }

    public void setId(ContactoCategoriaId id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
