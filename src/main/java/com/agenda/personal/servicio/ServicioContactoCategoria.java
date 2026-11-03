
package com.agenda.personal.servicio;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.personal.entidad.ContactoCategoria;
import com.agenda.personal.entidad.ContactoCategoriaId;
import com.agenda.personal.repositorio.CategoriaContactoRepository;
import com.agenda.personal.repositorio.ContactoCategoriaRepository;
import com.agenda.personal.repositorio.ContactoRepository;

@Service
@Transactional
public class ServicioContactoCategoria {

    private final ContactoCategoriaRepository relacionRepository;
    private final ContactoRepository contactoRepository;
    private final CategoriaContactoRepository categoriaRepository;

    public ServicioContactoCategoria(
            ContactoCategoriaRepository relacionRepository,
            ContactoRepository contactoRepository,
            CategoriaContactoRepository categoriaRepository) {

        this.relacionRepository = relacionRepository;
        this.contactoRepository = contactoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<ContactoCategoria> obtenerCategoriasDeContacto(
            Long contactoId, Long usuarioId) {

        verificarContacto(contactoId, usuarioId);

        return relacionRepository
                .findAllByUsuarioIdAndIdContactoId(usuarioId, contactoId);
    }

    @Transactional(readOnly = true)
    public List<ContactoCategoria> obtenerContactosDeCategoria(
            Long categoriaId, Long usuarioId) {

        verificarCategoria(categoriaId, usuarioId);

        return relacionRepository
                .findAllByUsuarioIdAndIdCategoriaId(usuarioId, categoriaId);
    }

    public ContactoCategoria asignarCategoria(
            Long contactoId, Long categoriaId, Long usuarioId) {

        verificarContacto(contactoId, usuarioId);
        verificarCategoria(categoriaId, usuarioId);

        ContactoCategoriaId id =
                new ContactoCategoriaId(contactoId, categoriaId);

        if (relacionRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "La categoría ya está asignada a este contacto.");
        }

        ContactoCategoria relacion =
                new ContactoCategoria(id, usuarioId);

        return relacionRepository.save(relacion);
    }

    public boolean quitarCategoria(
            Long contactoId, Long categoriaId, Long usuarioId) {

        verificarContacto(contactoId, usuarioId);
        verificarCategoria(categoriaId, usuarioId);

        ContactoCategoriaId id =
                new ContactoCategoriaId(contactoId, categoriaId);

        return relacionRepository.findById(id)
                .filter(relacion ->
                        Objects.equals(relacion.getUsuarioId(), usuarioId))
                .map(relacion -> {
                    relacionRepository.delete(relacion);
                    return true;
                })
                .orElse(false);
    }

    private void verificarContacto(Long contactoId, Long usuarioId) {

        if (contactoId == null || usuarioId == null) {
            throw new IllegalArgumentException(
                    "El contacto y el usuario son obligatorios.");
        }

        if (contactoRepository
                .findByIdAndUsuario_Id(contactoId, usuarioId)
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "El contacto no existe o no pertenece al usuario.");
        }
    }

    private void verificarCategoria(Long categoriaId, Long usuarioId) {

        if (categoriaId == null || usuarioId == null) {
            throw new IllegalArgumentException(
                    "La categoría y el usuario son obligatorios.");
        }

        if (categoriaRepository
                .findByIdAndUsuario_Id(categoriaId, usuarioId)
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "La categoría no existe o no pertenece al usuario.");
        }
    }
}
