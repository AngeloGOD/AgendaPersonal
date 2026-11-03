
package com.agenda.personal.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.personal.entidad.Contacto;
import com.agenda.personal.entidad.DireccionContacto;
import com.agenda.personal.repositorio.ContactoRepository;
import com.agenda.personal.repositorio.DireccionContactoRepository;

@Service
@Transactional
public class ServicioDireccionContacto {

    private final DireccionContactoRepository direccionRepository;
    private final ContactoRepository contactoRepository;

    public ServicioDireccionContacto(
            DireccionContactoRepository direccionRepository,
            ContactoRepository contactoRepository) {

        this.direccionRepository = direccionRepository;
        this.contactoRepository = contactoRepository;
    }

    @Transactional(readOnly = true)
    public List<DireccionContacto> obtenerDirecciones(
            Long contactoId, Long usuarioId) {

        verificarContacto(contactoId, usuarioId);

        return direccionRepository
                .findAllByContacto_IdOrderByEsPrincipalDesc(contactoId);
    }

    public DireccionContacto guardar(
            DireccionContacto direccion,
            Long contactoId,
            Long usuarioId) {

        if (direccion == null) {
            throw new IllegalArgumentException(
                    "Los datos de la dirección son obligatorios.");
        }

        Contacto contacto = verificarContacto(contactoId, usuarioId);

        if (direccion.getId() == null) {

            direccion.setContacto(contacto);

            if (direccion.isEsPrincipal()) {
                quitarPrincipalAnterior(contactoId, null);
            }

            return direccionRepository.save(direccion);
        }

        DireccionContacto existente = direccionRepository
                .findByIdAndContacto_Id(direccion.getId(), contactoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La dirección no existe o no pertenece al contacto."));

        if (direccion.isEsPrincipal()) {
            quitarPrincipalAnterior(contactoId, existente.getId());
        }

        existente.setCalle(direccion.getCalle());
        existente.setNumeroExterior(direccion.getNumeroExterior());
        existente.setNumeroInterior(direccion.getNumeroInterior());
        existente.setColonia(direccion.getColonia());
        existente.setCiudad(direccion.getCiudad());
        existente.setEstado(direccion.getEstado());
        existente.setCodigoPostal(direccion.getCodigoPostal());
        existente.setPais(direccion.getPais());
        existente.setTipo(direccion.getTipo());
        existente.setEsPrincipal(direccion.isEsPrincipal());

        return direccionRepository.save(existente);
    }

    public boolean eliminar(
            Long direccionId, Long contactoId, Long usuarioId) {

        if (direccionId == null) {
            return false;
        }

        if (contactoRepository
                .findByIdAndUsuario_Id(contactoId, usuarioId)
                .isEmpty()) {

            return false;
        }

        Optional<DireccionContacto> direccion =
                direccionRepository.findByIdAndContacto_Id(
                        direccionId, contactoId);

        if (direccion.isEmpty()) {
            return false;
        }

        direccionRepository.delete(direccion.get());

        return true;
    }

    private Contacto verificarContacto(
            Long contactoId, Long usuarioId) {

        return contactoRepository
                .findByIdAndUsuario_Id(contactoId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El contacto no existe o no pertenece al usuario."));
    }

    private void quitarPrincipalAnterior(
            Long contactoId, Long direccionActualId) {

        List<DireccionContacto> direcciones =
                direccionRepository
                        .findAllByContacto_IdOrderByEsPrincipalDesc(contactoId);

        for (DireccionContacto otra : direcciones) {

            boolean esOtraDireccion = direccionActualId == null
                    || !otra.getId().equals(direccionActualId);

            if (esOtraDireccion && otra.isEsPrincipal()) {
                otra.setEsPrincipal(false);
                direccionRepository.save(otra);
            }
        }

        direccionRepository.flush();
    }
}
