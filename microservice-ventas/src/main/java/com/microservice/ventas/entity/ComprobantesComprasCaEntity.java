package com.microservice.ventas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private LocalDate fechaEmision;
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;
    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;
    @Column(name = "periodo_registro", nullable = false)
    private LocalDate periodoRegistro;
    private String observacion;
    @Column(name = "codigo_moneda", nullable = false)
    private String codigoMoneda;
    @Column(name = "tipo_cambio", nullable = false)
    private Double tipoCambio;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "comprobante_tipo", nullable = false)
    private ComprobantesTiposComprasEntity comprobantesTiposEntity;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "estado")
    private ComprobantesComprasEstadosEntity comprobanteCompraEstadosEntity;
    @Column(name = "estado_creacion")
    private String estadoCreacion;
    @Column(name = "id_punto_venta", nullable = false)
    private Long idPuntoVenta;
    @OneToMany(mappedBy = "comprobantesComprasCaEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComprobantesComprasDetalleEntity> comprobantesComprasDetalleEntity = new ArrayList<>();
    @OneToMany(mappedBy = "comprobanteCabeceraEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComprobantesComprasCuotasEntity> comprobantesComprasCuotaEntity = new ArrayList<>();
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
