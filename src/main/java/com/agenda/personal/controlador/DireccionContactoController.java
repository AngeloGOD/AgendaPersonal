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

import com.agenda.personal.dto.DireccionContactoDTO;
import com.agenda.personal.dto.DireccionContactoRespuestaDTO;
import com.agenda.personal.entidad.DireccionContacto;
import com.agenda.personal.seguridad.UsuarioAutenticadoService;
import com.agenda.personal.servicio.ServicioDireccionContacto;

@RestController
@RequestMapping("/api/contactos/{contactoId}/direcciones")
public class DireccionContactoController {

    private final ServicioDireccionContacto servicioDireccion;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public DireccionContactoController(
            ServicioDireccionContacto servicioDireccion,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.servicioDireccion = servicioDireccion;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    // Obtener las direcciones de un contacto.
    @GetMapping
    public ResponseEntity<List<DireccionContactoRespuestaDTO>> obtenerDirecciones(
            @PathVariable Long contactoId,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        List<DireccionContactoRespuestaDTO> direcciones =
                servicioDireccion.obtenerDirecciones(contactoId, usuarioId)
                        .stream()
                        .map(this::convertirARespuesta)
                        .toList();

        return ResponseEntity.ok(direcciones);
    }

    // Registrar una dirección.
    @PostMapping
    public ResponseEntity<DireccionContactoRespuestaDTO> registrarDireccion(
            @PathVariable Long contactoId,
            @Valid @RequestBody DireccionContactoDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        DireccionContacto direccion = convertirAEntidad(datos);

        DireccionContacto guardada =
                servicioDireccion.guardar(direccion, contactoId, usuarioId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertirARespuesta(guardada));
    }

    // Actualizar una dirección.
    @PutMapping("/{direccionId}")
    public ResponseEntity<DireccionContactoRespuestaDTO> actualizarDireccion(
            @PathVariable Long contactoId,
            @PathVariable Long direccionId,
            @Valid @RequestBody DireccionContactoDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        DireccionContacto direccion = convertirAEntidad(datos);
        direccion.setId(direccionId);

        DireccionContacto actualizada =
                servicioDireccion.guardar(direccion, contactoId, usuarioId);

        return ResponseEntity.ok(convertirARespuesta(actualizada));
    }

    // Eliminar una dirección.
    @DeleteMapping("/{direccionId}")
    public ResponseEntity<Void> eliminarDireccion(
            @PathVariable Long contactoId,
            @PathVariable Long direccionId,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        boolean eliminada = servicioDireccion.eliminar(
                direccionId, contactoId, usuarioId);

        if (!eliminada) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    // Convertir DTO a entidad.
    private DireccionContacto convertirAEntidad(
            DireccionContactoDTO datos) {

        DireccionContacto direccion = new DireccionContacto();

        direccion.setCalle(datos.getCalle());
        direccion.setNumeroExterior(datos.getNumeroExterior());
        direccion.setNumeroInterior(datos.getNumeroInterior());
        direccion.setColonia(datos.getColonia());
        direccion.setCiudad(datos.getCiudad());
        direccion.setEstado(datos.getEstado());
        direccion.setCodigoPostal(datos.getCodigoPostal());
        direccion.setPais(datos.getPais());
        direccion.setTipo(datos.getTipo());
        direccion.setEsPrincipal(datos.isEsPrincipal());

        return direccion;
    }

    // Convertir entidad a DTO de respuesta.
    private DireccionContactoRespuestaDTO convertirARespuesta(
            DireccionContacto direccion) {

        return new DireccionContactoRespuestaDTO(
                direccion.getId(),
                direccion.getContacto().getId(),
                direccion.getCalle(),
                direccion.getNumeroExterior(),
                direccion.getNumeroInterior(),
                direccion.getColonia(),
                direccion.getCiudad(),
                direccion.getEstado(),
                direccion.getCodigoPostal(),
                direccion.getPais(),
                direccion.getTipo().name(),
                direccion.isEsPrincipal()
        );
    }
}