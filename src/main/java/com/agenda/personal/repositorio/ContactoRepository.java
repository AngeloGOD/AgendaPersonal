package com.agenda.personal.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.personal.entidad.Contacto;

public interface ContactoRepository extends JpaRepository<Contacto, Long> {

    List<Contacto> findAllByUsuario_IdOrderByNombreAscApellidosAsc(Long usuarioId);

    Optional<Contacto> findByIdAndUsuario_Id(Long id, Long usuarioId);

}