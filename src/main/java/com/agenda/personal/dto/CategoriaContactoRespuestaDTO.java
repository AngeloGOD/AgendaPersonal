
package com.agenda.personal.dto;

public class CategoriaContactoRespuestaDTO {

    private Long id;
    private String nombre;
    private String color;
    private String descripcion;

    public CategoriaContactoRespuestaDTO() {
    }

    public CategoriaContactoRespuestaDTO(
            Long id,
            String nombre,
            String color,
            String descripcion) {

        this.id = id;
        this.nombre = nombre;
        this.color = color;
        this.descripcion = descripcion;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
