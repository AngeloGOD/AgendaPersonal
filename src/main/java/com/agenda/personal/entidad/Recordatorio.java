package com.agenda.personal.entidad;

import com.agenda.personal.enums.TipoRecordatorio;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoRecordatorio tipo = TipoRecordatorio.NOTIFICACION;

    @Column(nullable = false)
    private boolean activo = true;

    public Recordatorio() {
    }

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

    public TipoRecordatorio getTipo() {
        return tipo;
    }

    public void setTipo(TipoRecordatorio tipo) {
        this.tipo = tipo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}