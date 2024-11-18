package com.microservice.empresas.persistence.repository;

import com.microservice.empresas.persistence.entity.EmpresaEntity;
import com.microservice.empresas.persistence.entity.EntidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IEntidadRepository extends JpaRepository<EntidadEntity, Long>, JpaSpecificationExecutor<EntidadEntity> {
    Optional<EntidadEntity> findByEmpresaAndNroDocumento(EmpresaEntity empresa, String nroDocumento);
    Optional<EntidadEntity> findByNroDocumentoAndDocumentoTipo_DocCodigo(String nroDocumento, String documentoTipoDocCodigo);
    @Query("select e from EntidadEntity e where e.nroDocumento like %?1%")
    List<EntidadEntity> findNroDocumento(String nroDocumento);
    @Query("select e from EntidadEntity e where e.nombre like %?1%")
    List<EntidadEntity> findByNombre(String nombre);
}
