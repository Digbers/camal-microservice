package com.microservice.inventario.controller;

import com.microservice.inventario.controller.DTO.UnidadesDTO;
import com.microservice.inventario.service.unidades.UnidadesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventario/unidades")
public class UnidadesController {
    private final UnidadesService unidadesService;

    @GetMapping("/find-by-id-empresa/{idEmpresa}")
    public ResponseEntity<List<UnidadesDTO>> findByIdEmpresa(@PathVariable Long idEmpresa){
        return ResponseEntity.ok(unidadesService.findByIdEmpresa(idEmpresa));
    }
    @GetMapping("/findAll/{idEmpresa}")
    public ResponseEntity<Page<UnidadesDTO>> findAll(@PathVariable Long idEmpresa,
                                                           @RequestParam(name = "codigo", required = false) String codigo,
                                                           @RequestParam(name = "nombre", required = false) String nombre,
                                                           @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                           @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                                     @RequestParam(required = false) String sort // Parámetro sort opcional
    ) {
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
        return ResponseEntity.ok(unidadesService.findAllByEmpresa(codigo, nombre, pageable, idEmpresa));
    }
}
