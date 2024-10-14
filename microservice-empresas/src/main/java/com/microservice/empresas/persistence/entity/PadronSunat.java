package com.microservice.empresas.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "padron_sunat")
public class PadronSunat {
    @Id
    @Column(name ="numero_doc",unique = true, nullable = false)
    private String numeroDoc;
    @Column(name = "razon_social", nullable = false)
    private String razonSocial;
    @Column(name = "estado", nullable = false)
    private String estado;
    @Column(name = "condicion", nullable = false)
    private String condicion;
    @Column(name = "domicilio_fiscal", nullable = false)
    private String domiciloFiscal;
}
