
package com.agenda.personal.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.personal.entidad.Contacto;
import com.agenda.personal.entidad.CorreoContacto;
import com.agenda.personal.repositorio.ContactoRepository;
import com.agenda.personal.repositorio.CorreoContactoRepository;

@Service
@Transactional
public class ServicioCorreoContacto {

    private final CorreoContactoRepository correoRepository;
    private final ContactoRepository contactoRepository;

    public ServicioCorreoContacto(
            CorreoContactoRepository correoRepository,
            ContactoRepository contactoRepository) {

        this.correoRepository = correoRepository;
        this.contactoRepository = contactoRepository;
    }

    @Transactional(readOnly = true)
    public List<CorreoContacto> obtenerCorreos(
            Long contactoId, Long usuarioId) {

        verificarContacto(contactoId, usuarioId);

        return correoRepository
                .findAllByContacto_IdOrderByEsPrincipalDesc(contactoId);
    }

    public CorreoContacto guardar(
            CorreoContacto correo, Long contactoId, Long usuarioId) {

        if (correo == null) {
            throw new IllegalArgumentException(
                    "Los datos del correo son obligatorios.");
        }

        if (correo.getCorreo() == null
                || correo.getCorreo().isBlank()) {

            throw new IllegalArgumentException(
                    "La dirección de correo es obligatoria.");
        }

        String direccionCorreo = correo.getCorreo().trim();

        if (!direccionCorreo.matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            throw new IllegalArgumentException(
                    "La dirección de correo no tiene un formato válido.");
        }

        Contacto contacto = verificarContacto(contactoId, usuarioId);

        if (correo.getId() == null) {

            correo.setContacto(contacto);
            correo.setCorreo(direccionCorreo);

            if (correo.isEsPrincipal()) {
                quitarPrincipalAnterior(contactoId, null);
            }

            return correoRepository.save(correo);
        }

        CorreoContacto existente = correoRepository
                .findByIdAndContacto_Id(correo.getId(), contactoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El correo no existe o no pertenece al contacto."));

        if (correo.isEsPrincipal()) {
            quitarPrincipalAnterior(contactoId, existente.getId());
        }

        existente.setCorreo(direccionCorreo);
        existente.setTipo(correo.getTipo());
        existente.setEsPrincipal(correo.isEsPrincipal());

        return correoRepository.save(existente);
    }

    public boolean eliminar(
            Long correoId, Long contactoId, Long usuarioId) {

        if (correoId == null) {
            return false;
        }

        if (contactoRepository
                .findByIdAndUsuario_Id(contactoId, usuarioId)
                .isEmpty()) {

            return false;
        }

        Optional<CorreoContacto> correo =
                correoRepository.findByIdAndContacto_Id(
                        correoId, contactoId);

        if (correo.isEmpty()) {
            return false;
        }

        correoRepository.delete(correo.get());

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
            Long contactoId, Long correoActualId) {

        List<CorreoContacto> correos =
                correoRepository
                        .findAllByContacto_IdOrderByEsPrincipalDesc(contactoId);

        for (CorreoContacto otro : correos) {

            boolean esOtroCorreo = correoActualId == null
                    || !otro.getId().equals(correoActualId);

            if (esOtroCorreo && otro.isEsPrincipal()) {
                otro.setEsPrincipal(false);
                correoRepository.save(otro);
            }
        }

        correoRepository.flush();
    }
}
