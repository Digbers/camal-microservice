package com.microservice.empresas.controller;

import com.microservice.empresas.controller.dto.DocumentosTiposDTO;
import com.microservice.empresas.service.impl.DocumentosTiposServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/empresas/documentos")
@RequiredArgsConstructor
public class DocumentosTiposController {
    private final DocumentosTiposServiceImpl documentosTiposService;

    @GetMapping("/findAll/{idEmpresa}")
    public ResponseEntity<Page<DocumentosTiposDTO>> findAll(@PathVariable Long idEmpresa,
                                                            @RequestParam(name = "docCodigo", required = false) String docCodigo,
                                                            @RequestParam(name = "descripcion", required = false) String descripcion,
                                                            @RequestParam(name = "codigoSunat", required = false) String codigoSunat,
                                                            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                                            @RequestParam(required = false) String sort) {
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
        return ResponseEntity.ok(documentosTiposService.findAllByEmpresa(docCodigo, descripcion, codigoSunat, pageable, idEmpresa));
    }

    @GetMapping("/all")
    public ResponseEntity<List<DocumentosTiposDTO>> findAll() {
        return ResponseEntity.ok(documentosTiposService.findAll());
    }
    @GetMapping("/find-by-empresa/{empresa}")
    public ResponseEntity<List<DocumentosTiposDTO>> findByIdEmpresa(@PathVariable Long empresa) {
        return ResponseEntity.ok(documentosTiposService.findByIdEmpresa(empresa));
    }

    @GetMapping("/find/{docCodigo}/{empresa}")
    public ResponseEntity<Optional<DocumentosTiposDTO>> findById(@PathVariable String docCodigo, @PathVariable Long empresa) {
        return ResponseEntity.ok(documentosTiposService.findById(docCodigo, empresa));
    }

    @PostMapping("/save")
    public ResponseEntity<DocumentosTiposDTO> save(@RequestBody DocumentosTiposDTO documentosTiposDTO) {
        return ResponseEntity.ok(documentosTiposService.save(documentosTiposDTO));
    }

    @DeleteMapping("/delete/{docCodigo}/{empresa}")
    public ResponseEntity<Void> delete(@PathVariable String docCodigo, @PathVariable Long empresa) {
        documentosTiposService.deleteById(docCodigo, empresa);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{docCodigo}/{empresa}")
    public ResponseEntity<DocumentosTiposDTO> update(@PathVariable String docCodigo, @PathVariable Long empresa, @RequestBody DocumentosTiposDTO documentosTiposDTO) {
        return ResponseEntity.ok(documentosTiposService.update(docCodigo, empresa, documentosTiposDTO));
    }
}
