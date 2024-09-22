package com.microservice.ventas.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.Date;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comprobantes_compras_cabeceras")
public class ComprobantesComprasCaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_empresa", nullable = false)
    private Long idEmpresa;
    @Column(nullable = false)
    private String serie;
    private String numero;
    @Column(name = "fecha_emision", nullable = false)
    private Date fechaEmision;
    private String observacion;
    @Column(name = "id_moneda", nullable = false)
    private String idMoneda;
    private Double tipoCambio;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "estado")
    private ComprobantesComprasEstadosEntity comprobanteCompraEstadosEntity;
    @Column(name = "id_proveedor", nullable = false)
    private Long idProveedor;
    @Column(name = "usercodigo_creacion")
    private String usuarioCreacion;
    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private Timestamp fechaCreacion;
    @Column(name = "usercodigo_actualizacion")
    private String usuarioActualizacion;
    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private Timestamp fechaActualizacion;
}
