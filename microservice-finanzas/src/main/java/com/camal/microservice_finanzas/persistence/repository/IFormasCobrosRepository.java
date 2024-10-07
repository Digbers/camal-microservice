package com.camal.microservice_finanzas.persistence.repository;

import com.camal.microservice_finanzas.persistence.entity.FormasCobrosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface IFormasCobrosRepository extends JpaRepository<FormasCobrosEntity, String> {
    List<FormasCobrosEntity> findByIdEmpresa(Long idEmpresa);
}
