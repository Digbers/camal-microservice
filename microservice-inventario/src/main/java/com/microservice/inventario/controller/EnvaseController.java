package com.microservice.inventario.controller;

import com.microservice.inventario.controller.DTO.EnvaseDTO;
import com.microservice.inventario.service.envases.EnvaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    @GetMapping("/all")
    public ResponseEntity<Page<EnvaseDTO>> getAll(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Long idEnvase,
            @RequestParam(required = false) Long idEmpresa,
            @RequestParam(required = false) String tipoEnvase,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Integer capacidad,
            @RequestParam(required = false) Double pesoReferencia,
            @RequestParam(required = false) String estado
    ){
        return ResponseEntity.ok(envaseService.findAll(idEnvase, idEmpresa, tipoEnvase, descripcion, capacidad, pesoReferencia, estado, PageRequest.of(page, size)));
    }
    @GetMapping("/find-by-id-almacen/{idAlmacen}")
    public ResponseEntity<List<EnvaseDTO>> findByIdAlmacen(@PathVariable Long idAlmacen){
        return ResponseEntity.ok(envaseService.findByIdAlmacen(idAlmacen));
    }

}
