package com.microservice.empresas.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.empresas.config.UploadProperties;
import com.microservice.empresas.controller.dto.EmpresaDTO;
import com.microservice.empresas.persistence.entity.EmpresaEntity;
import com.microservice.empresas.service.IEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final IEmpresaService empresaService;
    private final UploadProperties uploadProperties;

    @GetMapping("/logos-dir")
    public ResponseEntity<String> logosDir() {
        return ResponseEntity.ok(uploadProperties.getLogosDir());
    }
    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(empresaService.findAll());
    }

    @GetMapping("/findAll/{idEmpresa}")
    public ResponseEntity<Page<EmpresaDTO>> findAll(@PathVariable Long idEmpresa,
                                                     @RequestParam(name = "razonSocial", required = false) String razonSocial,
                                                     @RequestParam(name = "empresaCodigo", required = false) String codigo,
                                                     @RequestParam(name = "ruc", required = false) String ruc,
                                                     @RequestParam(name = "direccion", required = false) String direccion,
                                                     @RequestParam(name = "departamento", required = false) String departamento,
                                                     @RequestParam(name = "provincia", required = false) String provincia,
                                                     @RequestParam(name = "distrito", required = false) String distrito,
                                                     @RequestParam(name = "ubigeo", required = false) String ubigeo,
                                                     @RequestParam(name = "telefono", required = false) String telefono,
                                                     @RequestParam(name = "celular", required = false) String celular,
                                                     @RequestParam(name = "correo", required = false) String correo,
                                                     @RequestParam(name = "web", required = false) String web,
                                                     @RequestParam(name = "logo", required = false) String logo,
                                                     @RequestParam(name = "estado", required = false) Boolean estado,
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
        return ResponseEntity.ok(empresaService.findAllByEmpresa(razonSocial, codigo, ruc, direccion, departamento, provincia, distrito, ubigeo, telefono, celular, correo, web, logo, estado, pageable, idEmpresa));
    }

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmpresaDTO> save(
            @RequestPart("empresa") String empresa, // Cambia temporalmente a String para inspección.
            @RequestPart(value = "logo") MultipartFile logo
    ) throws JsonProcessingException {
        System.out.println("Empresa JSON: " + empresa);
        if (logo != null) {
            System.out.println("Archivo: " + logo.getOriginalFilename());
        }
        EmpresaDTO empresaDTO = new ObjectMapper().readValue(empresa, EmpresaDTO.class); // Parse manual.
        return ResponseEntity.ok(empresaService.save(empresaDTO, logo));
    }


    @PostMapping("/findAllByIds")
    public ResponseEntity<?> findAllByIds(@RequestBody Set<Long> empresaIds) {
        return ResponseEntity.ok(empresaService.findAllByIds(empresaIds));
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<EmpresaEntity> findById(@PathVariable Long id) {
        Optional<EmpresaEntity> empresa = empresaService.findById(id);
        if(!empresa.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(empresa.get());
    }
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmpresaDTO> update(@PathVariable Long id, @RequestPart("empresa") String empresa,
                                                @RequestPart(value = "logo") MultipartFile logo) throws JsonProcessingException {
        EmpresaDTO empresaDTO = new ObjectMapper().readValue(empresa, EmpresaDTO.class); // Parse manual.
        return ResponseEntity.ok(empresaService.update(id, empresaDTO, logo));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<EmpresaEntity> empresa = empresaService.findById(id);
        if (empresa.isPresent()) {
            empresaService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
