package com.microservice.ventas.controller;

import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasEstadosDTO;
import com.microservice.ventas.entity.ComprobantesComprasEstadosEntity;
import com.microservice.ventas.service.compras.ComprasEstadosService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras/estados")
@RequiredArgsConstructor
public class ComprasEstadosController {
    private final ComprasEstadosService comprasEstadosService;

    @GetMapping("/find-by-empresa/{idEmpresa}")
    public ResponseEntity<List<ComprobantesComprasEstadosDTO>> findByIdEmpresa(@PathVariable Long idEmpresa) {
        return ResponseEntity.ok(comprasEstadosService.findByIdEmpresa(idEmpresa));
    }
    @PostMapping("/save")
    public ResponseEntity<ComprobantesComprasEstadosDTO> save(@RequestBody ComprobantesComprasEstadosDTO comprobantesComprasEstadosDTO) {
        return ResponseEntity.ok(comprasEstadosService.save(comprobantesComprasEstadosDTO));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        comprasEstadosService.deleteById(id);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/update/{id}")
    public ResponseEntity<ComprobantesComprasEstadosDTO> update(@PathVariable Long id, @RequestBody ComprobantesComprasEstadosDTO comprobantesComprasEstadosDTO) {
        return ResponseEntity.ok(comprasEstadosService.update(id, comprobantesComprasEstadosDTO));
    }
    @GetMapping("/findAll/{idEmpresa}")
    public ResponseEntity<Page<ComprobantesComprasEstadosDTO>> findAll(@PathVariable Long idEmpresa,
                                                                       @RequestParam(name = "codigo", required = false) String codigo,
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
        return ResponseEntity.ok(comprasEstadosService.findAllByEmpresa(codigo, descripcion, pageable, idEmpresa));
    }
}
