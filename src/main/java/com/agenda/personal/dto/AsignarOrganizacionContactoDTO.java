
package com.agenda.personal.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AsignarOrganizacionContactoDTO {

    @NotNull(message = "El ID de la organización es obligatorio")
    @Positive(message = "El ID de la organización debe ser positivo")
    private Long organizacionId;

    public AsignarOrganizacionContactoDTO() {
    }

    public Long getOrganizacionId() {
        return organizacionId;
    }

    public void setOrganizacionId(Long organizacionId) {
        this.organizacionId = organizacionId;
    }
}
