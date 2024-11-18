package com.microservice.inventario.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private Long numero;
    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;
    @Column(precision = 10, scale = 2)
    private BigDecimal total;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "cod_motivo", nullable = false)
    private MovimientosMotivosEntity motivoCodigo;//motivo venta, compra, traslado
    @Column(name = "id_usuario")
    private String idUsuario;
    @Column(name = "moneda_codigo")
    private String monedaCodigo;
    @OneToMany(mappedBy = "idMovimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimientosDetallesEntity> movimientosDetallesEntity = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "id_almacen")
    private AlmacenEntity idAlmacen;
    @Column(name = "tipo_doc_ref")
    private String tipoDocumentoReferencia;
    @Column(name = "serie_doc_ref")
    private String serieDocumentoReferencia;
    @Column(name = "numero_doc_ref")
    private String numeroDocumentoReferencia;
    private String observaciones;
    @Column(name = "id_entidad")
    private Long idEntidad;
    @Column(name = "cantidad_envaces")
    private Integer cantidadEnvaces;
    @Column(name = "fecha_ingreso_salida")
    private LocalDate fechaIngresoSalida;
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
