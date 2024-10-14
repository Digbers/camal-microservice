package com.microservice.inventario.controller;

import com.microservice.inventario.controller.DTO.ProductoDTO;
import com.microservice.inventario.persistence.entity.ProductosEntity;
import com.microservice.inventario.service.productos.ProductosServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
    //private final PagedResourcesAssembler<ProductoDTO> pagedResourcesAssembler;
    @GetMapping("/list")
    public Page<ProductoDTO> getAll(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long idEmpresa,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String codigoTipo,
            @RequestParam(required = false) Long almacenId
    ){
        return productosServiceImpl.finAll(id, idEmpresa, codigo, nombre, codigoTipo, almacenId, PageRequest.of(page, size));
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
    }/*
    @GetMapping("/verificar-stock/{id}")
    public ResponseEntity<Integer> verificarStock(@PathVariable Long id) {
        Integer stockDisponible = productosServiceImpl.obtenerStockDisponible(id);
        return ResponseEntity.ok(stockDisponible);
    }*/
    @GetMapping("/find-autocomplete/{descripcion}")
    public ResponseEntity<List<ProductoDTO>> findByDescripcionAutocomplete(@PathVariable String descripcion) {
        List<ProductoDTO> productos = productosServiceImpl.findByDescripcionAutocomplete(descripcion);
        return ResponseEntity.ok(productos);
    }
    @GetMapping("/find-test/{param}")
    public ResponseEntity<?> findStockMinimum(@PathVariable String param) {
        System.out.println("Find Stock Minimum: " + param);
        return ResponseEntity.ok(param);
    }

}
