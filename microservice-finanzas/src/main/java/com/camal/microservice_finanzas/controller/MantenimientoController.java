package com.camal.microservice_finanzas.controller;

import com.camal.microservice_finanzas.controller.DTO.FormasDeCobrosDTO;
import com.camal.microservice_finanzas.controller.DTO.FormasPagosDTO;
import com.camal.microservice_finanzas.controller.DTO.MonedasDTO;
import com.camal.microservice_finanzas.service.mantenimiento.FormasCobrosServiceImpl;
import com.camal.microservice_finanzas.service.mantenimiento.FormasPagosServiceImlp;
import com.camal.microservice_finanzas.service.mantenimiento.MonedasServiseImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/finanzas")
public class MantenimientoController {
    @Autowired
    private MonedasServiseImpl monedasServiseImpl;
    @Autowired
    private FormasPagosServiceImlp formasPagosServiceImlp;
    @Autowired
    private FormasCobrosServiceImpl formasCobrosServiceImpl;

    @GetMapping("/monedas")
    public ResponseEntity<List<MonedasDTO>> findAll() {
        return ResponseEntity.ok(monedasServiseImpl.findAll());
    }
    @GetMapping("/monedas/{id}")
    public ResponseEntity<MonedasDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(monedasServiseImpl.findById(id));
    }
    @GetMapping("/monedas/empresa/{idEmpresa}")
    public ResponseEntity<List<MonedasDTO>> findByIdEmpresa(@PathVariable Long idEmpresa) {
        return ResponseEntity.ok(monedasServiseImpl.findByIdEmpresa(idEmpresa));
    }
    @PatchMapping("/monedas/update/{id}")
    public ResponseEntity<MonedasDTO> update(@PathVariable String id, @RequestBody MonedasDTO monedasDTO) {
        return ResponseEntity.ok(monedasServiseImpl.update(id, monedasDTO));
    }
    @DeleteMapping("/monedas/delete/{id}")
    public ResponseEntity<?> deleteMonedas(@PathVariable String id) {
        Boolean b = monedasServiseImpl.deleteById(id);
        if(b){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/monedas/save")
    public ResponseEntity<MonedasDTO> save(@RequestBody MonedasDTO monedasDTO) {
        return ResponseEntity.ok(monedasServiseImpl.save(monedasDTO));
    }
    /*cobros*/
    @GetMapping("/cobros")
    public ResponseEntity<List<FormasDeCobrosDTO>> findAllCobros() {
        return ResponseEntity.ok(formasCobrosServiceImpl.findAll());
    }
    @GetMapping("/cobros/{id}")
    public ResponseEntity<FormasDeCobrosDTO> findByIdCobros(@PathVariable String id) {
        return ResponseEntity.ok(formasCobrosServiceImpl.findById(id));
    }
    @GetMapping("/cobros/empresa/{idEmpresa}")
    public ResponseEntity<List<FormasDeCobrosDTO>> findByIdEmpresaCobros(@PathVariable Long idEmpresa) {
        return ResponseEntity.ok(formasCobrosServiceImpl.findByIdEmpresa(idEmpresa));
    }
    @PatchMapping("/cobros/update/{id}")
    public ResponseEntity<FormasDeCobrosDTO> update(@PathVariable String id, @RequestBody FormasDeCobrosDTO formasDeCobrosDTO) {
        return ResponseEntity.ok(formasCobrosServiceImpl.update(id, formasDeCobrosDTO));
    }
    @DeleteMapping("/cobros/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        Boolean b = formasCobrosServiceImpl.deleteById(id);
        if(b){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/cobros/save")
    public ResponseEntity<FormasDeCobrosDTO> saveCobros(@RequestBody FormasDeCobrosDTO formasDeCobrosDTO) {
        return ResponseEntity.ok(formasCobrosServiceImpl.save(formasDeCobrosDTO));
    }
    /*Pagos*/
    @GetMapping("/pagos")
    public ResponseEntity<List<FormasPagosDTO>> findAllPagos() {
        return ResponseEntity.ok(formasPagosServiceImlp.findAll());
    }
    @GetMapping("/pagos/{id}")
    public ResponseEntity<FormasPagosDTO> findByIdPagos(@PathVariable String id) {
        return ResponseEntity.ok(formasPagosServiceImlp.findById(id));
    }
    @GetMapping("/pagos/empresa/{idEmpresa}")
    public ResponseEntity<List<FormasPagosDTO>> findByIdEmpresaPagos(@PathVariable Long idEmpresa) {
        return ResponseEntity.ok(formasPagosServiceImlp.findByIdEmpresa(idEmpresa));
    }
    @PatchMapping("/pagos/update/{id}")
    public ResponseEntity<FormasPagosDTO> update(@PathVariable String id, @RequestBody FormasPagosDTO formasPagosDTO) {
        return ResponseEntity.ok(formasPagosServiceImlp.update(id, formasPagosDTO));
    }
    @DeleteMapping("/pagos/delete/{id}")
    public ResponseEntity<?> deletePagos(@PathVariable String id) {
        Boolean b = formasPagosServiceImlp.deleteById(id);
        if(b){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/pagos/save")
    public ResponseEntity<FormasPagosDTO> savePagos(@RequestBody FormasPagosDTO formasPagosDTO) {
        return ResponseEntity.ok(formasPagosServiceImlp.save(formasPagosDTO));
    }
}
