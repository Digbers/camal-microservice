package com.microservice.empresas.controller;

import com.microservice.empresas.controller.dto.EmpresaDTO;
import com.microservice.empresas.persistence.entity.EmpresaEntity;
import com.microservice.empresas.service.IEmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    @Autowired
    private IEmpresaService empresaService;
    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(empresaService.findAll());
    }
    @PostMapping("/save")
    public ResponseEntity<EmpresaEntity> save(@RequestBody EmpresaDTO empresaDTO) {
        return ResponseEntity.ok(empresaService.save(empresaDTO));
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<EmpresaEntity> findById(@PathVariable Long id) {
        Optional<EmpresaEntity> empresa = empresaService.findById(id);
        if(!empresa.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(empresa.get());
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<EmpresaEntity> update(@PathVariable Long id, @RequestBody EmpresaDTO empresaDTO) {
        EmpresaEntity updatedEntidad = empresaService.update(id, empresaDTO);
        return ResponseEntity.ok(updatedEntidad);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<EmpresaEntity> empresa = empresaService.findById(id);
        if (empresa.isPresent()) {
            empresaService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}