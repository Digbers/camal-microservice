package com.camal.microservice_auth.service;

import com.camal.microservice_auth.controller.dto.RolesDTO;
import com.camal.microservice_auth.persistence.repository.RoleRepository;
import com.camal.microservice_auth.service.userDetail.IRolesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class RolesService implements IRolesService {

    private final RoleRepository roleRepository;

    @Override
    public List<RolesDTO> getRoles() {
        try {
            return roleRepository.findAll().stream().map(role -> RolesDTO.builder().id(role.getId()).roleEnum(role.getRoleEnum().name()).build()).toList();
        } catch (Exception e) {
            log.error("Error al obtener roles", e);
            throw new RuntimeException("Error al obtener roles");
        }
    }
}
