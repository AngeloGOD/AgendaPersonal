package com.agenda.personal.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.personal.entidad.Organizacion;
import com.agenda.personal.entidad.Usuario;
import com.agenda.personal.repositorio.OrganizacionRepository;
import com.agenda.personal.repositorio.UsuarioRepository;

@Service
@Transactional
public class ServicioOrganizacion {

    private final OrganizacionRepository organizacionRepository;
    private final UsuarioRepository usuarioRepository;

    public ServicioOrganizacion(
            OrganizacionRepository organizacionRepository,
            UsuarioRepository usuarioRepository) {

        this.organizacionRepository = organizacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Organizacion> obtenerOrganizaciones(Long usuarioId) {

        return organizacionRepository
                .findAllByUsuario_IdOrderByNombreAsc(usuarioId);
    }

    @Transactional(readOnly = true)
    public Optional<Organizacion> buscarPorId(Long id, Long usuarioId) {

        return organizacionRepository.findByIdAndUsuario_Id(id, usuarioId);
    }

    public Organizacion guardar(Organizacion organizacion, Long usuarioId) {

        if (organizacion == null) {
            throw new IllegalArgumentException(
                    "Los datos de la organización son obligatorios.");
        }

        if (organizacion.getNombre() == null
                || organizacion.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre de la organización es obligatorio.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El usuario no existe."));

        // Registrar una organización nueva.
        if (organizacion.getId() == null) {

            organizacion.setUsuario(usuario);
            organizacion.setNombre(organizacion.getNombre().trim());

            return organizacionRepository.save(organizacion);
        }

        // Comprobar que la organización pertenezca al usuario.
        Organizacion existente = buscarPorId(
                organizacion.getId(), usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La organización no existe o no pertenece al usuario."));

        // Actualizar los datos sin modificar al propietario.
        existente.setNombre(organizacion.getNombre().trim());
        existente.setTipo(organizacion.getTipo());
        existente.setSitioWeb(organizacion.getSitioWeb());
        existente.setTelefonoGeneral(organizacion.getTelefonoGeneral());
        existente.setDescripcion(organizacion.getDescripcion());

        return organizacionRepository.save(existente);
    }

    public boolean eliminar(Long id, Long usuarioId) {

        Optional<Organizacion> organizacion =
                buscarPorId(id, usuarioId);

        if (organizacion.isEmpty()) {
            return false;
        }

        organizacionRepository.delete(organizacion.get());

        return true;
    }
}