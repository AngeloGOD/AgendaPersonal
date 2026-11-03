package com.agenda.personal.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.personal.entidad.CorreoContacto;

public interface CorreoContactoRepository extends JpaRepository<CorreoContacto, Long> {

    List<CorreoContacto> findAllByContacto_IdOrderByEsPrincipalDesc(Long contactoId);

    Optional<CorreoContacto> findByIdAndContacto_Id(Long id, Long contactoId);

}