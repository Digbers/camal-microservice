package com.microservice.ventas.config;

import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasDetalleDTO;
import com.microservice.ventas.controller.DTO.ventas.ComprobantesVentasDetDTO;
import com.microservice.ventas.entity.ComprobantesComprasCaEntity;
import com.microservice.ventas.entity.ComprobantesComprasDetalleEntity;
import com.microservice.ventas.entity.ComprobantesVentasDetEntity;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper maper = new ModelMapper();
        maper.typeMap(ComprobantesVentasDetEntity.class, ComprobantesVentasDetDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getComprobanteCabeceraEntity().getId(), ComprobantesVentasDetDTO::setComprobanteCabecera);
        });
        maper.typeMap(ComprobantesComprasDetalleEntity.class, ComprobantesComprasDetalleDTO.class ).addMappings(mapper -> {
            mapper.map(src -> src.getComprobantesComprasCaEntity().getId(), ComprobantesComprasDetalleDTO::setIdComprobanteCabecera);
        });
        return maper;
    }
}

