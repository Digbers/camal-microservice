package com.microservice.ventas.controller;

import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasTiposDTO;
import com.microservice.ventas.controller.DTO.ventas.ComprobantesTiposVentasDTO;
import com.microservice.ventas.service.compras.ComprobantesComprasTiposService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compras/tipos")
@RequiredArgsConstructor
public class ComprobantesComprasTiposController {
    private final ComprobantesComprasTiposService comprobantesComprasTiposService;

    @GetMapping("/findAll/{idEmpresa}")
    public ResponseEntity<Page<ComprobantesComprasTiposDTO>> findAll(@PathVariable Long idEmpresa,
                                                                     @RequestParam(name = "codigo", required = false) String codigo,
                                                                     @RequestParam(name = "descripcion", required = false) String descripcion,
                                                                     @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                                     @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                                                     @RequestParam(required = false) String sort){
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

        return ResponseEntity.ok(comprobantesComprasTiposService.findAllByEmpresa(codigo, descripcion, pageable, idEmpresa));
    }
}
