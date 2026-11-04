package com.agenda.personal.seguridad;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.agenda.personal.entidad.Usuario;
import com.agenda.personal.servicio.ServicioUsuario;

@Service
public class UsuarioAutenticadoService {

    private final ServicioUsuario servicioUsuario;

    public UsuarioAutenticadoService(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    public Usuario obtenerUsuario(Authentication autenticacion) {

        if (autenticacion == null || !autenticacion.isAuthenticated()) {
            throw new IllegalStateException(
                    "No existe un usuario autenticado.");
        }

        return servicioUsuario.buscarPorCorreo(autenticacion.getName())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No se encontró el usuario autenticado."));
    }

    public Long obtenerUsuarioId(Authentication autenticacion) {
        return obtenerUsuario(autenticacion).getId();
    }
}