package com.microservice.inventario.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimientos_cabeceras")
public class MovimientosCabeceraEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_empresa", nullable = false)
    private Long idEmpresa;
    private String numero;
    private Timestamp fechaEmision;
    @Column(precision = 10, scale = 2)
    private BigDecimal total;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "cod_motivo", nullable = false)
    private MovimientosMotivosEntity motivoCodigo;
    @Column(name = "id_usuario")
    private Long idUsuario;
    @Column(name = "moneda_codigo", nullable = false)
    private String monedaCodigo;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "id_almacen")
    private AlmacenEntity idAlmacen;
    @Column(name = "tipo_doc_ref")
    private String tipoDocumentoReferencia;
    @Column(name = "serie_doc_ref")
    private String serieDocumentoReferencia;
    @Column(name = "numero_doc_ref")
    private String numeroDocumentoReferencia;
    private String observaciones;
    private Long idEntidad;
    private Integer cantidadEnvaces;
    private Timestamp fechaIngresoSalida;
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
