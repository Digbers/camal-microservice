package com.microservice.empresas.controller;

import com.microservice.empresas.controller.dto.EntidadDTO;
import com.microservice.empresas.controller.dto.PadronSunatDTO;
import com.microservice.empresas.persistence.entity.EntidadEntity;
import com.microservice.empresas.request.EntidadRequest;
import com.microservice.empresas.request.ReniectRequest;
import com.microservice.empresas.service.EntidadesSService;
import com.microservice.empresas.service.IEntidadService;
import com.microservice.empresas.service.WebScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/empresas/entidades")
@RequiredArgsConstructor
public class EntidadController {

    private final IEntidadService entidadService;
    private final EntidadesSService entidadesSService;
    //solo por ruc para el momento
    @GetMapping("/find-sunat/{tipodoc}/{numero}")
    public ResponseEntity<PadronSunatDTO> findEntidad(@PathVariable String tipodoc, @PathVariable String numero) {
        return ResponseEntity.ok(entidadesSService.findByNumeroDocTipoDocumento(numero, tipodoc));
    }
    @GetMapping("/find-entidad/{empresa}")
    public ResponseEntity<EntidadRequest> findEntidadReniec(@PathVariable Long empresa, @RequestBody ReniectRequest reniecRecuest){
        return ResponseEntity.ok(entidadesSService.findEntidadesByDNI(reniecRecuest, empresa));
    }
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
