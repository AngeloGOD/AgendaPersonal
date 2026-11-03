
package com.agenda.personal.dto;

import java.time.Instant;

public class UsuarioRespuestaDTO {

    private Long id;
    private String nombre;
    private String apellidos;
    private String correo;
    private String zonaHoraria;
    private boolean activo;
    private Instant fechaCreacion;

    public UsuarioRespuestaDTO() {
    }

    public UsuarioRespuestaDTO(
            Long id,
            String nombre,
            String apellidos,
            String correo,
            String zonaHoraria,
            boolean activo,
            Instant fechaCreacion) {

        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.zonaHoraria = zonaHoraria;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getZonaHoraria() {
        return zonaHoraria;
    }

    public void setZonaHoraria(String zonaHoraria) {
        this.zonaHoraria = zonaHoraria;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
