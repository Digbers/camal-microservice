package com.microservice.empresas.controller;

import com.microservice.empresas.controller.dto.ZonasDTO;
import com.microservice.empresas.persistence.entity.ZonasEntity;
import com.microservice.empresas.service.IZonasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/empresas/zonas")
public class ZonasController {
    @Autowired
    private IZonasService zonasService;
    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(zonasService.findAll());
    }
    @PostMapping("/save")
    public ResponseEntity<ZonasEntity> save(@RequestBody ZonasDTO zonasDTO) {
        return ResponseEntity.ok(zonasService.save(zonasDTO));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<ZonasEntity> update(@PathVariable Long id, @RequestBody ZonasDTO zonasDTO) {
        return ResponseEntity.ok(zonasService.update(id, zonasDTO));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ZonasEntity> delete(@PathVariable Long id) {
        Optional<ZonasEntity> zona = zonasService.findById(id);
        if (zona.isPresent()) {
            zonasService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
