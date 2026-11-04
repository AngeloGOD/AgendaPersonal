package com.agenda.personal.dto;

import com.agenda.personal.enums.TipoOrganizacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OrganizacionDTO {

    @NotBlank(message = "El nombre de la organización es obligatorio")
    @Size(max = 180, message = "El nombre no puede superar los 180 caracteres")
    private String nombre;

    @NotNull(message = "El tipo de organización es obligatorio")
    private TipoOrganizacion tipo = TipoOrganizacion.EMPRESA;

    @Size(max = 255, message = "El sitio web no puede superar los 255 caracteres")
    private String sitioWeb;

    @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres")
    private String telefonoGeneral;

    private String descripcion;

    public OrganizacionDTO() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoOrganizacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoOrganizacion tipo) {
        this.tipo = tipo;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }

    public String getTelefonoGeneral() {
        return telefonoGeneral;
    }

    public void setTelefonoGeneral(String telefonoGeneral) {
        this.telefonoGeneral = telefonoGeneral;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}