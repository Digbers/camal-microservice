package com.camal.microservice_auth.controller;

import com.camal.microservice_auth.controller.dto.RolesDTO;
import com.camal.microservice_auth.service.userDetail.IRolesService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("auth/roles")
public class RolesController {
    private final IRolesService rolesService;

    @GetMapping("/list")
    public ResponseEntity<List<RolesDTO>> getRoles() {
        return ResponseEntity.ok(rolesService.getRoles());
    }
}
