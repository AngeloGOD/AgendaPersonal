package com.agenda.personal.controlador;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.agenda.personal.dto.ReglaRecurrenciaDTO;
import com.agenda.personal.dto.ReglaRecurrenciaRespuestaDTO;
import com.agenda.personal.entidad.ReglaRecurrencia;
import com.agenda.personal.seguridad.UsuarioAutenticadoService;
import com.agenda.personal.servicio.ServicioReglaRecurrencia;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/eventos/{eventoId}/recurrencia")
public class ReglaRecurrenciaController {

    private final ServicioReglaRecurrencia servicioRegla;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public ReglaRecurrenciaController(
            ServicioReglaRecurrencia servicioRegla,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.servicioRegla = servicioRegla;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @GetMapping
    public ResponseEntity<ReglaRecurrenciaRespuestaDTO> obtenerRegla(
            @PathVariable Long eventoId,
            Authentication authentication) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(authentication);

        return servicioRegla.buscarPorEvento(eventoId, usuarioId)
                .map(regla -> ResponseEntity.ok(convertirARespuesta(regla)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ReglaRecurrenciaRespuestaDTO> registrarRegla(
            @PathVariable Long eventoId,
            @Valid @RequestBody ReglaRecurrenciaDTO datos,
            Authentication authentication) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(authentication);

        ReglaRecurrencia regla = convertirAEntidad(datos);

        ReglaRecurrencia guardada = servicioRegla.guardar(
                regla, eventoId, usuarioId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertirARespuesta(guardada));
    }

    @PutMapping
    public ResponseEntity<ReglaRecurrenciaRespuestaDTO> actualizarRegla(
            @PathVariable Long eventoId,
            @Valid @RequestBody ReglaRecurrenciaDTO datos,
            Authentication authentication) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(authentication);

        ReglaRecurrencia regla = convertirAEntidad(datos);

        ReglaRecurrencia actualizada = servicioRegla.guardar(
                regla, eventoId, usuarioId);

        return ResponseEntity.ok(convertirARespuesta(actualizada));
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminarRegla(
            @PathVariable Long eventoId,
            Authentication authentication) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(authentication);

        boolean eliminada = servicioRegla.eliminar(eventoId, usuarioId);

        if (!eliminada) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    private ReglaRecurrencia convertirAEntidad(
            ReglaRecurrenciaDTO datos) {

        ReglaRecurrencia regla = new ReglaRecurrencia();

        regla.setFrecuencia(datos.getFrecuencia());
        regla.setIntervalo(datos.getIntervalo());
        regla.setDiasSemana(datos.getDiasSemana());
        regla.setDiaMes(datos.getDiaMes());
        regla.setFechaFin(datos.getFechaFin());
        regla.setNumeroOcurrencias(datos.getNumeroOcurrencias());

        return regla;
    }

    private ReglaRecurrenciaRespuestaDTO convertirARespuesta(
            ReglaRecurrencia regla) {

        return new ReglaRecurrenciaRespuestaDTO(
                regla.getId(),
                regla.getEvento().getId(),
                regla.getFrecuencia().name(),
                regla.getIntervalo(),
                regla.getDiasSemana(),
                regla.getDiaMes(),
                regla.getFechaFin(),
                regla.getNumeroOcurrencias()
        );
    }
}