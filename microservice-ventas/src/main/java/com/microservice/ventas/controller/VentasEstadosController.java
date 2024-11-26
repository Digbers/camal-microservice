package com.microservice.ventas.controller;

import com.microservice.ventas.controller.DTO.ventas.ComprobantesVentasEstadoDTO;
import com.microservice.ventas.service.ventas.VentasEstadosServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas/estados")
@RequiredArgsConstructor
public class VentasEstadosController {
    private final VentasEstadosServiceImpl ventasEstadosService;

    @PostMapping("/save")
    public ResponseEntity<ComprobantesVentasEstadoDTO> save(@RequestBody ComprobantesVentasEstadoDTO comprobantesVentasEstadoDTO) {
        return ResponseEntity.ok(ventasEstadosService.save(comprobantesVentasEstadoDTO));
    }
    @PatchMapping("/update/{id}")
    public ResponseEntity<ComprobantesVentasEstadoDTO> update(@PathVariable Long id, @RequestBody ComprobantesVentasEstadoDTO comprobantesVentasEstadoDTO) {
        return ResponseEntity.ok(ventasEstadosService.update(id, comprobantesVentasEstadoDTO));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ventasEstadosService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/find-by-empresa/{idEmpresa}")
    public ResponseEntity<List<ComprobantesVentasEstadoDTO>> findByIdEmpresa(@PathVariable Long idEmpresa) {
        return ResponseEntity.ok(ventasEstadosService.findByIdEmpresa(idEmpresa));
    }
    @GetMapping("/findAll/{idEmpresa}")
    public ResponseEntity<Page<ComprobantesVentasEstadoDTO>> findAll(@PathVariable Long idEmpresa,
                                                           @RequestParam(name = "codigo", required = false) String codigo,
                                                           @RequestParam(name = "descripcion", required = false) String descripcion,
                                                           @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                           @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return ResponseEntity.ok(ventasEstadosService.findAllByEmpresa(codigo, descripcion, PageRequest.of(page, size), idEmpresa));
    }
}
