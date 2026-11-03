
package com.agenda.personal.seguridad;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.agenda.personal.entidad.Usuario;
import com.agenda.personal.servicio.ServicioUsuario;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final ServicioUsuario servicioUsuario;

    public UsuarioDetailsService(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    @Override
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException {

        Usuario usuario = servicioUsuario.buscarPorCorreo(correo)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "No existe una cuenta con ese correo."
                    )
                );

        return User.withUsername(usuario.getCorreo())
                .password(usuario.getContrasenaHash())
                .disabled(!usuario.isActivo())
                .authorities("ROLE_USER")
                .build();
    }
}
