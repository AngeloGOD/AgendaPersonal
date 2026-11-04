package com.agenda.personal.controlador;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agenda.personal.dto.AsignarOrganizacionContactoDTO;
import com.agenda.personal.dto.ContactoOrganizacionRespuestaDTO;
import com.agenda.personal.entidad.ContactoOrganizacion;
import com.agenda.personal.seguridad.UsuarioAutenticadoService;
import com.agenda.personal.servicio.ServicioContactoOrganizacion;

@RestController
@RequestMapping("/api/contactos/{contactoId}/organizaciones")
public class ContactoOrganizacionController {

    private final ServicioContactoOrganizacion servicioContactoOrganizacion;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public ContactoOrganizacionController(
            ServicioContactoOrganizacion servicioContactoOrganizacion,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.servicioContactoOrganizacion = servicioContactoOrganizacion;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    // Obtener las organizaciones asignadas a un contacto.
    @GetMapping
    public ResponseEntity<List<ContactoOrganizacionRespuestaDTO>> obtenerOrganizaciones(
            @PathVariable Long contactoId,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        List<ContactoOrganizacionRespuestaDTO> organizaciones =
                servicioContactoOrganizacion
                        .obtenerOrganizacionesDeContacto(contactoId, usuarioId)
                        .stream()
                        .map(this::convertirARespuesta)
                        .toList();

        return ResponseEntity.ok(organizaciones);
    }

    // Asignar una organización a un contacto.
    @PostMapping
    public ResponseEntity<ContactoOrganizacionRespuestaDTO> asignarOrganizacion(
            @PathVariable Long contactoId,
            @Valid @RequestBody AsignarOrganizacionContactoDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        ContactoOrganizacion relacion =
                servicioContactoOrganizacion.asignarOrganizacion(
                        contactoId,
                        datos.getOrganizacionId(),
                        usuarioId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertirARespuesta(relacion));
    }

    // Quitar una organización de un contacto.
    @DeleteMapping("/{organizacionId}")
    public ResponseEntity<Void> quitarOrganizacion(
            @PathVariable Long contactoId,
            @PathVariable Long organizacionId,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        boolean eliminada =
                servicioContactoOrganizacion.quitarOrganizacion(
                        contactoId, organizacionId, usuarioId);

        if (!eliminada) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    // Convertir entidad a DTO de respuesta.
    private ContactoOrganizacionRespuestaDTO convertirARespuesta(
            ContactoOrganizacion relacion) {

        return new ContactoOrganizacionRespuestaDTO(
                relacion.getId(),
                relacion.getContactoId(),
                relacion.getOrganizacionId()
        );
    }
}