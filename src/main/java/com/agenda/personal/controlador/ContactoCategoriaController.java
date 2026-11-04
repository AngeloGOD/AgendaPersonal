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

import com.agenda.personal.dto.AsignarCategoriaContactoDTO;
import com.agenda.personal.dto.ContactoCategoriaRespuestaDTO;
import com.agenda.personal.entidad.ContactoCategoria;
import com.agenda.personal.seguridad.UsuarioAutenticadoService;
import com.agenda.personal.servicio.ServicioContactoCategoria;

@RestController
@RequestMapping("/api/contactos/{contactoId}/categorias")
public class ContactoCategoriaController {

    private final ServicioContactoCategoria servicioContactoCategoria;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public ContactoCategoriaController(
            ServicioContactoCategoria servicioContactoCategoria,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.servicioContactoCategoria = servicioContactoCategoria;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    // Obtener las categorías asignadas a un contacto.
    @GetMapping
    public ResponseEntity<List<ContactoCategoriaRespuestaDTO>> obtenerCategorias(
            @PathVariable Long contactoId,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        List<ContactoCategoriaRespuestaDTO> categorias =
                servicioContactoCategoria
                        .obtenerCategoriasDeContacto(contactoId, usuarioId)
                        .stream()
                        .map(this::convertirARespuesta)
                        .toList();

        return ResponseEntity.ok(categorias);
    }

    // Asignar una categoría a un contacto.
    @PostMapping
    public ResponseEntity<ContactoCategoriaRespuestaDTO> asignarCategoria(
            @PathVariable Long contactoId,
            @Valid @RequestBody AsignarCategoriaContactoDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        ContactoCategoria relacion =
                servicioContactoCategoria.asignarCategoria(
                        contactoId,
                        datos.getCategoriaId(),
                        usuarioId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertirARespuesta(relacion));
    }

    // Quitar una categoría de un contacto.
    @DeleteMapping("/{categoriaId}")
    public ResponseEntity<Void> quitarCategoria(
            @PathVariable Long contactoId,
            @PathVariable Long categoriaId,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        boolean eliminada =
                servicioContactoCategoria.quitarCategoria(
                        contactoId, categoriaId, usuarioId);

        if (!eliminada) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    // Convertir entidad a DTO de respuesta.
    private ContactoCategoriaRespuestaDTO convertirARespuesta(
            ContactoCategoria relacion) {

        return new ContactoCategoriaRespuestaDTO(
                relacion.getId().getContactoId(),
                relacion.getId().getCategoriaId()
        );
    }
}