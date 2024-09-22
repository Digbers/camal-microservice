package com.microservice.empresas.controller;

import com.microservice.empresas.controller.dto.EntidadDTO;
import com.microservice.empresas.persistence.entity.EntidadEntity;
import com.microservice.empresas.service.IEntidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/entidades")
public class EntidadController {
    @Autowired
    private IEntidadService entidadService;

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(entidadService.findAll());
    }
    @GetMapping("/find/{id}")
    public ResponseEntity<EntidadEntity> findById(@PathVariable Long id) {
        Optional<EntidadEntity> entidad = entidadService.findById(id);
        return entidad.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping("/save")
    public ResponseEntity<EntidadEntity> save(@RequestBody EntidadDTO entidad) {
        return ResponseEntity.ok(entidadService.save(entidad));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<EntidadEntity> delete(@PathVariable Long id) {
        Optional<EntidadEntity> entidad = entidadService.findById(id);
        if (entidad.isPresent()) {
            entidadService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<EntidadEntity> update(@PathVariable Long id, @RequestBody EntidadDTO entidad) {
        return ResponseEntity.ok(entidadService.update(id,entidad));
    }

}
