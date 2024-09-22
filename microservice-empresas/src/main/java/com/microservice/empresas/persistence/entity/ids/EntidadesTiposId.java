package com.microservice.empresas.persistence.entity.ids;

import lombok.*;

import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class EntidadesTiposId implements Serializable {
    private Long empresa;
    private String tipoCodigo;
}
