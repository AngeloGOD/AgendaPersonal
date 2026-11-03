
package com.agenda.personal.dto;

import java.time.Instant;
import java.time.LocalDate;

public class ContactoRespuestaDTO {

    private Long id;
    private String nombre;
    private String apellidos;
    private String apodo;
    private LocalDate fechaNacimiento;
    private String notas;
    private boolean favorito;
    private Instant fechaCreacion;
    private Instant fechaActualizacion;

    public ContactoRespuestaDTO() {
    }

    public ContactoRespuestaDTO(
            Long id,
            String nombre,
            String apellidos,
            String apodo,
            LocalDate fechaNacimiento,
            String notas,
            boolean favorito,
            Instant fechaCreacion,
            Instant fechaActualizacion) {

        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.apodo = apodo;
        this.fechaNacimiento = fechaNacimiento;
        this.notas = notas;
        this.favorito = favorito;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Instant getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Instant fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
