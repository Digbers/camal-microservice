package com.microservice.empresas.aspect;

import com.microservice.empresas.persistence.entity.*;
import com.microservice.empresas.persistence.repository.*;
import com.microservice.empresas.request.ReniectRequest;
import com.microservice.empresas.response.EntidadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class EntidadesAspect {
    private final IEntidadRepository entidadRepository;
    private final IEmpresaRepository empresaRepository;
    private final IDocumentosTiposRepository documentoTiposRepository;
    private final IEntidadesTiposRepository entidadesTiposRepository;

    @Around("execution(* com.microservice.empresas.service.EntidadesSService.findByNumeroDocTipoDocumento(..)) && args(numeroDoc, tipoDocumento, empresa)")
    public Object guardarResultadoEntidadRUCAround( ProceedingJoinPoint joinPoint, String numeroDoc, String tipoDocumento, Long empresa) throws Throwable {
        // Llama al método objetivo y obtiene el resultado
        Object result = joinPoint.proceed();
        if (result instanceof EntidadResponse entidadResponse) {
            log.info("Entro al ASPECTO con @Around");
            try {
                // Verificar si la entidad ya existe en la base de datos
                Optional<EmpresaEntity> empresaExistente = empresaRepository.findById(empresa);
                if (empresaExistente.isPresent()) {
                    EmpresaEntity empresaEntity = empresaExistente.get();

                    Optional<EntidadEntity> entidadExistente = entidadRepository.findByEmpresaAndNroDocumento(empresaEntity, entidadResponse.getNumeroDocumento());
                    if (entidadExistente.isPresent()) {
                        //log.info("La entidad ya existe en la base de datos. No se realizará el guardado.");
                        entidadResponse.setId(entidadExistente.get().getId());
                        //log.info("nombre aspect: " + entidadResponse.getNombre());
                        log.info("La entidad ya existe en la base de datos. Se ha asignado el ID: " + entidadResponse.getId());
                    } else {
                        List<EntidadesTiposEntity> listEntidades = new ArrayList<>();
                        // Guardar la entidad si no existe
                        DocumentoTiposEntity documentoTipo = documentoTiposRepository.findByEmpresaAndDocCodigo(empresa, "RUC")
                                .orElseThrow(() -> new IllegalArgumentException("Documento tipo no encontrado con codigo RUC"));
                        EntidadesTiposEntity entidadestipo = entidadesTiposRepository.findByEmpresaAndTipoCodigo(empresa, "CLI")
                                .orElseThrow(() -> new IllegalArgumentException("Entidades tipo no encontrado para el codigo CLI"));
                        listEntidades.add(entidadestipo);
                        EntidadEntity nuevaEntidad = EntidadEntity.builder()
                                .empresa(empresaEntity)
                                .nombre(entidadResponse.getNombre())
                                .documentoTipo(documentoTipo)
                                .nroDocumento(entidadResponse.getNumeroDocumento())
                                .entidadesTiposList(listEntidades)
                                .direccion(entidadResponse.getDireccion())
                                .estado(entidadResponse.getEstado().equals("ACTIVO"))
                                .condicion(entidadResponse.getCondicion())
                                .build();
                        EntidadEntity entidadGuardada = entidadRepository.save(nuevaEntidad);
                        // Asignar el ID de la entidad guardada
                        entidadResponse.setId(entidadGuardada.getId());
                        log.info("Entidad guardada exitosamente en la base de datos con ID: " + entidadResponse.getId());
                    }
                } else {
                    log.warn("Empresa no encontrada en la base de datos.");
                }
            } catch (Exception e) {
                log.error("Error al guardar entidad: ", e);
            }
        }
        return result;
    }
    @Around("execution(* com.microservice.empresas.service.EntidadesSService.findEntidadesByDNI(..)) && args(reniectRequest, empresa)")
    public Object guardarResultadoEntidadDNIAround(ProceedingJoinPoint joinPoint, ReniectRequest reniectRequest, Long empresa) throws Throwable {
        // Llama al método objetivo y obtiene el resultado
        Object result = joinPoint.proceed();
        if (result instanceof EntidadResponse entidadResponse) {
            log.info("Entro al ASPECTO con @Around");
            try {
                // Verificar si la entidad ya existe en la base de datos
                Optional<EmpresaEntity> empresaExistente = empresaRepository.findById(empresa);
                if (empresaExistente.isPresent()) {
                    EmpresaEntity empresaEntity = empresaExistente.get();
                    // Verificar si la entidad ya existe en la base de datos FALTA PROBARLO
                    Optional<EntidadEntity> entidadExistente = entidadRepository.findByNroDocumentoAndDocumentoTipo_DocCodigo(entidadResponse.getNumeroDocumento(), "DNI");
                    if (entidadExistente.isPresent()) {
                        entidadResponse.setId(entidadExistente.get().getId());
                        log.info("La entidad ya existe en la base de datos. Se ha asignado el ID: " + entidadResponse.getId());
                    } else {
                        List<EntidadesTiposEntity> listEntidades = new ArrayList<>();
                        // Guardar la entidad si no existe
                        DocumentoTiposEntity documentoTipo = documentoTiposRepository.findByEmpresaAndDocCodigo(empresa, "DNI")
                                .orElseThrow(() -> new IllegalArgumentException("Documento tipo no encontrado con codigo DNI"));
                        EntidadesTiposEntity entidadestipo = entidadesTiposRepository.findByEmpresaAndTipoCodigo(empresa, "CLI")
                                .orElseThrow(() -> new IllegalArgumentException("Entidades tipo no encontrado para el codigo CLI"));
                        listEntidades.add(entidadestipo);
                        EntidadEntity nuevaEntidad = EntidadEntity.builder()
                                .empresa(empresaEntity)
                                .nombre(entidadResponse.getNombre())
                                .documentoTipo(documentoTipo)
                                .nroDocumento(entidadResponse.getNumeroDocumento())
                                .direccion(entidadResponse.getDireccion())
                                .estado(true)
                                .condicion(entidadResponse.getCondicion())
                                .entidadesTiposList(listEntidades)
                                .build();
                        EntidadEntity entidadGuardada = entidadRepository.save(nuevaEntidad);
                        // Asignar el ID de la entidad guardada
                        entidadResponse.setId(entidadGuardada.getId());
                        log.info("Entidad guardada exitosamente en la base de datos con ID: " + entidadResponse.getId());
                    }
                } else {
                    log.warn("Empresa no encontrada en la base de datos.");
                }
            } catch (Exception e) {
                log.error("Error al guardar entidad: ", e);
            }

        }
        return result;
    }

}
