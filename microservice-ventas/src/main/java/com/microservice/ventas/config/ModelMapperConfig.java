package com.microservice.ventas.config;

import com.microservice.ventas.controller.DTO.ComprobantesVentasDetDTO;
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
        return maper;
    }
}

