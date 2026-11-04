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

import com.agenda.personal.dto.CorreoContactoDTO;
import com.agenda.personal.dto.CorreoContactoRespuestaDTO;
import com.agenda.personal.entidad.CorreoContacto;
import com.agenda.personal.seguridad.UsuarioAutenticadoService;
import com.agenda.personal.servicio.ServicioCorreoContacto;

@RestController
@RequestMapping("/api/contactos/{contactoId}/correos")
public class CorreoContactoController {

    private final ServicioCorreoContacto servicioCorreo;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public CorreoContactoController(
            ServicioCorreoContacto servicioCorreo,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.servicioCorreo = servicioCorreo;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    // Obtener los correos de un contacto.
    @GetMapping
    public ResponseEntity<List<CorreoContactoRespuestaDTO>> obtenerCorreos(
            @PathVariable Long contactoId,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        List<CorreoContactoRespuestaDTO> correos =
                servicioCorreo.obtenerCorreos(contactoId, usuarioId)
                        .stream()
                        .map(correo -> convertirARespuesta(correo, contactoId))
                        .toList();

        return ResponseEntity.ok(correos);
    }

    // Registrar un correo.
    @PostMapping
    public ResponseEntity<CorreoContactoRespuestaDTO> registrarCorreo(
            @PathVariable Long contactoId,
            @Valid @RequestBody CorreoContactoDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        CorreoContacto correo = convertirAEntidad(datos);

        CorreoContacto guardado =
                servicioCorreo.guardar(correo, contactoId, usuarioId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertirARespuesta(guardado, contactoId));
    }

    // Actualizar un correo.
    @PutMapping("/{correoId}")
    public ResponseEntity<CorreoContactoRespuestaDTO> actualizarCorreo(
            @PathVariable Long contactoId,
            @PathVariable Long correoId,
            @Valid @RequestBody CorreoContactoDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        CorreoContacto correo = convertirAEntidad(datos);
        correo.setId(correoId);

        CorreoContacto actualizado =
                servicioCorreo.guardar(correo, contactoId, usuarioId);

        return ResponseEntity.ok(
                convertirARespuesta(actualizado, contactoId));
    }

    // Eliminar un correo.
    @DeleteMapping("/{correoId}")
    public ResponseEntity<Void> eliminarCorreo(
            @PathVariable Long contactoId,
            @PathVariable Long correoId,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        boolean eliminado = servicioCorreo.eliminar(
                correoId, contactoId, usuarioId);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    // Convertir DTO a entidad.
    private CorreoContacto convertirAEntidad(
            CorreoContactoDTO datos) {

        CorreoContacto correo = new CorreoContacto();

        correo.setCorreo(datos.getCorreo().trim());
        correo.setTipo(datos.getTipo());
        correo.setEsPrincipal(datos.isEsPrincipal());

        return correo;
    }

    // Convertir entidad a DTO de respuesta.
    private CorreoContactoRespuestaDTO convertirARespuesta(
            CorreoContacto correo, Long contactoId) {

        return new CorreoContactoRespuestaDTO(
                correo.getId(),
                contactoId,
                correo.getCorreo(),
                correo.getTipo().name(),
                correo.isEsPrincipal()
        );
    }
}