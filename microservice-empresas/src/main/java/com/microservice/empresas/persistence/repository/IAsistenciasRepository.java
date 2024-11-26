package com.microservice.empresas.persistence.repository;

import com.microservice.empresas.persistence.entity.AsistenciasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface IAsistenciasRepository extends JpaRepository<AsistenciasEntity, Long> {
    @Query("select a from AsistenciasEntity a where a.entidad.id = :idEntidad and a.fechaAsistencia = :fechaAsistencia")
    Optional<AsistenciasEntity> findByIdEntidadAndFechaAsistencia(Long idEntidad, LocalDate fechaAsistencia);
}
