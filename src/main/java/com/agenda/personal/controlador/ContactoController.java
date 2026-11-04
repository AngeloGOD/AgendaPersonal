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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agenda.personal.dto.ContactoDTO;
import com.agenda.personal.dto.ContactoRespuestaDTO;
import com.agenda.personal.entidad.Contacto;
import com.agenda.personal.seguridad.UsuarioAutenticadoService;
import com.agenda.personal.servicio.ServicioContacto;

@RestController
@RequestMapping("/api/contactos")
public class ContactoController {

    private final ServicioContacto servicioContacto;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public ContactoController(
            ServicioContacto servicioContacto,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.servicioContacto = servicioContacto;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    // Obtener todos los contactos del usuario autenticado.
    @GetMapping
    public ResponseEntity<List<ContactoRespuestaDTO>> obtenerContactos(
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        List<ContactoRespuestaDTO> contactos =
                servicioContacto.obtenerContactos(usuarioId)
                        .stream()
                        .map(this::convertirARespuesta)
                        .toList();

        return ResponseEntity.ok(contactos);
    }

    // Obtener un contacto específico.
    @GetMapping("/{id}")
    public ResponseEntity<ContactoRespuestaDTO> obtenerContacto(
            @PathVariable Long id,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        return servicioContacto.buscarPorId(id, usuarioId)
                .map(this::convertirARespuesta)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Registrar un contacto nuevo.
    @PostMapping
    public ResponseEntity<ContactoRespuestaDTO> registrarContacto(
            @Valid @RequestBody ContactoDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        Contacto contacto = convertirAEntidad(datos);

        Contacto contactoGuardado =
                servicioContacto.guardar(contacto, usuarioId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertirARespuesta(contactoGuardado));
    }

    // Actualizar un contacto existente.
    @PutMapping("/{id}")
    public ResponseEntity<ContactoRespuestaDTO> actualizarContacto(
            @PathVariable Long id,
            @Valid @RequestBody ContactoDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        Contacto contacto = convertirAEntidad(datos);
        contacto.setId(id);

        Contacto contactoActualizado =
                servicioContacto.guardar(contacto, usuarioId);

        return ResponseEntity.ok(
                convertirARespuesta(contactoActualizado));
    }

    // Eliminar un contacto.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarContacto(
            @PathVariable Long id,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        boolean eliminado =
                servicioContacto.eliminar(id, usuarioId);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    // Convertir DTO de entrada a entidad.
    private Contacto convertirAEntidad(ContactoDTO datos) {

        Contacto contacto = new Contacto();

        contacto.setNombre(datos.getNombre().trim());
        contacto.setApellidos(datos.getApellidos());
        contacto.setApodo(datos.getApodo());
        contacto.setFechaNacimiento(datos.getFechaNacimiento());
        contacto.setNotas(datos.getNotas());
        contacto.setFavorito(datos.isFavorito());

        return contacto;
    }

    // Convertir entidad a DTO de respuesta.
    private ContactoRespuestaDTO convertirARespuesta(Contacto contacto) {

        return new ContactoRespuestaDTO(
                contacto.getId(),
                contacto.getNombre(),
                contacto.getApellidos(),
                contacto.getApodo(),
                contacto.getFechaNacimiento(),
                contacto.getNotas(),
                contacto.isFavorito(),
                contacto.getFechaCreacion(),
                contacto.getFechaActualizacion()
        );
    }
}