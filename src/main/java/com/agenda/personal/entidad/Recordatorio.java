
package com.agenda.personal.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "recordatorio")
public class Recordatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @Column(name = "minutos_antes", nullable = false)
    private Integer minutosAntes;

    @Column(nullable = false, length = 20)
    private String tipo = "NOTIFICACION";

    @Column(nullable = false)
    private boolean activo = true;

    // Constructor vacío
    public Recordatorio() {
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
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
