
package com.agenda.personal.dto;

public class OrganizacionRespuestaDTO {

    private Long id;
    private String nombre;
    private String tipo;
    private String sitioWeb;
    private String telefonoGeneral;
    private String descripcion;

    public OrganizacionRespuestaDTO() {
    }

    public OrganizacionRespuestaDTO(
            Long id,
            String nombre,
            String tipo,
            String sitioWeb,
            String telefonoGeneral,
            String descripcion) {

        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.sitioWeb = sitioWeb;
        this.telefonoGeneral = telefonoGeneral;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
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
