package com.agenda.personal.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.personal.entidad.Contacto;
import com.agenda.personal.entidad.Usuario;
import com.agenda.personal.repositorio.ContactoRepository;
import com.agenda.personal.repositorio.UsuarioRepository;

@Service
@Transactional
public class ServicioContacto {

    private final ContactoRepository contactoRepository;
    private final UsuarioRepository usuarioRepository;

    public ServicioContacto(
            ContactoRepository contactoRepository,
            UsuarioRepository usuarioRepository) {

        this.contactoRepository = contactoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Contacto> obtenerContactos(Long usuarioId) {

        return contactoRepository
                .findAllByUsuario_IdOrderByNombreAscApellidosAsc(usuarioId);
    }

    @Transactional(readOnly = true)
    public Optional<Contacto> buscarPorId(Long id, Long usuarioId) {

        return contactoRepository.findByIdAndUsuario_Id(id, usuarioId);
    }

    public Contacto guardar(Contacto contacto, Long usuarioId) {

        if (contacto == null) {
            throw new IllegalArgumentException(
                    "Los datos del contacto son obligatorios.");
        }

        if (contacto.getNombre() == null
                || contacto.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre del contacto es obligatorio.");
        }

        // Comprobar que el usuario propietario exista.
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El usuario no existe."));

        // Registrar un contacto nuevo.
        if (contacto.getId() == null) {

            contacto.setUsuario(usuario);

            return contactoRepository.save(contacto);
        }

        // Buscar el contacto comprobando que pertenezca al usuario.
        Contacto existente = buscarPorId(
                contacto.getId(), usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El contacto no existe o no pertenece al usuario."));

        // Actualizar únicamente los datos permitidos.
        existente.setNombre(contacto.getNombre().trim());
        existente.setApellidos(contacto.getApellidos());
        existente.setApodo(contacto.getApodo());
        existente.setFechaNacimiento(contacto.getFechaNacimiento());
        existente.setNotas(contacto.getNotas());
        existente.setFavorito(contacto.isFavorito());

        return contactoRepository.save(existente);
    }

    public boolean eliminar(Long id, Long usuarioId) {

        Optional<Contacto> contacto = buscarPorId(id, usuarioId);

        if (contacto.isEmpty()) {
            return false;
        }

        contactoRepository.delete(contacto.get());

        return true;
    }
}