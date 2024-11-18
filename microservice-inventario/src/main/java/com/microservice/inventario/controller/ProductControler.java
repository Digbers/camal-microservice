package com.microservice.inventario.controller;

import com.microservice.inventario.controller.DTO.ProductoDTO;
import com.microservice.inventario.controller.DTO.response.ProductoAResponse;
import com.microservice.inventario.persistence.entity.ProductosEntity;
import com.microservice.inventario.service.productos.ProductosServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("api/inventario/productos")
@RequiredArgsConstructor
public class ProductControler {
    private final ProductosServiceImpl productosServiceImpl;

    @GetMapping("/list")
    public Page<ProductoDTO> getAll(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long idEmpresa,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String codigoTipo,
            @RequestParam(required = false) Long almacenId,
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
        return productosServiceImpl.finAll(id, idEmpresa, codigo, nombre, codigoTipo, almacenId, pageable);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductoDTO>> getAll(){
        return ResponseEntity.ok(productosServiceImpl.findAll());
    }
    @PostMapping("/save")
    public ResponseEntity<ProductoDTO> save(@RequestBody @Valid ProductoDTO pruductoRequest){
        return ResponseEntity.ok(productosServiceImpl.save(pruductoRequest));
    }
    @PatchMapping("/update/{id}")
    public ResponseEntity<ProductoDTO> update(@PathVariable Long id, @RequestBody @Valid ProductoDTO pruductoRequest){
        return ResponseEntity.ok(productosServiceImpl.update(id, pruductoRequest));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Mono<ProductosEntity>> delete(@PathVariable Long id){
        Boolean b = productosServiceImpl.deleteById(id);
        if(b){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/find-autocomplete/{descripcion}")
    public ResponseEntity<List<ProductoAResponse>> findByDescripcionAutocomplete(@PathVariable String descripcion) {
        List<ProductoAResponse> productos = productosServiceImpl.findByDescripcionAutocomplete(descripcion);
        return ResponseEntity.ok(productos);
    }
    @GetMapping("/find-test/{param}")
    public ResponseEntity<?> findStockMinimum(@PathVariable String param) {
        System.out.println("Find Stock Minimum: " + param);
        return ResponseEntity.ok(param);
    }

}
