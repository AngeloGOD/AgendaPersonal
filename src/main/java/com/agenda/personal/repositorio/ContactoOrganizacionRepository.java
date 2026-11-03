package com.agenda.personal.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.personal.entidad.ContactoOrganizacion;

public interface ContactoOrganizacionRepository
        extends JpaRepository<ContactoOrganizacion, Long> {

    List<ContactoOrganizacion> findAllByUsuarioIdAndContactoId(
            Long usuarioId,
            Long contactoId
    );

    List<ContactoOrganizacion> findAllByUsuarioIdAndOrganizacionId(
            Long usuarioId,
            Long organizacionId
    );

}