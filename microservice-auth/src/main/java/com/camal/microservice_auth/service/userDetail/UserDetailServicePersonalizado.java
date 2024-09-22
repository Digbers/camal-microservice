package com.camal.microservice_auth.service.userDetail;

import com.camal.microservice_auth.controller.dto.MenuDTO;
import com.camal.microservice_auth.controller.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserDetailServicePersonalizado {
    Page<UserDTO> findAll(Long id, String username, Boolean isEnabled, Pageable pageable);

    void agregarMenus(Long userId, List<Long> menusId);
    void eliminarMenus(Long userId, List<Long> menusId);
     List<MenuDTO> obtenerMenus(String userName);
     List<MenuDTO> obtenerMenus(Long userId);
}
