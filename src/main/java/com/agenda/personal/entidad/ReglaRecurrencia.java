package com.agenda.personal.entidad;

import java.time.LocalDate;

import com.agenda.personal.enums.FrecuenciaRecurrencia;

import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "regla_recurrencia")
public class ReglaRecurrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false, unique = true)
    private Evento evento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FrecuenciaRecurrencia frecuencia;

    @Column(nullable = false)
    private Integer intervalo = 1;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "dias_semana", columnDefinition = "smallint[]")
    private Short[] diasSemana;

    @Column(name = "dia_mes")
    private Integer diaMes;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "numero_ocurrencias")
    private Integer numeroOcurrencias;

    public ReglaRecurrencia() {
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

    public FrecuenciaRecurrencia getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(FrecuenciaRecurrencia frecuencia) {
        this.frecuencia = frecuencia;
    }

    public Integer getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Integer intervalo) {
        this.intervalo = intervalo;
    }

    public Short[] getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(Short[] diasSemana) {
        this.diasSemana = diasSemana;
    }

    public Integer getDiaMes() {
        return diaMes;
    }

    public void setDiaMes(Integer diaMes) {
        this.diaMes = diaMes;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getNumeroOcurrencias() {
        return numeroOcurrencias;
    }

    public void setNumeroOcurrencias(Integer numeroOcurrencias) {
        this.numeroOcurrencias = numeroOcurrencias;
    }
}