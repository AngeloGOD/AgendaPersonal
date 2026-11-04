package com.agenda.personal.dto;

import com.agenda.personal.enums.TipoTelefono;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TelefonoContactoDTO {

    @NotBlank(message = "El número de teléfono es obligatorio")
    @Size(max = 30, message = "El número no puede superar los 30 caracteres")
    private String numero;

    @Size(max = 5, message = "El código de país no puede superar los 5 caracteres")
    private String codigoPais;

    @NotNull(message = "El tipo de teléfono es obligatorio")
    private TipoTelefono tipo = TipoTelefono.MOVIL;

    private boolean esPrincipal = false;

    public TelefonoContactoDTO() {
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCodigoPais() {
        return codigoPais;
    }

    public void setCodigoPais(String codigoPais) {
        this.codigoPais = codigoPais;
    }

    public TipoTelefono getTipo() {
        return tipo;
    }

    public void setTipo(TipoTelefono tipo) {
        this.tipo = tipo;
    }

    public boolean isEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }
}