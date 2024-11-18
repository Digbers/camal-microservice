package com.camal.microservice_auth.persistence.repository;

import com.camal.microservice_auth.persistence.entity.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<RolesEntity, Long> {
    List<RolesEntity> findRoleEntityByRoleEnumIn(List<String> roleNames);
}
