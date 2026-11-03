package com.agenda.personal.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.personal.entidad.CategoriaContacto;
import com.agenda.personal.entidad.Usuario;
import com.agenda.personal.repositorio.CategoriaContactoRepository;
import com.agenda.personal.repositorio.UsuarioRepository;

@Service
@Transactional
public class ServicioCategoriaContacto {

    private final CategoriaContactoRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public ServicioCategoriaContacto(
            CategoriaContactoRepository categoriaRepository,
            UsuarioRepository usuarioRepository) {

        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaContacto> obtenerCategorias(Long usuarioId) {

        return categoriaRepository
                .findAllByUsuario_IdOrderByNombreAsc(usuarioId);
    }

    @Transactional(readOnly = true)
    public Optional<CategoriaContacto> buscarPorId(
            Long id, Long usuarioId) {

        return categoriaRepository.findByIdAndUsuario_Id(id, usuarioId);
    }

    public CategoriaContacto guardar(
            CategoriaContacto categoria, Long usuarioId) {

        if (categoria == null) {
            throw new IllegalArgumentException(
                    "Los datos de la categoría son obligatorios.");
        }

        if (categoria.getNombre() == null
                || categoria.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre de la categoría es obligatorio.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El usuario no existe."));

        // Registrar una categoría nueva.
        if (categoria.getId() == null) {

            categoria.setUsuario(usuario);
            categoria.setNombre(categoria.getNombre().trim());

            return categoriaRepository.save(categoria);
        }

        // Comprobar que la categoría pertenezca al usuario.
        CategoriaContacto existente = buscarPorId(
                categoria.getId(), usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La categoría no existe o no pertenece al usuario."));

        // Actualizar los datos sin modificar al propietario.
        existente.setNombre(categoria.getNombre().trim());
        existente.setColor(categoria.getColor());
        existente.setDescripcion(categoria.getDescripcion());

        return categoriaRepository.save(existente);
    }

    public boolean eliminar(Long id, Long usuarioId) {

        Optional<CategoriaContacto> categoria =
                buscarPorId(id, usuarioId);

        if (categoria.isEmpty()) {
            return false;
        }

        categoriaRepository.delete(categoria.get());

        return true;
    }
}