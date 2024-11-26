package com.microservice.empresas.persistence.repository;

import com.microservice.empresas.persistence.entity.EmpresaEntity;
import com.microservice.empresas.persistence.entity.EntidadEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
    @Query("""
        select e from EntidadEntity e 
        inner join e.entidadesTiposList et 
        left join fetch e.asistencias a
        where et.tipoCodigo = :tipoCodigo 
          and e.empresa.id = :idEmpresa 
          and a.fechaAsistencia BETWEEN :startDate AND :endDate
        """)
    Page<EntidadEntity> findAllByTipoCodigoAndEmpresa(
            @Param("tipoCodigo") String tipoCodigo,
            @Param("idEmpresa") Long idEmpresa,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("""
        select e from EntidadEntity e 
        inner join e.entidadesTiposList et 
        where et.tipoCodigo = :tipoCodigo 
          and e.empresa.id = :idEmpresa
        """)
    Page<EntidadEntity> findAllTrabajadoresByTipoCodigoAndEmpresa(
            @Param("tipoCodigo") String tipoCodigo,
            @Param("idEmpresa") Long idEmpresa,
            Pageable pageable);

}
