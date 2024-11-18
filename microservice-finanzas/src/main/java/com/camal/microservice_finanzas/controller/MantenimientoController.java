package com.camal.microservice_finanzas.controller;

import com.camal.microservice_finanzas.controller.DTO.FormasDeCobrosDTO;
import com.camal.microservice_finanzas.controller.DTO.FormasPagosDTO;
import com.camal.microservice_finanzas.controller.DTO.MonedasDTO;
import com.camal.microservice_finanzas.service.mantenimiento.FormasCobrosServiceImpl;
import com.camal.microservice_finanzas.service.mantenimiento.FormasPagosServiceImlp;
import com.camal.microservice_finanzas.service.mantenimiento.MonedasServiseImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/finanzas")
@RequiredArgsConstructor
public class MantenimientoController {
    private final MonedasServiseImpl monedasServiseImpl;
    private final FormasPagosServiceImlp formasPagosServiceImlp;
    private final FormasCobrosServiceImpl formasCobrosServiceImpl;

    @GetMapping("/monedas")
    public ResponseEntity<List<MonedasDTO>> findAll() {
        return ResponseEntity.ok(monedasServiseImpl.findAll());
    }
    @GetMapping("monedas/findAll/{idEmpresa}")
    public ResponseEntity<Page<MonedasDTO>> findAll( @PathVariable Long idEmpresa,
            @RequestParam(name = "codigo", required = false) String codigo,
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "simbolo", required = false) String simbolo,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return ResponseEntity.ok(monedasServiseImpl.findAll(codigo, nombre, simbolo, PageRequest.of(page, size), idEmpresa));
    }
    @GetMapping("/monedas/{id}")
    public ResponseEntity<MonedasDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(monedasServiseImpl.findById(id));
    }
    @GetMapping("/monedas/find-by-empresa/{idEmpresa}")
    public ResponseEntity<List<MonedasDTO>> findByIdEmpresa(@PathVariable Long idEmpresa) {
        return ResponseEntity.ok(monedasServiseImpl.findByIdEmpresa(idEmpresa));
    }
    @PatchMapping("/monedas/update/{id}")
    public ResponseEntity<MonedasDTO> update(@PathVariable Long id, @RequestBody MonedasDTO monedasDTO) {
        return ResponseEntity.ok(monedasServiseImpl.update(id, monedasDTO));
    }
    @DeleteMapping("/monedas/delete/{id}")
    public ResponseEntity<?> deleteMonedas(@PathVariable Long id) {
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
    @GetMapping("/metodos-cobros")
    public ResponseEntity<List<FormasDeCobrosDTO>> findAllCobros() {
        return ResponseEntity.ok(formasCobrosServiceImpl.findAll());
    }
    @GetMapping("/metodos-cobros/{id}")
    public ResponseEntity<FormasDeCobrosDTO> findByIdCobros(@PathVariable Long id) {
        return ResponseEntity.ok(formasCobrosServiceImpl.findById(id));
    }
    @GetMapping("/metodos-cobros/find-by-empresa/{idEmpresa}")
    public ResponseEntity<List<FormasDeCobrosDTO>> findByIdEmpresaCobros(@PathVariable Long idEmpresa) {
        return ResponseEntity.ok(formasCobrosServiceImpl.findByIdEmpresa(idEmpresa));
    }
    @PatchMapping("/metodos-cobros/update/{id}")
    public ResponseEntity<FormasDeCobrosDTO> update(@PathVariable Long id, @RequestBody FormasDeCobrosDTO formasDeCobrosDTO) {
        return ResponseEntity.ok(formasCobrosServiceImpl.update(id, formasDeCobrosDTO));
    }
    @DeleteMapping("/metodos-cobros/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Boolean b = formasCobrosServiceImpl.deleteById(id);
        if(b){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/metodos-cobros/save")
    public ResponseEntity<FormasDeCobrosDTO> saveCobros(@RequestBody FormasDeCobrosDTO formasDeCobrosDTO) {
        return ResponseEntity.ok(formasCobrosServiceImpl.save(formasDeCobrosDTO));
    }
    /*Pagos*/
    @GetMapping("/metodos-pagos")
    public ResponseEntity<List<FormasPagosDTO>> findAllPagos() {
        return ResponseEntity.ok(formasPagosServiceImlp.findAll());
    }
    @GetMapping("/metodos-pagos/{id}")
    public ResponseEntity<FormasPagosDTO> findByIdPagos(@PathVariable Long id) {
        return ResponseEntity.ok(formasPagosServiceImlp.findById(id));
    }
    @GetMapping("/metodos-pagos/find-by-empresa/{idEmpresa}")
    public ResponseEntity<List<FormasPagosDTO>> findByIdEmpresaPagos(@PathVariable Long idEmpresa) {
        return ResponseEntity.ok(formasPagosServiceImlp.findByIdEmpresa(idEmpresa));
    }
    @PatchMapping("/metodos-pagos/update/{id}")
    public ResponseEntity<FormasPagosDTO> update(@PathVariable Long id, @RequestBody FormasPagosDTO formasPagosDTO) {
        return ResponseEntity.ok(formasPagosServiceImlp.update(id, formasPagosDTO));
    }
    @DeleteMapping("/metodos-pagos/delete/{id}")
    public ResponseEntity<?> deletePagos(@PathVariable Long id) {
        Boolean b = formasPagosServiceImlp.deleteById(id);
        if(b){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/metodos-pagos/save")
    public ResponseEntity<FormasPagosDTO> savePagos(@RequestBody FormasPagosDTO formasPagosDTO) {
        return ResponseEntity.ok(formasPagosServiceImlp.save(formasPagosDTO));
    }
}
