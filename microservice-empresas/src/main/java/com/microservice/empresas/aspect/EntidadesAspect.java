package com.microservice.empresas.aspect;

import com.microservice.empresas.controller.dto.PadronSunatDTO;
import com.microservice.empresas.persistence.entity.*;
import com.microservice.empresas.persistence.entity.ids.DocumentosTiposId;
import com.microservice.empresas.persistence.repository.IDocumentosTiposRepository;
import com.microservice.empresas.persistence.repository.IEmpresaRepository;
import com.microservice.empresas.persistence.repository.IEntidadRepository;
import com.microservice.empresas.persistence.repository.IPadronSunatRepository;
import com.microservice.empresas.request.EntidadRequest;
import com.microservice.empresas.request.ReniectRequest;
import com.microservice.empresas.service.IEmpresaSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class EntidadesAspect {
    private final IEntidadRepository entidadRepository;
    private final IPadronSunatRepository padronSunatRepository;
    private final ModelMapper modelMapper;
    private final IEmpresaRepository empresaRepository;
    private final IDocumentosTiposRepository documentoTiposRepository;

    @AfterReturning(
            pointcut = "execution(* com.microservice.empresas.service.EntidadesSService.findByNumeroDocTipoDocumento(..))",
            returning = "padronSunatDTO"
    )
    public void guardarResultadoEntidadesRuc(PadronSunatDTO padronSunatDTO) {
        log.info("Entro al ASPECTO");
        if (padronSunatDTO != null) {

            log.info("Guardando entidad : " + padronSunatDTO.getRazonSocial());
            Optional<PadronSunat> padronSunat = padronSunatRepository.findById(padronSunatDTO.getNumeroDoc());
            if(padronSunat.isPresent()){
                log.info("Entidad ya existe en la base de datos.");
                return;
            }
            PadronSunat padron = modelMapper.map(padronSunatDTO, PadronSunat.class);
            padronSunatRepository.save(padron);
            log.info("Entidad guardada exitosamente en la base de datos.");
        } else {
            log.warn("No se encontró información para guardar.");
        }
    }
    @Around("execution(* com.microservice.empresas.service.EntidadesSService.findEntidadesByDNI(..)) && args(reniectRequest, empresa)")
    public Object guardarResultadoEntidadDNIAround(ProceedingJoinPoint joinPoint, ReniectRequest reniectRequest, Long empresa) throws Throwable {
        // Llama al método objetivo y obtiene el resultado
        Object result = joinPoint.proceed();

        if (result instanceof EntidadRequest entidadRequest) {
            log.info("Entro al ASPECTO con @Around");
            if (entidadRequest != null) {
                try {
                    // Verificar si la entidad ya existe en la base de datos
                    Optional<EmpresaEntity> empresaExistente = empresaRepository.findById(empresa);
                    if (empresaExistente.isPresent()) {
                        EmpresaEntity empresaEntity = empresaExistente.get();

                        Optional<EntidadEntity> entidadExistente = entidadRepository.findByEmpresaAndNroDocumento(empresaEntity, entidadRequest.numero());
                        if (entidadExistente.isPresent()) {
                            log.info("La entidad ya existe en la base de datos. No se realizará el guardado.");
                        } else {
                            // Guardar la entidad si no existe
                            DocumentosTiposId documentosTiposId = DocumentosTiposId.builder()
                                    .empresa(empresa)
                                    .docCodigo("DNI")
                                    .build();
                            DocumentoTiposEntity documentoTipo = documentoTiposRepository.findById(documentosTiposId)
                                    .orElseThrow(() -> new IllegalArgumentException("Documento tipo no encontrado con id: " + documentosTiposId));
                            List<EntidadesTiposEntity> listEntidades = List.of(EntidadesTiposEntity.builder()
                                    .empresa(empresaEntity)
                                    .tipoCodigo("DNI")
                                    .build());

                            EntidadEntity nuevaEntidad = EntidadEntity.builder()
                                    .empresa(empresaEntity)
                                    .nombre(entidadRequest.nombres())
                                    .apellidoMaterno(entidadRequest.apellido_materno())
                                    .apellidoPaterno(entidadRequest.apellido_paterno())
                                    .documentoTipo(documentoTipo)
                                    .nroDocumento(entidadRequest.numero())
                                    .entidadesTiposList(listEntidades)
                                    .build();

                            entidadRepository.save(nuevaEntidad);
                            log.info("Entidad guardada exitosamente en la base de datos.");
                        }
                    } else {
                        log.warn("Empresa no encontrada en la base de datos.");
                    }
                } catch (Exception e) {
                    log.error("Error al guardar entidad: ", e);
                }
            } else {
                log.warn("No se encontró información para guardar.");
            }
        }
        return result;
    }

}
