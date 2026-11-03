package com.agenda.personal.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.personal.entidad.DireccionContacto;

public interface DireccionContactoRepository extends JpaRepository<DireccionContacto, Long> {

    List<DireccionContacto> findAllByContacto_IdOrderByEsPrincipalDesc(Long contactoId);

    Optional<DireccionContacto> findByIdAndContacto_Id(Long id, Long contactoId);

}