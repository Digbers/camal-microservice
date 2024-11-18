package com.microservice.empresas.controller;

import com.microservice.empresas.controller.dto.EntidadesTiposDTO;
import com.microservice.empresas.service.impl.EntidadesTiposServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empresas/entidades/tipos")
@RequiredArgsConstructor
public class EntidadTipoController {
    private final EntidadesTiposServiceImpl entidadesTiposServiceImpl;

    @GetMapping("/findAll/{idEmpresa}")
    public ResponseEntity<Page<EntidadesTiposDTO>> findAll(@PathVariable Long idEmpresa,
                                                           @RequestParam(name = "tipoCodigo", required = false) String tipoCodigo,
                                                           @RequestParam(name = "descripcion", required = false) String descripcion,
                                                           @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                           @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                                           @RequestParam(required = false) String sort) {
        Pageable pageable;

        // Verificar si el parámetro sort está presente
        if (sort != null && !sort.isEmpty()) {
            // Dividir el sort en columna y dirección
            String[] sortParams = sort.split(",");
            String column = sortParams[0];
            Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            pageable = PageRequest.of(page, size, Sort.by(direction, column));
        } else {
            // Solo paginación si no hay sort
            pageable = PageRequest.of(page, size);
        }
        return ResponseEntity.ok(entidadesTiposServiceImpl.findAllByEmpresa(tipoCodigo, descripcion, pageable, idEmpresa));
    }

}
