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
