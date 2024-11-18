package com.camal.microservice_auth.service.userDetail;

import com.camal.microservice_auth.controller.dto.RolesDTO;

import java.util.List;

public interface IRolesService {
    List<RolesDTO> getRoles();
}
