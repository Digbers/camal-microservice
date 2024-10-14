package com.microservice.empresas.persistence.repository;

import com.microservice.empresas.persistence.entity.PadronSunat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPadronSunatRepository extends JpaRepository<PadronSunat, String> {
}
