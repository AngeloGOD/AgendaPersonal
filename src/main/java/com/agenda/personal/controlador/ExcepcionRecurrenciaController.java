package com.agenda.personal.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.agenda.personal.dto.ExcepcionRecurrenciaDTO;
import com.agenda.personal.dto.ExcepcionRecurrenciaRespuestaDTO;
import com.agenda.personal.entidad.ExcepcionRecurrencia;
import com.agenda.personal.seguridad.UsuarioAutenticadoService;
import com.agenda.personal.servicio.ServicioExcepcionRecurrencia;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/eventos/{eventoId}/excepciones-recurrencia")
public class ExcepcionRecurrenciaController {

    private final ServicioExcepcionRecurrencia servicioExcepcion;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public ExcepcionRecurrenciaController(
            ServicioExcepcionRecurrencia servicioExcepcion,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.servicioExcepcion = servicioExcepcion;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @GetMapping
    public ResponseEntity<List<ExcepcionRecurrenciaRespuestaDTO>> obtenerExcepciones(
            @PathVariable Long eventoId,
            Authentication authentication) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(authentication);

        List<ExcepcionRecurrenciaRespuestaDTO> excepciones =
                servicioExcepcion.obtenerExcepciones(eventoId, usuarioId)
                        .stream()
                        .map(this::convertirARespuesta)
                        .toList();

        return ResponseEntity.ok(excepciones);
    }

    @PostMapping
    public ResponseEntity<ExcepcionRecurrenciaRespuestaDTO> registrarExcepcion(
            @PathVariable Long eventoId,
            @Valid @RequestBody ExcepcionRecurrenciaDTO datos,
            Authentication authentication) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(authentication);

        ExcepcionRecurrencia excepcion = convertirAEntidad(datos);

        ExcepcionRecurrencia guardada = servicioExcepcion.guardar(
                excepcion, eventoId, usuarioId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertirARespuesta(guardada));
    }

    @PutMapping("/{excepcionId}")
    public ResponseEntity<ExcepcionRecurrenciaRespuestaDTO> actualizarExcepcion(
            @PathVariable Long eventoId,
            @PathVariable Long excepcionId,
            @Valid @RequestBody ExcepcionRecurrenciaDTO datos,
            Authentication authentication) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(authentication);

        ExcepcionRecurrencia excepcion = convertirAEntidad(datos);
        excepcion.setId(excepcionId);

        ExcepcionRecurrencia actualizada = servicioExcepcion.guardar(
                excepcion, eventoId, usuarioId);

        return ResponseEntity.ok(convertirARespuesta(actualizada));
    }

    @DeleteMapping("/{excepcionId}")
    public ResponseEntity<Void> eliminarExcepcion(
            @PathVariable Long eventoId,
            @PathVariable Long excepcionId,
            Authentication authentication) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(authentication);

        boolean eliminada = servicioExcepcion.eliminar(
                excepcionId, eventoId, usuarioId);

        if (!eliminada) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    private ExcepcionRecurrencia convertirAEntidad(
            ExcepcionRecurrenciaDTO datos) {

        ExcepcionRecurrencia excepcion = new ExcepcionRecurrencia();

        excepcion.setFechaOriginal(datos.getFechaOriginal());
        excepcion.setTipo(datos.getTipo());
        excepcion.setNuevaFechaInicio(datos.getNuevaFechaInicio());
        excepcion.setNuevaFechaFin(datos.getNuevaFechaFin());
        excepcion.setNuevaUbicacion(datos.getNuevaUbicacion());

        return excepcion;
    }

    private ExcepcionRecurrenciaRespuestaDTO convertirARespuesta(
            ExcepcionRecurrencia excepcion) {

        return new ExcepcionRecurrenciaRespuestaDTO(
                excepcion.getId(),
                excepcion.getEvento().getId(),
                excepcion.getFechaOriginal(),
                excepcion.getTipo().name(),
                excepcion.getNuevaFechaInicio(),
                excepcion.getNuevaFechaFin(),
                excepcion.getNuevaUbicacion()
        );
    }
}