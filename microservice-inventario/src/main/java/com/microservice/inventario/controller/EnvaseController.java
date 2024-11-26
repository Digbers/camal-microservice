package com.microservice.inventario.controller;

import com.microservice.inventario.controller.DTO.EnvaseDTO;
import com.microservice.inventario.service.envases.EnvaseService;
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
@RequestMapping("/api/inventario/envases")
public class EnvaseController {

    private final EnvaseService envaseService;
    @GetMapping("/save")
    public ResponseEntity<EnvaseDTO> save(@RequestBody EnvaseDTO envaseRequest){
        return ResponseEntity.ok(envaseService.save(envaseRequest));
    }
    @PatchMapping("/update/{id}")
    public ResponseEntity<EnvaseDTO> update(@PathVariable Long id, @RequestBody EnvaseDTO envaseRequest){
        return ResponseEntity.ok(envaseService.update(id, envaseRequest));
    }
    @GetMapping("/all/{idEmpresa}")
    public ResponseEntity<List<EnvaseDTO>> findAll(@PathVariable Long idEmpresa){
        return ResponseEntity.ok(envaseService.findByIdEmpresa(idEmpresa));
    }
    @GetMapping("/findAll/{idEmpresa}")
    public ResponseEntity<Page<EnvaseDTO>> getAll(
            @PathVariable Long idEmpresa,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Long idEnvase,
            @RequestParam(required = false) String tipoEnvase,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Integer capacidad,
            @RequestParam(required = false) Double pesoReferencia,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String sort // Parámetro sort opcional
    ){
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
        return ResponseEntity.ok(envaseService.findAll(idEnvase,tipoEnvase, descripcion, capacidad, pesoReferencia, estado, pageable, idEmpresa));
    }
    @GetMapping("/find-by-id-almacen/{idAlmacen}")
    public ResponseEntity<List<EnvaseDTO>> findByIdAlmacen(@PathVariable Long idAlmacen){
        return ResponseEntity.ok(envaseService.findByIdAlmacen(idAlmacen));
    }

}
