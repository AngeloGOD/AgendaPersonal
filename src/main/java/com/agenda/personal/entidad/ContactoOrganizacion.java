
package com.agenda.personal.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "contacto_organizacion")
public class ContactoOrganizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contacto_id", nullable = false)
    private Long contactoId;

    @Column(name = "organizacion_id", nullable = false)
    private Long organizacionId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    // Constructor vacío
    public ContactoOrganizacion() {
    }

    // Getters y setters
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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
