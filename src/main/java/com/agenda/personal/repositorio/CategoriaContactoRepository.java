package com.agenda.personal.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.personal.entidad.CategoriaContacto;

public interface CategoriaContactoRepository extends JpaRepository<CategoriaContacto, Long> {

    List<CategoriaContacto> findAllByUsuario_IdOrderByNombreAsc(Long usuarioId);

    Optional<CategoriaContacto> findByIdAndUsuario_Id(Long id, Long usuarioId);

}