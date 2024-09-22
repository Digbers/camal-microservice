package com.camal.microservice_auth.service.userDetail;

import com.camal.microservice_auth.controller.dto.MenuDTO;

import com.camal.microservice_auth.persistence.entity.MenusEntity;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
public interface IMenuService {
    Page<MenuDTO> findAll(Pageable pageable);
    Optional<MenusEntity> findById(Long id);
    MenuDTO save(MenuDTO menuDTO);
    boolean deleteById(Long id);

}
