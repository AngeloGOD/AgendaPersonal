
package com.agenda.personal.dto;

public class TelefonoContactoRespuestaDTO {

    private Long id;
    private Long contactoId;
    private String numero;
    private String codigoPais;
    private String tipo;
    private boolean esPrincipal;

    public TelefonoContactoRespuestaDTO() {
    }

    public TelefonoContactoRespuestaDTO(
            Long id,
            Long contactoId,
            String numero,
            String codigoPais,
            String tipo,
            boolean esPrincipal) {

        this.id = id;
        this.contactoId = contactoId;
        this.numero = numero;
        this.codigoPais = codigoPais;
        this.tipo = tipo;
        this.esPrincipal = esPrincipal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContactoId() {
        return contactoId;
    }

    public void setContactoId(Long contactoId) {
        this.contactoId = contactoId;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }
}
