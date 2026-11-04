package com.agenda.personal.dto;

import com.agenda.personal.enums.TipoDireccion;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DireccionContactoDTO {

    @Size(max = 150, message = "La calle no puede superar los 150 caracteres")
    private String calle;

    @Size(max = 20, message = "El número exterior no puede superar los 20 caracteres")
    private String numeroExterior;

    @Size(max = 20, message = "El número interior no puede superar los 20 caracteres")
    private String numeroInterior;

    @Size(max = 100, message = "La colonia no puede superar los 100 caracteres")
    private String colonia;

    @Size(max = 100, message = "La ciudad no puede superar los 100 caracteres")
    private String ciudad;

    @Size(max = 100, message = "El estado no puede superar los 100 caracteres")
    private String estado;

    @Size(max = 15, message = "El código postal no puede superar los 15 caracteres")
    private String codigoPostal;

    @Size(max = 100, message = "El país no puede superar los 100 caracteres")
    private String pais;

    @NotNull(message = "El tipo de dirección es obligatorio")
    private TipoDireccion tipo = TipoDireccion.CASA;

    private boolean esPrincipal = false;

    public DireccionContactoDTO() {
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumeroExterior() {
        return numeroExterior;
    }

    public void setNumeroExterior(String numeroExterior) {
        this.numeroExterior = numeroExterior;
    }

    public String getNumeroInterior() {
        return numeroInterior;
    }

    public void setNumeroInterior(String numeroInterior) {
        this.numeroInterior = numeroInterior;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public TipoDireccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoDireccion tipo) {
        this.tipo = tipo;
    }

    public boolean isEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }
}