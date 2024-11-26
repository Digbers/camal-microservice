package com.microservice.inventario.controller;
import com.microservice.inventario.controller.DTO.PuntoVentaDTO;
import com.microservice.inventario.controller.DTO.UnidadesDTO;
import com.microservice.inventario.controller.DTO.response.DatosGeneralesResponse;
import com.microservice.inventario.service.puntoVentaI.PuntoVentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/inventario/punto-venta")
@RequiredArgsConstructor
public class PuntoVentaController {
    private final PuntoVentaService puntoVentaService;

    @PostMapping("/save")
    public ResponseEntity<PuntoVentaDTO> save(@RequestBody PuntoVentaDTO puntoVentaDTO) {
        return ResponseEntity.ok(puntoVentaService.save(puntoVentaDTO));
    }
    @PatchMapping("/update/{id}")
    public ResponseEntity<PuntoVentaDTO> update(@PathVariable Long id, @RequestBody PuntoVentaDTO puntoVentaDTO) {
        return ResponseEntity.ok(puntoVentaService.update(id, puntoVentaDTO));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        puntoVentaService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/findAll/{idEmpresa}")
    public ResponseEntity<Page<PuntoVentaDTO>> findAll(@PathVariable Long idEmpresa,
                                                     @RequestParam(name = "direccion", required = false) String direccion,
                                                     @RequestParam(name = "nombre", required = false) String nombre,
                                                     @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                     @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return ResponseEntity.ok(puntoVentaService.findAllByEmpresa(direccion, nombre, PageRequest.of(page, size), idEmpresa));
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<PuntoVentaDTO> findById(@PathVariable Long id) {
        Optional<PuntoVentaDTO> puntoVenta = puntoVentaService.findById(id);
        if(puntoVenta.isPresent()){
            return ResponseEntity.ok(puntoVenta.get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/find/{idEmpresa}/{idAlmacen}/{puntoVenta}")
    ResponseEntity<DatosGeneralesResponse> findByIdEmpresaAlmacenPuntoVenta(@PathVariable Long idEmpresa, @PathVariable Long idAlmacen, @PathVariable Long puntoVenta) {
        DatosGeneralesResponse datosGeneralesResponse = puntoVentaService.findDatosGenerales(idEmpresa, idAlmacen, puntoVenta);
        return ResponseEntity.ok(datosGeneralesResponse);
    }

    @GetMapping("/find/almacen/{id}")
    public ResponseEntity<List<PuntoVentaDTO>> findByIdAlmacen(@PathVariable Long id) {
        List<PuntoVentaDTO> puntoVenta = puntoVentaService.findAllByIdAlmacen(id);
        return ResponseEntity.ok(puntoVenta);

    }

}
