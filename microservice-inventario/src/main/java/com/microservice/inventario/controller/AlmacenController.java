package com.microservice.inventario.controller;

import com.microservice.inventario.controller.DTO.AlmacenDTO;
import com.microservice.inventario.controller.DTO.request.ConvertirProductoRequest;
import com.microservice.inventario.service.almacenI.AlmacenService;
import com.microservice.inventario.service.almacenI.StockAlmacenService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/inventario/almacenes")
@RequiredArgsConstructor
public class AlmacenController {
    private final AlmacenService almacenService;
    private final ModelMapper modelMapper;
    private final StockAlmacenService stockAlmacenService;

    @PostMapping("/trasformar-producto")
    public ResponseEntity<Boolean> trasfomarProducto(@RequestBody ConvertirProductoRequest productoRequest) {
        return ResponseEntity.ok(stockAlmacenService.convertirProducto(productoRequest));
    }


    @GetMapping("/list")
    public Page<AlmacenDTO> findAll(Pageable pageable) {
        return almacenService.findAll(pageable);
    }
    @GetMapping("/find/{id}")
    public ResponseEntity<AlmacenDTO> findById(@PathVariable Long id) {
        Optional<AlmacenDTO> almacen = almacenService.findById(id);
        if(almacen.isPresent()){
            return ResponseEntity.ok(almacen.get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/find/empresa/{id}")
    public ResponseEntity<List<AlmacenDTO>> findByIdEmpresa(@PathVariable Long id) {
        List<AlmacenDTO> almacenes = almacenService.findByIdEmpresa(id);
        return ResponseEntity.ok(almacenes);
    }
}

