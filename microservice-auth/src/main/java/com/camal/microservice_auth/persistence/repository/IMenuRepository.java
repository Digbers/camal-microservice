package com.camal.microservice_auth.persistence.repository;

import com.camal.microservice_auth.persistence.entity.MenusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMenuRepository extends JpaRepository<MenusEntity, Long> {
}
