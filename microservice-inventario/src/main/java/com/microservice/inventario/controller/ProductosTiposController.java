package com.microservice.inventario.controller;

import com.microservice.inventario.controller.DTO.ProductosTiposDTO;
import com.microservice.inventario.controller.DTO.UnidadesDTO;
import com.microservice.inventario.service.productos.ProductosTiposServiceImpl;
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
@RequestMapping("api/inventario/productosTipos")
public class ProductosTiposController {
    private final ProductosTiposServiceImpl productosTiposService;

    @GetMapping("/findAll/{idEmpresa}")
    public ResponseEntity<Page<ProductosTiposDTO>> findAll(@PathVariable Long idEmpresa,
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
        return ResponseEntity.ok(productosTiposService.findAllByEmpresa(codigo, nombre, pageable, idEmpresa));
    }

    @GetMapping("/all/{idEmpresa}")
    public ResponseEntity<List<ProductosTiposDTO>> findAll(@PathVariable Long idEmpresa) {
        return ResponseEntity.ok(productosTiposService.findByIdEmpresa(idEmpresa));
    }
    @PostMapping("/save")
    public ResponseEntity<ProductosTiposDTO> save(@RequestBody ProductosTiposDTO productosTiposDTO) {
        return ResponseEntity.ok(productosTiposService.save(productosTiposDTO));
    }
}
