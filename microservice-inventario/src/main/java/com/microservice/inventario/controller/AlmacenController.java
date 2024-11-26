package com.microservice.inventario.controller;

import com.microservice.inventario.controller.DTO.AlmacenDTO;
import com.microservice.inventario.controller.DTO.AlmacenTipoDTO;
import com.microservice.inventario.controller.DTO.request.ConvertirProductoRequest;
import com.microservice.inventario.service.almacenI.AlmacenService;
import com.microservice.inventario.service.almacenI.AlmacenesTiposService;
import com.microservice.inventario.service.almacenI.StockAlmacenService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/inventario/almacenes")
@RequiredArgsConstructor
public class AlmacenController {
    private final AlmacenService almacenService;
    private final StockAlmacenService stockAlmacenService;
    private final AlmacenesTiposService almacenTipoService;

    @PostMapping("/trasformar-producto")
    public ResponseEntity<Boolean> trasfomarProducto(@RequestBody ConvertirProductoRequest productoRequest) {
        return ResponseEntity.ok(stockAlmacenService.convertirProducto(productoRequest));
    }
    @GetMapping("/findTipos/{idEmpresa}")
    public ResponseEntity<List<AlmacenTipoDTO>> findTipos(@PathVariable Long idEmpresa) {
        return ResponseEntity.ok(almacenTipoService.findByIdEmpresa(idEmpresa));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAlmacen(@PathVariable Long id) {
        Boolean b = almacenService.deleteById(id);
        if(b){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/findByEmpresa/{idEmpresa}")
    public ResponseEntity<List<AlmacenDTO>> findByEmpresa(@PathVariable Long idEmpresa) {
        return ResponseEntity.ok(almacenService.findByIdEmpresa(idEmpresa));
    }

    @GetMapping("/findAll/{idEmpresa}")
    public ResponseEntity<Page<AlmacenDTO>> findAll(@PathVariable Long idEmpresa,
        @RequestParam(name = "nombre", required = false) String nombre,
        @RequestParam(name = "tipoAlmacen", required = false) String tipoAlmacen,
        @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
        @RequestParam(required = false) String sort
    ) {
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
        return ResponseEntity.ok(almacenService.findAllByEmpresa(nombre, tipoAlmacen, pageable, idEmpresa));
    }
    @GetMapping("/find/{id}")
    public ResponseEntity<AlmacenDTO> findById(@PathVariable Long id) {
        Optional<AlmacenDTO> almacen = almacenService.findById(id);
        if(almacen.isPresent()){
            return ResponseEntity.ok(almacen.get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/find/empresa/{id}")
    public ResponseEntity<List<AlmacenDTO>> findByIdEmpresa(@PathVariable Long id) {
        List<AlmacenDTO> almacenes = almacenService.findByIdEmpresa(id);
        return ResponseEntity.ok(almacenes);
    }
    @PostMapping("/save")
    public ResponseEntity<AlmacenDTO> save(@RequestBody AlmacenDTO almacenDTO) {
        return ResponseEntity.ok(almacenService.save(almacenDTO));
    }
    @PatchMapping("/update/{id}")
    public ResponseEntity<AlmacenDTO> update(@PathVariable Long id, @RequestBody AlmacenDTO almacenDTO) {
        return ResponseEntity.ok(almacenService.update(id, almacenDTO));
    }
}

