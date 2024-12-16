package com.camal.microservice_finanzas.config;

import com.camal.microservice_finanzas.controller.DTO.ComprobantesVentasCobrosDTO;
import com.camal.microservice_finanzas.controller.DTO.compras.ComprobantesComprasPagosDTO;
import com.camal.microservice_finanzas.persistence.entity.ComprobantesComprasPagosEntity;
import com.camal.microservice_finanzas.persistence.entity.ComprobantesVentasCobrosEntity;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(ComprobantesVentasCobrosEntity.class, ComprobantesVentasCobrosDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getFormasCobrosEntity().getCodigo(), ComprobantesVentasCobrosDTO::setFormasDeCobros);
            mapper.map(src -> src.getMonedasEntity().getCodigo(), ComprobantesVentasCobrosDTO::setMonedas);
        });
        modelMapper.typeMap(ComprobantesComprasPagosEntity.class, ComprobantesComprasPagosDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getFormaPagosEntity().getCodigo(), ComprobantesComprasPagosDTO::setFormaPagosEntity);
            mapper.map(src -> src.getMonedasEntity().getCodigo(), ComprobantesComprasPagosDTO::setMonedasEntity);
        });
        return modelMapper;
    }
}
