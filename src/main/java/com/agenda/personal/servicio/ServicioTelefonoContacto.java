
package com.agenda.personal.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.personal.entidad.Contacto;
import com.agenda.personal.entidad.TelefonoContacto;
import com.agenda.personal.repositorio.ContactoRepository;
import com.agenda.personal.repositorio.TelefonoContactoRepository;

@Service
@Transactional
public class ServicioTelefonoContacto {

    private final TelefonoContactoRepository telefonoRepository;
    private final ContactoRepository contactoRepository;

    public ServicioTelefonoContacto(
            TelefonoContactoRepository telefonoRepository,
            ContactoRepository contactoRepository) {

        this.telefonoRepository = telefonoRepository;
        this.contactoRepository = contactoRepository;
    }

    @Transactional(readOnly = true)
    public List<TelefonoContacto> obtenerTelefonos(
            Long contactoId, Long usuarioId) {

        verificarContacto(contactoId, usuarioId);

        return telefonoRepository
                .findAllByContacto_IdOrderByEsPrincipalDesc(contactoId);
    }

    public TelefonoContacto guardar(
            TelefonoContacto telefono,
            Long contactoId,
            Long usuarioId) {

        if (telefono == null) {
            throw new IllegalArgumentException(
                    "Los datos del teléfono son obligatorios.");
        }

        if (telefono.getNumero() == null
                || telefono.getNumero().isBlank()) {

            throw new IllegalArgumentException(
                    "El número de teléfono es obligatorio.");
        }

        Contacto contacto = verificarContacto(contactoId, usuarioId);

        if (telefono.getId() == null) {

            telefono.setContacto(contacto);
            telefono.setNumero(telefono.getNumero().trim());

            if (telefono.isEsPrincipal()) {
                quitarPrincipalAnterior(contactoId, null);
            }

            return telefonoRepository.save(telefono);
        }

        TelefonoContacto existente = telefonoRepository
                .findByIdAndContacto_Id(telefono.getId(), contactoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El teléfono no existe o no pertenece al contacto."));

        if (telefono.isEsPrincipal()) {
            quitarPrincipalAnterior(contactoId, existente.getId());
        }

        existente.setNumero(telefono.getNumero().trim());
        existente.setCodigoPais(telefono.getCodigoPais());
        existente.setTipo(telefono.getTipo());
        existente.setEsPrincipal(telefono.isEsPrincipal());

        return telefonoRepository.save(existente);
    }

    public boolean eliminar(
            Long telefonoId,
            Long contactoId,
            Long usuarioId) {

        if (telefonoId == null) {
            return false;
        }

        if (contactoRepository
                .findByIdAndUsuario_Id(contactoId, usuarioId)
                .isEmpty()) {

            return false;
        }

        Optional<TelefonoContacto> telefono =
                telefonoRepository.findByIdAndContacto_Id(
                        telefonoId, contactoId);

        if (telefono.isEmpty()) {
            return false;
        }

        telefonoRepository.delete(telefono.get());

        return true;
    }

    private Contacto verificarContacto(Long contactoId, Long usuarioId) {

        return contactoRepository
                .findByIdAndUsuario_Id(contactoId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El contacto no existe o no pertenece al usuario."));
    }

    private void quitarPrincipalAnterior(
            Long contactoId, Long telefonoActualId) {

        List<TelefonoContacto> telefonos =
                telefonoRepository
                        .findAllByContacto_IdOrderByEsPrincipalDesc(contactoId);

        for (TelefonoContacto otro : telefonos) {

            boolean esOtroTelefono = telefonoActualId == null
                    || !otro.getId().equals(telefonoActualId);

            if (esOtroTelefono && otro.isEsPrincipal()) {
                otro.setEsPrincipal(false);
                telefonoRepository.save(otro);
            }
        }

        telefonoRepository.flush();
    }
}
