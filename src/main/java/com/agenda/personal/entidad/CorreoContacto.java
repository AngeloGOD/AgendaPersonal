package com.agenda.personal.entidad;

import com.agenda.personal.enums.TipoCorreo;

import jakarta.persistence.*;

@Entity
@Table(name = "correo_contacto")
public class CorreoContacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contacto_id", nullable = false)
    private Contacto contacto;

    @Column(nullable = false, length = 254)
    private String correo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoCorreo tipo = TipoCorreo.PERSONAL;

    @Column(name = "es_principal", nullable = false)
    private boolean esPrincipal = false;

    public CorreoContacto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public TipoCorreo getTipo() {
        return tipo;
    }

    public void setTipo(TipoCorreo tipo) {
        this.tipo = tipo;
    }

    public boolean isEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }
}