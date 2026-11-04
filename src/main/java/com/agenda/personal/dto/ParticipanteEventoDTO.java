package com.agenda.personal.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ParticipanteEventoDTO {

    @NotNull(message = "El ID del contacto es obligatorio")
    @Positive(message = "El ID del contacto debe ser positivo")
    private Long contactoId;

    public ParticipanteEventoDTO() {
    }

    public Long getContactoId() {
        return contactoId;
    }

    public void setContactoId(Long contactoId) {
        this.contactoId = contactoId;
    }
}