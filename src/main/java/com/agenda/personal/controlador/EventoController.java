
package com.agenda.personal.controlador;

import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agenda.personal.dto.EventoDTO;
import com.agenda.personal.dto.EventoRespuestaDTO;
import com.agenda.personal.entidad.Evento;
import com.agenda.personal.seguridad.UsuarioAutenticadoService;
import com.agenda.personal.servicio.ServicioEvento;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final ServicioEvento servicioEvento;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public EventoController(
            ServicioEvento servicioEvento,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.servicioEvento = servicioEvento;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    // Obtener eventos dentro de un intervalo de fechas.
    @GetMapping
    public ResponseEntity<List<EventoRespuestaDTO>> obtenerEventos(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant fechaInicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant fechaFin,

            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        List<EventoRespuestaDTO> eventos =
                servicioEvento.obtenerEventos(
                        usuarioId, fechaInicio, fechaFin)
                        .stream()
                        .map(this::convertirARespuesta)
                        .toList();

        return ResponseEntity.ok(eventos);
    }

    // Obtener un evento por su ID.
    @GetMapping("/{id}")
    public ResponseEntity<EventoRespuestaDTO> obtenerEvento(
            @PathVariable Long id,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        return servicioEvento.buscarPorId(id, usuarioId)
                .map(this::convertirARespuesta)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Registrar un evento.
    @PostMapping
    public ResponseEntity<EventoRespuestaDTO> registrarEvento(
            @Valid @RequestBody EventoDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        Evento evento = convertirAEntidad(datos);

        Evento guardado = servicioEvento.guardar(evento, usuarioId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertirARespuesta(guardado));
    }

    // Actualizar un evento.
    @PutMapping("/{id}")
    public ResponseEntity<EventoRespuestaDTO> actualizarEvento(
            @PathVariable Long id,
            @Valid @RequestBody EventoDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        Evento evento = convertirAEntidad(datos);
        evento.setId(id);

        Evento actualizado = servicioEvento.guardar(evento, usuarioId);

        return ResponseEntity.ok(convertirARespuesta(actualizado));
    }

    // Eliminar un evento.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvento(
            @PathVariable Long id,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        boolean eliminado = servicioEvento.eliminar(id, usuarioId);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    // Convertir DTO a entidad.
    private Evento convertirAEntidad(EventoDTO datos) {

        Evento evento = new Evento();

        evento.setTitulo(datos.getTitulo().trim());
        evento.setDescripcion(datos.getDescripcion());
        evento.setFechaInicio(datos.getFechaInicio());
        evento.setFechaFin(datos.getFechaFin());
        evento.setUbicacion(datos.getUbicacion());
        evento.setTipo(datos.getTipo());
        evento.setEstado(datos.getEstado());
        evento.setTodoElDia(datos.isTodoElDia());

        return evento;
    }

    // Convertir entidad a DTO de respuesta.
    private EventoRespuestaDTO convertirARespuesta(Evento evento) {

        return new EventoRespuestaDTO(
                evento.getId(),
                evento.getTitulo(),
                evento.getDescripcion(),
                evento.getFechaInicio(),
                evento.getFechaFin(),
                evento.getUbicacion(),
                evento.getTipo().name(),
                evento.getEstado().name(),
                evento.isTodoElDia(),
                evento.getFechaCreacion(),
                evento.getFechaActualizacion()
        );
    }
}
