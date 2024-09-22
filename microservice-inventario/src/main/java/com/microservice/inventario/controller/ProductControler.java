package com.microservice.inventario.controller;

import com.microservice.inventario.controller.DTO.ProductoDTO;
import com.microservice.inventario.persistence.entity.ProductosEntity;
import com.microservice.inventario.service.productos.ProductosServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/inventario")
public class ProductControler {
    @Autowired
    private ProductosServiceImpl productosServiceImpl;
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
    @GetMapping("/verificar-stock/{id}")
    public ResponseEntity<Integer> verificarStock(@PathVariable Long id) {
        Integer stockDisponible = productosServiceImpl.obtenerStockDisponible(id);
        return ResponseEntity.ok(stockDisponible);
    }

}
