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

import com.agenda.personal.dto.OrganizacionDTO;
import com.agenda.personal.dto.OrganizacionRespuestaDTO;
import com.agenda.personal.entidad.Organizacion;
import com.agenda.personal.seguridad.UsuarioAutenticadoService;
import com.agenda.personal.servicio.ServicioOrganizacion;

@RestController
@RequestMapping("/api/organizaciones")
public class OrganizacionController {

    private final ServicioOrganizacion servicioOrganizacion;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public OrganizacionController(
            ServicioOrganizacion servicioOrganizacion,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.servicioOrganizacion = servicioOrganizacion;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    // Obtener todas las organizaciones del usuario.
    @GetMapping
    public ResponseEntity<List<OrganizacionRespuestaDTO>> obtenerOrganizaciones(
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        List<OrganizacionRespuestaDTO> organizaciones =
                servicioOrganizacion.obtenerOrganizaciones(usuarioId)
                        .stream()
                        .map(this::convertirARespuesta)
                        .toList();

        return ResponseEntity.ok(organizaciones);
    }

    // Obtener una organización por su ID.
    @GetMapping("/{id}")
    public ResponseEntity<OrganizacionRespuestaDTO> obtenerOrganizacion(
            @PathVariable Long id,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        return servicioOrganizacion.buscarPorId(id, usuarioId)
                .map(this::convertirARespuesta)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Registrar una organización.
    @PostMapping
    public ResponseEntity<OrganizacionRespuestaDTO> registrarOrganizacion(
            @Valid @RequestBody OrganizacionDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        Organizacion organizacion = convertirAEntidad(datos);

        Organizacion guardada =
                servicioOrganizacion.guardar(organizacion, usuarioId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertirARespuesta(guardada));
    }

    // Actualizar una organización.
    @PutMapping("/{id}")
    public ResponseEntity<OrganizacionRespuestaDTO> actualizarOrganizacion(
            @PathVariable Long id,
            @Valid @RequestBody OrganizacionDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        Organizacion organizacion = convertirAEntidad(datos);
        organizacion.setId(id);

        Organizacion actualizada =
                servicioOrganizacion.guardar(organizacion, usuarioId);

        return ResponseEntity.ok(convertirARespuesta(actualizada));
    }

    // Eliminar una organización.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOrganizacion(
            @PathVariable Long id,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        boolean eliminada =
                servicioOrganizacion.eliminar(id, usuarioId);

        if (!eliminada) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    // Convertir DTO a entidad.
    private Organizacion convertirAEntidad(OrganizacionDTO datos) {

        Organizacion organizacion = new Organizacion();

        organizacion.setNombre(datos.getNombre().trim());
        organizacion.setTipo(datos.getTipo());
        organizacion.setSitioWeb(datos.getSitioWeb());
        organizacion.setTelefonoGeneral(datos.getTelefonoGeneral());
        organizacion.setDescripcion(datos.getDescripcion());

        return organizacion;
    }

    // Convertir entidad a DTO de respuesta.
    private OrganizacionRespuestaDTO convertirARespuesta(
            Organizacion organizacion) {

        return new OrganizacionRespuestaDTO(
                organizacion.getId(),
                organizacion.getNombre(),
                organizacion.getTipo().name(),
                organizacion.getSitioWeb(),
                organizacion.getTelefonoGeneral(),
                organizacion.getDescripcion()
        );
    }
}