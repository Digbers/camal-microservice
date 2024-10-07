package com.microservice.inventario.controller;

import com.microservice.inventario.controller.DTO.PuntoVentaDTO;
import com.microservice.inventario.service.puntoVentaI.PuntoVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/inventario/punto-venta")
public class PuntoVentaController {
    @Autowired
    private PuntoVentaService puntoVentaService;
    @GetMapping("/list")
    public Page<PuntoVentaDTO> findAll(Pageable pageable) {
        return puntoVentaService.findAll(pageable);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<PuntoVentaDTO> findById(@PathVariable Long id) {
        Optional<PuntoVentaDTO> puntoVenta = puntoVentaService.findById(id);
        if(puntoVenta.isPresent()){
            return ResponseEntity.ok(puntoVenta.get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/find/almacen/{id}")
    public ResponseEntity<List<PuntoVentaDTO>> findByIdAlmacen(@PathVariable Long id) {
        List<PuntoVentaDTO> puntoVenta = puntoVentaService.findAllByIdAlmacen(id);
        return ResponseEntity.ok(puntoVenta);

    }

}
