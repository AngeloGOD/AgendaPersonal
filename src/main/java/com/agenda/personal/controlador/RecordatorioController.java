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

import com.agenda.personal.dto.RecordatorioDTO;
import com.agenda.personal.dto.RecordatorioRespuestaDTO;
import com.agenda.personal.entidad.Recordatorio;
import com.agenda.personal.seguridad.UsuarioAutenticadoService;
import com.agenda.personal.servicio.ServicioRecordatorio;

@RestController
@RequestMapping("/api/eventos/{eventoId}/recordatorios")
public class RecordatorioController {

    private final ServicioRecordatorio servicioRecordatorio;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public RecordatorioController(
            ServicioRecordatorio servicioRecordatorio,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.servicioRecordatorio = servicioRecordatorio;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @GetMapping
    public ResponseEntity<List<RecordatorioRespuestaDTO>> obtenerRecordatorios(
            @PathVariable Long eventoId,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        List<RecordatorioRespuestaDTO> recordatorios =
                servicioRecordatorio.obtenerRecordatorios(eventoId, usuarioId)
                        .stream()
                        .map(this::convertirARespuesta)
                        .toList();

        return ResponseEntity.ok(recordatorios);
    }

    @PostMapping
    public ResponseEntity<RecordatorioRespuestaDTO> registrarRecordatorio(
            @PathVariable Long eventoId,
            @Valid @RequestBody RecordatorioDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        Recordatorio recordatorio = convertirAEntidad(datos);

        Recordatorio guardado = servicioRecordatorio.guardar(
                recordatorio, eventoId, usuarioId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertirARespuesta(guardado));
    }

    @PutMapping("/{recordatorioId}")
    public ResponseEntity<RecordatorioRespuestaDTO> actualizarRecordatorio(
            @PathVariable Long eventoId,
            @PathVariable Long recordatorioId,
            @Valid @RequestBody RecordatorioDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        Recordatorio recordatorio = convertirAEntidad(datos);
        recordatorio.setId(recordatorioId);

        Recordatorio actualizado = servicioRecordatorio.guardar(
                recordatorio, eventoId, usuarioId);

        return ResponseEntity.ok(convertirARespuesta(actualizado));
    }

    @DeleteMapping("/{recordatorioId}")
    public ResponseEntity<Void> eliminarRecordatorio(
            @PathVariable Long eventoId,
            @PathVariable Long recordatorioId,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        boolean eliminado = servicioRecordatorio.eliminar(
                recordatorioId, eventoId, usuarioId);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    private Recordatorio convertirAEntidad(RecordatorioDTO datos) {

        Recordatorio recordatorio = new Recordatorio();

        recordatorio.setMinutosAntes(datos.getMinutosAntes());
        recordatorio.setTipo(datos.getTipo());
        recordatorio.setActivo(datos.isActivo());

        return recordatorio;
    }

    private RecordatorioRespuestaDTO convertirARespuesta(
            Recordatorio recordatorio) {

        return new RecordatorioRespuestaDTO(
                recordatorio.getId(),
                recordatorio.getEvento().getId(),
                recordatorio.getMinutosAntes(),
                recordatorio.getTipo().name(),
                recordatorio.isActivo()
        );
    }
}