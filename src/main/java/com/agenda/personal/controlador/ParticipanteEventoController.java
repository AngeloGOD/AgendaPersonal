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

import com.agenda.personal.dto.ParticipanteEventoDTO;
import com.agenda.personal.dto.ParticipanteEventoRespuestaDTO;
import com.agenda.personal.entidad.ParticipanteEvento;
import com.agenda.personal.seguridad.UsuarioAutenticadoService;
import com.agenda.personal.servicio.ServicioParticipanteEvento;

@RestController
@RequestMapping("/api/eventos/{eventoId}/participantes")
public class ParticipanteEventoController {

    private final ServicioParticipanteEvento servicioParticipante;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public ParticipanteEventoController(
            ServicioParticipanteEvento servicioParticipante,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.servicioParticipante = servicioParticipante;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @GetMapping
    public ResponseEntity<List<ParticipanteEventoRespuestaDTO>> obtenerParticipantes(
            @PathVariable Long eventoId,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        List<ParticipanteEventoRespuestaDTO> participantes =
                servicioParticipante.obtenerParticipantes(eventoId, usuarioId)
                        .stream()
                        .map(this::convertirARespuesta)
                        .toList();

        return ResponseEntity.ok(participantes);
    }

    @PostMapping
    public ResponseEntity<ParticipanteEventoRespuestaDTO> agregarParticipante(
            @PathVariable Long eventoId,
            @Valid @RequestBody ParticipanteEventoDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        ParticipanteEvento participante =
                servicioParticipante.agregarParticipante(
                        eventoId,
                        datos.getContactoId(),
                        usuarioId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertirARespuesta(participante));
    }

    @DeleteMapping("/{contactoId}")
    public ResponseEntity<Void> quitarParticipante(
            @PathVariable Long eventoId,
            @PathVariable Long contactoId,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        boolean eliminado = servicioParticipante.quitarParticipante(
                eventoId, contactoId, usuarioId);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    private ParticipanteEventoRespuestaDTO convertirARespuesta(
            ParticipanteEvento participante) {

        return new ParticipanteEventoRespuestaDTO(
                participante.getId(),
                participante.getEventoId(),
                participante.getContactoId(),
                participante.getUsuarioId(),
                participante.getEstadoInvitacion().name()
        );
    }
}