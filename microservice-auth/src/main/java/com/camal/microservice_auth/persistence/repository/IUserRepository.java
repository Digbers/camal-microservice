package com.camal.microservice_auth.persistence.repository;

import com.camal.microservice_auth.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface IUserRepository extends JpaRepository<UserEntity,Long>, JpaSpecificationExecutor<UserEntity> {
    Optional<UserEntity> findUserEntityByUsername(String username);
    Optional<UserEntity> findByUsercodigo(String usercodigo);

    @Query("SELECT u FROM UserEntity u WHERE u.username = ?1")
    Optional<UserEntity> findUser(String username);

    @Query("SELECT u.usercodigo FROM UserEntity u WHERE u.username = ?1")
    Optional<String> findUserCodigo(String username);
}