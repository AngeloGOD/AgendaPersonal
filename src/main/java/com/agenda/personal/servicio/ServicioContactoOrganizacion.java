
package com.agenda.personal.servicio;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.personal.entidad.ContactoOrganizacion;
import com.agenda.personal.repositorio.ContactoOrganizacionRepository;
import com.agenda.personal.repositorio.ContactoRepository;
import com.agenda.personal.repositorio.OrganizacionRepository;

@Service
@Transactional
public class ServicioContactoOrganizacion {

    private final ContactoOrganizacionRepository relacionRepository;
    private final ContactoRepository contactoRepository;
    private final OrganizacionRepository organizacionRepository;

    public ServicioContactoOrganizacion(
            ContactoOrganizacionRepository relacionRepository,
            ContactoRepository contactoRepository,
            OrganizacionRepository organizacionRepository) {

        this.relacionRepository = relacionRepository;
        this.contactoRepository = contactoRepository;
        this.organizacionRepository = organizacionRepository;
    }

    @Transactional(readOnly = true)
    public List<ContactoOrganizacion> obtenerOrganizacionesDeContacto(
            Long contactoId, Long usuarioId) {

        verificarContacto(contactoId, usuarioId);

        return relacionRepository
                .findAllByUsuarioIdAndContactoId(usuarioId, contactoId);
    }

    @Transactional(readOnly = true)
    public List<ContactoOrganizacion> obtenerContactosDeOrganizacion(
            Long organizacionId, Long usuarioId) {

        verificarOrganizacion(organizacionId, usuarioId);

        return relacionRepository
                .findAllByUsuarioIdAndOrganizacionId(usuarioId, organizacionId);
    }

    public ContactoOrganizacion asignarOrganizacion(
            Long contactoId, Long organizacionId, Long usuarioId) {

        verificarContacto(contactoId, usuarioId);
        verificarOrganizacion(organizacionId, usuarioId);

        boolean existe = relacionRepository
                .findAllByUsuarioIdAndContactoId(usuarioId, contactoId)
                .stream()
                .anyMatch(relacion ->
                        Objects.equals(
                                relacion.getOrganizacionId(), organizacionId));

        if (existe) {
            throw new IllegalArgumentException(
                    "La organización ya está asignada a este contacto.");
        }

        ContactoOrganizacion relacion = new ContactoOrganizacion();

        relacion.setContactoId(contactoId);
        relacion.setOrganizacionId(organizacionId);
        relacion.setUsuarioId(usuarioId);

        return relacionRepository.save(relacion);
    }

    public boolean quitarOrganizacion(
            Long contactoId, Long organizacionId, Long usuarioId) {

        verificarContacto(contactoId, usuarioId);
        verificarOrganizacion(organizacionId, usuarioId);

        return relacionRepository
                .findAllByUsuarioIdAndContactoId(usuarioId, contactoId)
                .stream()
                .filter(relacion ->
                        Objects.equals(
                                relacion.getOrganizacionId(), organizacionId))
                .findFirst()
                .map(relacion -> {
                    relacionRepository.delete(relacion);
                    return true;
                })
                .orElse(false);
    }

    private void verificarContacto(Long contactoId, Long usuarioId) {

        if (contactoId == null || usuarioId == null) {
            throw new IllegalArgumentException(
                    "El contacto y el usuario son obligatorios.");
        }

        if (contactoRepository
                .findByIdAndUsuario_Id(contactoId, usuarioId)
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "El contacto no existe o no pertenece al usuario.");
        }
    }

    private void verificarOrganizacion(
            Long organizacionId, Long usuarioId) {

        if (organizacionId == null || usuarioId == null) {
            throw new IllegalArgumentException(
                    "La organización y el usuario son obligatorios.");
        }

        if (organizacionRepository
                .findByIdAndUsuario_Id(organizacionId, usuarioId)
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "La organización no existe o no pertenece al usuario.");
        }
    }
}
