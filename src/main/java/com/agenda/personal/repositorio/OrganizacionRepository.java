package com.agenda.personal.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.personal.entidad.Organizacion;

public interface OrganizacionRepository extends JpaRepository<Organizacion, Long> {

    List<Organizacion> findAllByUsuario_IdOrderByNombreAsc(Long usuarioId);

    Optional<Organizacion> findByIdAndUsuario_Id(Long id, Long usuarioId);

}