package com.microservice.inventario.persistence.repository;

import com.microservice.inventario.persistence.entity.AlmacenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAlmacenRepository extends JpaRepository<AlmacenEntity, Long> {

    @Query("SELECT u FROM AlmacenEntity u WHERE u.idEmpresa = ?1")
    List<AlmacenEntity> findAllByIdEmpresa(Long idEmpresa);
}
