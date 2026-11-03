package com.agenda.personal.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.personal.entidad.ContactoCategoria;
import com.agenda.personal.entidad.ContactoCategoriaId;

public interface ContactoCategoriaRepository
        extends JpaRepository<ContactoCategoria, ContactoCategoriaId> {

    List<ContactoCategoria> findAllByUsuarioIdAndIdContactoId(
            Long usuarioId,
            Long contactoId
    );

    List<ContactoCategoria> findAllByUsuarioIdAndIdCategoriaId(
            Long usuarioId,
            Long categoriaId
    );

}