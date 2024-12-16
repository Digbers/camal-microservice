package com.microservice.empresas.service.impl;

import com.microservice.empresas.config.UploadProperties;
import com.microservice.empresas.controller.dto.EmpresaDTO;
import com.microservice.empresas.persistence.entity.EmpresaEntity;
import com.microservice.empresas.persistence.especification.EmpresaEspecification;
import com.microservice.empresas.persistence.repository.IEmpresaRepository;
import com.microservice.empresas.response.EmpresaResponseDTO;
import com.microservice.empresas.service.IEmpresaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmpresaServiceImpl implements IEmpresaService {

    private final IEmpresaRepository empresaRepository;
    private final ModelMapper modelMapper;
    private final ResourceLoader resourceLoader;
    private final UploadProperties uploadProperties;

    @Override
    public Page<EmpresaDTO> findAllByEmpresa(String razonSocial, String empresaCodigo, String ruc, String direccion, String departamento, String provincia, String distrito, String ubigeo, String telefono, String celular, String correo, String web, String logo, Boolean estado, Pageable pageable, Long idEmpresa) {
        try{
            //String uploadDir = uploadProperties.getLogosDir();
            log.info("uploadDir: " + uploadProperties.getLogosDir());
            Specification<EmpresaEntity> specification = EmpresaEspecification.getEmpresas(razonSocial, empresaCodigo, ruc, direccion, departamento, provincia, distrito, ubigeo, telefono, celular, correo, web, logo, estado);
            return empresaRepository.findAll(specification, pageable).map(empresa -> {

                if (empresa.getLogo() != null && !empresa.getLogo().isEmpty()) {
                    empresa.setLogo("http://localhost:8080/logos/" + empresa.getLogo());
                }
                return modelMapper.map(empresa, EmpresaDTO.class);
            });
        } catch (Exception e) {
            log.error("Error al obtener la lista de empresas", e);
            throw new RuntimeException("Error al obtener la lista de empresas" + e.getMessage());
        }
    }

    @Override
    public List<EmpresaEntity> findAll() {
        return (List<EmpresaEntity>) empresaRepository.findAll();
    }

    @Override
    public Optional<EmpresaEntity> findById(Long id) {
        try {
            return empresaRepository.findById(id);
        } catch (Exception e) {
            log.error("Error al obtener la empresa con id: " + id, e);
            throw new RuntimeException("Error al obtener la empresa con id: " + id + e.getMessage());
        }
    }

    @Override
    public EmpresaDTO save(EmpresaDTO empresaDTO, MultipartFile logo) {
        String logoUrl = null;
        try {
            if (logo != null && !logo.isEmpty()) {
                logoUrl = saveLogo(logo);
                empresaDTO.setLogo(logoUrl);
            }
            EmpresaEntity empresaEntity = modelMapper.map(empresaDTO, EmpresaEntity.class);
            EmpresaEntity empresa = empresaRepository.save(empresaEntity);
            return modelMapper.map(empresa, EmpresaDTO.class);
        } catch (IOException e) {
            log.error("Error al guardar la empresa", e);
            throw new RuntimeException("Error al guardar la empresa" + e.getMessage());
        } catch (Exception e) {
            log.error("Error al guardar la empresa", e);

            // Si ya se guardó el logo, eliminarlo
            if (logoUrl != null) {
                deleteLogo(logoUrl);
            }
            throw new RuntimeException("Error al guardar la empresa: " + e.getMessage());
        }
    }
    private String saveLogo(MultipartFile file) throws IOException {
        String uploadDir = uploadProperties.getLogosDir();
        Resource resource = resourceLoader.getResource("file:" + uploadDir);
        File directory = resource.getFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(directory.getAbsolutePath(), fileName);
        Files.write(filePath, file.getBytes());

        return fileName; // Retorna solo el nombre del archivo
    }
    private void deleteLogo(String logoFileName) {
        try {
            String uploadDir = uploadProperties.getLogosDir();
            Resource resource = resourceLoader.getResource("file:" + uploadDir);
            File file = new File(resource.getFile(), logoFileName);

            if (file.exists() && file.isFile()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    log.warn("No se pudo eliminar el archivo: " + logoFileName);
                } else {
                    log.info("Archivo eliminado correctamente: " + logoFileName);
                }
            } else {
                log.warn("El archivo no existe o no es un archivo válido: " + logoFileName);
            }
        } catch (IOException e) {
            log.error("Error al intentar eliminar el archivo: " + logoFileName, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        empresaRepository.deleteById(id);
    }

    @Override
    public EmpresaDTO update(Long id, EmpresaDTO empresaDTO, MultipartFile logo) {
        EmpresaEntity empresa = empresaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada con id: " + id));
        try {
            if(empresa.getLogo() != null) {
                deleteLogo(empresa.getLogo());
            }
            empresa.setEmpresaCodigo(empresaDTO.getEmpresaCodigo());
            empresa.setDistrito(empresaDTO.getDistrito());
            empresa.setDepartamento(empresaDTO.getDepartamento());
            empresa.setProvincia(empresaDTO.getProvincia());
            empresa.setCelular(empresaDTO.getCelular());
            empresa.setCorreo(empresaDTO.getCorreo());
            empresa.setDireccion(empresaDTO.getDireccion());
            empresa.setEstado(empresaDTO.getEstado());
            empresa.setRazonSocial(empresaDTO.getRazonSocial());
            empresa.setRuc(empresaDTO.getRuc());
            empresa.setTelefono(empresaDTO.getTelefono());
            empresa.setUbigeo(empresaDTO.getUbigeo());
            empresa.setUsuarioActualizacion(empresaDTO.getUsuarioActualizacion());
            empresa.setWeb(empresaDTO.getWeb());
            if (logo != null && !logo.isEmpty()) {
                String logoUrl = saveLogo(logo);
                empresa.setLogo(logoUrl);
            }
            EmpresaEntity empresas = empresaRepository.save(empresa);
            return modelMapper.map(empresas, EmpresaDTO.class);
        } catch (IOException e) {
            log.error("Error al actualizar la empresa", e);
            throw new RuntimeException("Error al actualizar la empresa" + e.getMessage());
        }
    }

    @Override
    public List<EmpresaResponseDTO> findAllByIds(Set<Long> empresaIds) {
        List<EmpresaEntity> empresas = empresaRepository.findAllById(empresaIds);
        return empresas.stream()
                .map(empresa -> modelMapper.map(empresa, EmpresaResponseDTO.class))
                .collect(Collectors.toList());
    }
}
