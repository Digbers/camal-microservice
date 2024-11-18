package com.microservice.empresas.controller;

import com.microservice.empresas.controller.dto.EntidadDTO;
import com.microservice.empresas.request.CreateEntidadRequest;
import com.microservice.empresas.request.ReniectRequest;
import com.microservice.empresas.response.EntidadResponse;
import com.microservice.empresas.response.IdsEntidades;
import com.microservice.empresas.service.EntidadesSService;
import com.microservice.empresas.service.IEntidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/empresas/entidades")
@RequiredArgsConstructor
public class EntidadController {

    private final IEntidadService entidadService;
    private final EntidadesSService entidadesSService;
    //solo por ruc para el momento
    @GetMapping("/find-sunat/{tipodoc}/{numero}/{empresa}")
    public ResponseEntity<EntidadResponse> findEntidad(@PathVariable String tipodoc, @PathVariable String numero, @PathVariable Long empresa) {
        return ResponseEntity.ok(entidadesSService.findByNumeroDocTipoDocumento(numero, tipodoc, empresa));
    }
    //buqueda por dni
    //no esta guardando  en entidades x tipos
    @PostMapping("/find-entidad/{empresa}")
    public ResponseEntity<EntidadResponse> findEntidadReniec(@PathVariable Long empresa, @RequestBody ReniectRequest reniecRecuest){
        return ResponseEntity.ok(entidadesSService.findEntidadesByDNI(reniecRecuest, empresa));
    }
    @GetMapping("/findAll/{idEmpresa}")
    public ResponseEntity<Page<EntidadDTO>> findAll(@PathVariable Long idEmpresa,
                                                                     @RequestParam(name = "nombre", required = false) String nombre,
                                                                     @RequestParam(name = "apellidoPaterno", required = false) String apellidoPaterno,
                                                                     @RequestParam(name = "apellidoMaterno", required = false) String apellidoMaterno,
                                                                     @RequestParam(name = "documentoTipo", required = false) String codigo,
                                                                     @RequestParam(name = "nroDocumento", required = false) String nroDocumento,
                                                                     @RequestParam(name = "email", required = false) String email,
                                                                     @RequestParam(name = "celular", required = false) String celular,
                                                                     @RequestParam(name = "direccion", required = false) String direccion,
                                                                     @RequestParam(name = "sexo", required = false) String sexo,
                                                                     @RequestParam(name = "estado", required = false) Boolean estado,
                                                                     @RequestParam(name = "condicion", required = false) String condicion,
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
        return ResponseEntity.ok(entidadService.findAllByEmpresa(nombre, apellidoPaterno, apellidoMaterno, codigo, nroDocumento, email, celular, direccion, sexo, estado, condicion, pageable, idEmpresa));
    }
    @GetMapping("/find/{id}")
    public ResponseEntity<EntidadDTO> findById(@PathVariable Long id) {
        Optional<EntidadDTO> entidad = entidadService.findById(id);
        return entidad.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping("/save")
    public ResponseEntity<EntidadResponse> save(@RequestBody CreateEntidadRequest entidad) {
        return ResponseEntity.ok(entidadService.save(entidad));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<EntidadDTO> delete(@PathVariable Long id) {
        Optional<EntidadDTO> entidad = entidadService.findById(id);
        if (entidad.isPresent()) {
            entidadService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/autocomplete-nro-documento/{nroDocumento}")
    public ResponseEntity<List<EntidadResponse>> autocompleteNroDocumento(@PathVariable String nroDocumento) {
        return ResponseEntity.ok(entidadService.autocompleteNroDocumento(nroDocumento));
    }
    @GetMapping("/autocomplete-nombre/{nombre}")
    public ResponseEntity<List<EntidadResponse>> autocompleteNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(entidadService.autocompleteNombre(nombre));
    }
    @GetMapping("/search")
    public ResponseEntity<IdsEntidades> search(
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "nroDocumento", required = false) String nroDocumento) {
        return ResponseEntity.ok(entidadService.search(nombre, nroDocumento));
    }
    @GetMapping("/clientes-batch")
    public ResponseEntity<List<EntidadResponse>> clientesBatch(@RequestParam(name = "ids") List<Long> ids) {
        // Llama al servicio que obtiene los clientes en lote
        List<EntidadResponse> entidades = entidadService.findEntidadesByIds(ids);
        return ResponseEntity.ok(entidades);
    }

}
