
package com.agenda.personal.servicio;

import java.util.Locale;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.personal.entidad.Usuario;
import com.agenda.personal.repositorio.UsuarioRepository;

@Service
@Transactional
public class ServicioUsuario {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public ServicioUsuario(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCorreo(String correo) {

        if (correo == null || correo.isBlank()) {
            return Optional.empty();
        }

        return usuarioRepository.findByCorreoIgnoreCase(
                correo.trim()
        );
    }

    public Usuario guardar(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Los datos del usuario son obligatorios."
            );
        }

        if (usuario.getCorreo() == null
                || usuario.getCorreo().isBlank()) {

            throw new IllegalArgumentException(
                    "El correo electrónico es obligatorio."
            );
        }

        if (usuario.getContrasenaHash() == null
                || usuario.getContrasenaHash().isBlank()) {

            throw new IllegalArgumentException(
                    "La contraseña es obligatoria."
            );
        }

        // Normalizar el correo.
        String correo = usuario.getCorreo()
                .trim()
                .toLowerCase(Locale.ROOT);

        usuario.setCorreo(correo);

        // Comprobar que el correo no esté registrado.
        Optional<Usuario> existente =
                usuarioRepository.findByCorreoIgnoreCase(correo);

        if (existente.isPresent()
                && !existente.get().getId().equals(usuario.getId())) {

            throw new IllegalArgumentException(
                    "Ya existe una cuenta registrada con ese correo."
            );
        }

        // Convertir la contraseña en un hash BCrypt.
        usuario.setContrasenaHash(
                passwordEncoder.encode(usuario.getContrasenaHash())
        );

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {

        if (id == null) {
            return Optional.empty();
        }

        return usuarioRepository.findById(id);
    }
}
