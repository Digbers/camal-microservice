package com.microservice.inventario.controller;

import com.microservice.inventario.controller.DTO.*;
import com.microservice.inventario.service.movimientos.MovimientosService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/inventario/movimientos")
@RequiredArgsConstructor
public class MovimientosController {
    private final MovimientosService movimientosService;
    @GetMapping("/list")
    public Page<MovimientosCabeceraDTO> getAll(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long idEmpresa,
            @RequestParam(required = false) Long numero,
            @RequestParam(required = false) LocalDate fechaEmision,
            @RequestParam(required = false) BigDecimal total,
            @RequestParam(required = false) String motivoCodigo,
            @RequestParam(required = false) String idUsuario,
            @RequestParam(required = false) String monedaCodigo,
            @RequestParam(required = false) String sort // Parámetro sort opcional
    ){
        Pageable pageable;

        // Verificar si el parámetro sort está presente
        if (sort != null && !sort.isEmpty()) {
            // Dividir el sort en columna y dirección
            String[] sortParams = sort.split(",");
            String column = sortParams[0];
            Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            pageable = PageRequest.of(page, size, Sort.by(direction, column));
        } else {
            // Solo paginación si no hay sort
            pageable = PageRequest.of(page, size);
        }
        return movimientosService.finAll(id, idEmpresa, numero, fechaEmision, total, motivoCodigo, idUsuario, monedaCodigo, pageable);
    }
    @GetMapping("/find-detalle/{id}")
    public ResponseEntity<List<MovimientosDetalleDTO>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(movimientosService.findByIdMovimiento(id));
    }
    @GetMapping("/motivos/find")
    public ResponseEntity<List<MovimientosMotivosDTO>> findAll() {
        return ResponseEntity.ok(movimientosService.findAllMotivos());
    }
    @GetMapping("/estados/find")
    public ResponseEntity<List<MovimientosEstadosDTO>> findAllEstados() {
        return ResponseEntity.ok(movimientosService.findAllEstados());
    }
}
