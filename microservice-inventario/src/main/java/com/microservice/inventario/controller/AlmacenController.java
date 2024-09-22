package com.microservice.inventario.controller;

import com.microservice.inventario.controller.DTO.AlmacenDTO;
import com.microservice.inventario.service.almacenI.AlmacenService;
import org.modelmapper.ModelMapper;
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
@RequestMapping("/inventario/almacenes")
public class AlmacenController {
    @Autowired
    private AlmacenService almacenService;
    @Autowired
    private ModelMapper modelMapper;

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

