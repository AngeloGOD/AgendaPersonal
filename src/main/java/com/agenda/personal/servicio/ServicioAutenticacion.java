
package com.agenda.personal.servicio;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.personal.dto.UsuarioLoginDTO;
import com.agenda.personal.dto.UsuarioRespuestaDTO;
import com.agenda.personal.entidad.Usuario;

@Service
@Transactional
public class ServicioAutenticacion {

    private final AuthenticationManager authenticationManager;
    private final ServicioUsuario servicioUsuario;

    public ServicioAutenticacion(
            AuthenticationManager authenticationManager,
            ServicioUsuario servicioUsuario) {

        this.authenticationManager = authenticationManager;
        this.servicioUsuario = servicioUsuario;
    }

    @Transactional(readOnly = true)
    public UsuarioRespuestaDTO iniciarSesion(UsuarioLoginDTO datos) {

        if (datos == null
                || datos.getCorreo() == null
                || datos.getCorreo().isBlank()
                || datos.getContrasena() == null
                || datos.getContrasena().isBlank()) {

            throw new IllegalArgumentException(
                    "El correo y la contraseña son obligatorios."
            );
        }

        String correo = datos.getCorreo().trim();

        // Spring Security verifica el correo y la contraseña.
        Authentication autenticacion =
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        correo,
                        datos.getContrasena()
                    )
                );

        // Obtener el correo validado por Spring Security.
        String correoAutenticado =
                autenticacion.getName();

        Usuario usuario = servicioUsuario
                .buscarPorCorreo(correoAutenticado)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "No se encontró el usuario autenticado."
                    )
                );

        // Devolver únicamente datos públicos del usuario.
        return new UsuarioRespuestaDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getCorreo(),
                usuario.getZonaHoraria(),
                usuario.isActivo(),
                usuario.getFechaCreacion()
        );
    }
}
