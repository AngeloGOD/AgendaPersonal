
package com.agenda.personal.controlador;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agenda.personal.dto.UsuarioRegistroDTO;
import com.agenda.personal.dto.UsuarioRespuestaDTO;
import com.agenda.personal.entidad.Usuario;
import com.agenda.personal.servicio.ServicioUsuario;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final ServicioUsuario servicioUsuario;

    public UsuarioController(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioRespuestaDTO> registrarUsuario(
            @Valid @RequestBody UsuarioRegistroDTO datos) {

        Usuario usuario = new Usuario();

        usuario.setNombre(datos.getNombre().trim());
        usuario.setApellidos(datos.getApellidos().trim());
        usuario.setCorreo(datos.getCorreo().trim());

        usuario.setContrasenaHash(datos.getContrasena());

        if (datos.getZonaHoraria() != null
                && !datos.getZonaHoraria().isBlank()) {

            usuario.setZonaHoraria(datos.getZonaHoraria().trim());
        }

        Usuario usuarioGuardado = servicioUsuario.guardar(usuario);

        UsuarioRespuestaDTO respuesta =
                new UsuarioRespuestaDTO(
                    usuarioGuardado.getId(),
                    usuarioGuardado.getNombre(),
                    usuarioGuardado.getApellidos(),
                    usuarioGuardado.getCorreo(),
                    usuarioGuardado.getZonaHoraria(),
                    usuarioGuardado.isActivo(),
                    usuarioGuardado.getFechaCreacion()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }
}
