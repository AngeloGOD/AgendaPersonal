
package com.agenda.personal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CategoriaContactoDTO {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
    private String nombre;

    @Pattern(
        regexp = "^#[0-9A-Fa-f]{6}$",
        message = "El color debe tener un formato hexadecimal válido, por ejemplo #64748B"
    )
    private String color = "#64748B";

    @Size(max = 250, message = "La descripción no puede superar los 250 caracteres")
    private String descripcion;

    public CategoriaContactoDTO() {
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
