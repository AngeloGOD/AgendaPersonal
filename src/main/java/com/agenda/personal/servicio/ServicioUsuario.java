package com.agenda.personal.servicio;

import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.personal.entidad.Usuario;
import com.agenda.personal.repositorio.UsuarioRepository;

@Service
@Transactional
public class ServicioUsuario {

    private final UsuarioRepository usuarioRepository;

    public ServicioUsuario(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCorreo(String correo) {

        if (correo == null || correo.isBlank()) {
            return Optional.empty();
        }

        return usuarioRepository.findByCorreoIgnoreCase(correo.trim());
    }

    public Usuario guardar(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Los datos del usuario son obligatorios.");
        }

        if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
            throw new IllegalArgumentException(
                    "El correo electrónico es obligatorio.");
        }

        // Normalizar el correo para evitar diferencias por espacios o mayúsculas.
        String correo = usuario.getCorreo()
                .trim()
                .toLowerCase(Locale.ROOT);

        usuario.setCorreo(correo);

        // Comprobar que el correo no pertenezca a otro usuario.
        Optional<Usuario> existente =
                usuarioRepository.findByCorreoIgnoreCase(correo);

        if (existente.isPresent()
                && !existente.get().getId().equals(usuario.getId())) {

            throw new IllegalArgumentException(
                    "Ya existe una cuenta registrada con ese correo.");
        }

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