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

import com.agenda.personal.dto.TelefonoContactoDTO;
import com.agenda.personal.dto.TelefonoContactoRespuestaDTO;
import com.agenda.personal.entidad.TelefonoContacto;
import com.agenda.personal.seguridad.UsuarioAutenticadoService;
import com.agenda.personal.servicio.ServicioTelefonoContacto;

@RestController
@RequestMapping("/api/contactos/{contactoId}/telefonos")
public class TelefonoContactoController {

    private final ServicioTelefonoContacto servicioTelefono;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public TelefonoContactoController(
            ServicioTelefonoContacto servicioTelefono,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.servicioTelefono = servicioTelefono;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    // Obtener los teléfonos de un contacto.
    @GetMapping
    public ResponseEntity<List<TelefonoContactoRespuestaDTO>> obtenerTelefonos(
            @PathVariable Long contactoId,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        List<TelefonoContactoRespuestaDTO> telefonos =
                servicioTelefono.obtenerTelefonos(contactoId, usuarioId)
                        .stream()
                        .map(telefono -> convertirARespuesta(telefono, contactoId))
                        .toList();

        return ResponseEntity.ok(telefonos);
    }

    // Registrar un teléfono.
    @PostMapping
    public ResponseEntity<TelefonoContactoRespuestaDTO> registrarTelefono(
            @PathVariable Long contactoId,
            @Valid @RequestBody TelefonoContactoDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        TelefonoContacto telefono = convertirAEntidad(datos);

        TelefonoContacto guardado =
                servicioTelefono.guardar(telefono, contactoId, usuarioId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertirARespuesta(guardado, contactoId));
    }

    // Actualizar un teléfono.
    @PutMapping("/{telefonoId}")
    public ResponseEntity<TelefonoContactoRespuestaDTO> actualizarTelefono(
            @PathVariable Long contactoId,
            @PathVariable Long telefonoId,
            @Valid @RequestBody TelefonoContactoDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        TelefonoContacto telefono = convertirAEntidad(datos);
        telefono.setId(telefonoId);

        TelefonoContacto actualizado =
                servicioTelefono.guardar(telefono, contactoId, usuarioId);

        return ResponseEntity.ok(
                convertirARespuesta(actualizado, contactoId));
    }

    // Eliminar un teléfono.
    @DeleteMapping("/{telefonoId}")
    public ResponseEntity<Void> eliminarTelefono(
            @PathVariable Long contactoId,
            @PathVariable Long telefonoId,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        boolean eliminado = servicioTelefono.eliminar(
                telefonoId, contactoId, usuarioId);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    // Convertir DTO a entidad.
    private TelefonoContacto convertirAEntidad(
            TelefonoContactoDTO datos) {

        TelefonoContacto telefono = new TelefonoContacto();

        telefono.setNumero(datos.getNumero().trim());
        telefono.setCodigoPais(datos.getCodigoPais());
        telefono.setTipo(datos.getTipo());
        telefono.setEsPrincipal(datos.isEsPrincipal());

        return telefono;
    }

    // Convertir entidad a DTO de respuesta.
    private TelefonoContactoRespuestaDTO convertirARespuesta(
            TelefonoContacto telefono, Long contactoId) {

        return new TelefonoContactoRespuestaDTO(
                telefono.getId(),
                contactoId,
                telefono.getNumero(),
                telefono.getCodigoPais(),
                telefono.getTipo().name(),
                telefono.isEsPrincipal()
        );
    }
}