package com.agenda.personal.dto;

import com.agenda.personal.enums.TipoCorreo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CorreoContactoDTO {

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no tiene un formato válido")
    @Size(max = 254, message = "El correo no puede superar los 254 caracteres")
    private String correo;

    @NotNull(message = "El tipo de correo es obligatorio")
    private TipoCorreo tipo = TipoCorreo.PERSONAL;

    private boolean esPrincipal = false;

    public CorreoContactoDTO() {
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public TipoCorreo getTipo() {
        return tipo;
    }

    public void setTipo(TipoCorreo tipo) {
        this.tipo = tipo;
    }

    public boolean isEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }
}