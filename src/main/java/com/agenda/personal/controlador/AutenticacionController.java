
package com.agenda.personal.controlador;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agenda.personal.dto.UsuarioLoginDTO;
import com.agenda.personal.dto.UsuarioRespuestaDTO;
import com.agenda.personal.entidad.Usuario;
import com.agenda.personal.servicio.ServicioUsuario;

@RestController
@RequestMapping("/api/autenticacion")
public class AutenticacionController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final ServicioUsuario servicioUsuario;

    public AutenticacionController(
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository,
            ServicioUsuario servicioUsuario) {

        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.servicioUsuario = servicioUsuario;
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioRespuestaDTO> iniciarSesion(
            @Valid @RequestBody UsuarioLoginDTO datos,
            HttpServletRequest request,
            HttpServletResponse response) {

        Authentication autenticacion =
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        datos.getCorreo().trim(),
                        datos.getContrasena()
                    )
                );

        SecurityContext contexto =
                SecurityContextHolder.createEmptyContext();

        contexto.setAuthentication(autenticacion);
        SecurityContextHolder.setContext(contexto);

        securityContextRepository.saveContext(
                contexto,
                request,
                response
        );

        Usuario usuario = servicioUsuario
                .buscarPorCorreo(autenticacion.getName())
                .orElseThrow();

        UsuarioRespuestaDTO respuesta =
                new UsuarioRespuestaDTO(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getApellidos(),
                    usuario.getCorreo(),
                    usuario.getZonaHoraria(),
                    usuario.isActivo(),
                    usuario.getFechaCreacion()
                );

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> cerrarSesion(
            HttpServletRequest request,
            HttpServletResponse response) {

        SecurityContextHolder.clearContext();

        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }

        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/yo")
    public ResponseEntity<UsuarioRespuestaDTO> obtenerUsuarioAutenticado(
            Authentication autenticacion) {

        Usuario usuario = servicioUsuario
                .buscarPorCorreo(autenticacion.getName())
                .orElseThrow();

        UsuarioRespuestaDTO respuesta = new UsuarioRespuestaDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getCorreo(),
                usuario.getZonaHoraria(),
                usuario.isActivo(),
                usuario.getFechaCreacion()
        );

        return ResponseEntity.ok(respuesta);
    }
}
