package com.microservice.inventario.persistence.repository;

import com.microservice.inventario.persistence.entity.AlmacenTipoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAlmacenTipoRepository extends JpaRepository<AlmacenTipoEntity, Long> {
    List<AlmacenTipoEntity> findAllByIdEmpresa(Long idEmpresa);
    Optional<AlmacenTipoEntity> findByIdEmpresaAndCodigo(Long idEmpresa, String codigo);
}
