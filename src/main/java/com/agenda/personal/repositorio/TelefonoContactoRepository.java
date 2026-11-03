package com.agenda.personal.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.personal.entidad.TelefonoContacto;

public interface TelefonoContactoRepository extends JpaRepository<TelefonoContacto, Long> {

    List<TelefonoContacto> findAllByContacto_IdOrderByEsPrincipalDesc(Long contactoId);

    Optional<TelefonoContacto> findByIdAndContacto_Id(Long id, Long contactoId);

}