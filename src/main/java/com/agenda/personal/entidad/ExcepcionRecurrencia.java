package com.agenda.personal.entidad;

import java.time.Instant;

import com.agenda.personal.enums.TipoExcepcion;

import jakarta.persistence.*;

@Entity
@Table(name = "excepcion_recurrencia")
public class ExcepcionRecurrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @Column(name = "fecha_original", nullable = false)
    private Instant fechaOriginal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoExcepcion tipo;

    @Column(name = "nueva_fecha_inicio")
    private Instant nuevaFechaInicio;

    @Column(name = "nueva_fecha_fin")
    private Instant nuevaFechaFin;

    @Column(name = "nueva_ubicacion", length = 255)
    private String nuevaUbicacion;

    public ExcepcionRecurrencia() {
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

    public Instant getFechaOriginal() {
        return fechaOriginal;
    }

    public void setFechaOriginal(Instant fechaOriginal) {
        this.fechaOriginal = fechaOriginal;
    }

    public TipoExcepcion getTipo() {
        return tipo;
    }

    public void setTipo(TipoExcepcion tipo) {
        this.tipo = tipo;
    }

    public Instant getNuevaFechaInicio() {
        return nuevaFechaInicio;
    }

    public void setNuevaFechaInicio(Instant nuevaFechaInicio) {
        this.nuevaFechaInicio = nuevaFechaInicio;
    }

    public Instant getNuevaFechaFin() {
        return nuevaFechaFin;
    }

    public void setNuevaFechaFin(Instant nuevaFechaFin) {
        this.nuevaFechaFin = nuevaFechaFin;
    }

    public String getNuevaUbicacion() {
        return nuevaUbicacion;
    }

    public void setNuevaUbicacion(String nuevaUbicacion) {
        this.nuevaUbicacion = nuevaUbicacion;
    }
}