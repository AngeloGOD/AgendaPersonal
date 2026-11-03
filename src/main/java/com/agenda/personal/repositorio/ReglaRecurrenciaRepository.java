package com.agenda.personal.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.personal.entidad.ReglaRecurrencia;

public interface ReglaRecurrenciaRepository
        extends JpaRepository<ReglaRecurrencia, Long> {

    Optional<ReglaRecurrencia> findByEvento_Id(Long eventoId);

    Optional<ReglaRecurrencia> findByIdAndEvento_Id(Long id, Long eventoId);

}