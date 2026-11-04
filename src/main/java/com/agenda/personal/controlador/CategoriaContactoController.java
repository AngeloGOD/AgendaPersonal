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

import com.agenda.personal.dto.CategoriaContactoDTO;
import com.agenda.personal.dto.CategoriaContactoRespuestaDTO;
import com.agenda.personal.entidad.CategoriaContacto;
import com.agenda.personal.seguridad.UsuarioAutenticadoService;
import com.agenda.personal.servicio.ServicioCategoriaContacto;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaContactoController {

    private final ServicioCategoriaContacto servicioCategoria;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public CategoriaContactoController(
            ServicioCategoriaContacto servicioCategoria,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.servicioCategoria = servicioCategoria;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    // Obtener todas las categorías del usuario.
    @GetMapping
    public ResponseEntity<List<CategoriaContactoRespuestaDTO>> obtenerCategorias(
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        List<CategoriaContactoRespuestaDTO> categorias =
                servicioCategoria.obtenerCategorias(usuarioId)
                        .stream()
                        .map(this::convertirARespuesta)
                        .toList();

        return ResponseEntity.ok(categorias);
    }

    // Obtener una categoría por su ID.
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaContactoRespuestaDTO> obtenerCategoria(
            @PathVariable Long id,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        return servicioCategoria.buscarPorId(id, usuarioId)
                .map(this::convertirARespuesta)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Registrar una categoría.
    @PostMapping
    public ResponseEntity<CategoriaContactoRespuestaDTO> registrarCategoria(
            @Valid @RequestBody CategoriaContactoDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        CategoriaContacto categoria = convertirAEntidad(datos);

        CategoriaContacto guardada =
                servicioCategoria.guardar(categoria, usuarioId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertirARespuesta(guardada));
    }

    // Actualizar una categoría.
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaContactoRespuestaDTO> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaContactoDTO datos,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        CategoriaContacto categoria = convertirAEntidad(datos);
        categoria.setId(id);

        CategoriaContacto actualizada =
                servicioCategoria.guardar(categoria, usuarioId);

        return ResponseEntity.ok(convertirARespuesta(actualizada));
    }

    // Eliminar una categoría.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(
            @PathVariable Long id,
            Authentication autenticacion) {

        Long usuarioId = usuarioAutenticadoService
                .obtenerUsuarioId(autenticacion);

        boolean eliminada =
                servicioCategoria.eliminar(id, usuarioId);

        if (!eliminada) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    // Convertir DTO a entidad.
    private CategoriaContacto convertirAEntidad(
            CategoriaContactoDTO datos) {

        CategoriaContacto categoria = new CategoriaContacto();

        categoria.setNombre(datos.getNombre().trim());
        categoria.setColor(datos.getColor());
        categoria.setDescripcion(datos.getDescripcion());

        return categoria;
    }

    // Convertir entidad a DTO de respuesta.
    private CategoriaContactoRespuestaDTO convertirARespuesta(
            CategoriaContacto categoria) {

        return new CategoriaContactoRespuestaDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getColor(),
                categoria.getDescripcion()
        );
    }
}