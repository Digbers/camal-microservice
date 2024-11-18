package com.microservice.ventas.entity;

import jakarta.persistence.*;
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
@Table(name = "comprobantes_ventas_cabeceras")
public class ComprobantesVentasCabEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_empresa", nullable = false)
    private Long idEmpresa;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "comprobante_tipo", nullable = false)
    private ComprobantesTiposVentasEntity comprobantesTiposEntity;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "facturacion_electronica")
    private FacturacionElectronicaEntity facturacionElectronicaEntity;
    private String serie;
    private String numero;
    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;
    @Column(name = "id_punto_venta", nullable = false)
    private Long idPuntoVenta;
    private String nombreCliente;
    private String numeroDocumentoCliente;
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "estado", nullable = false)
    private ComprobantesVentasEstadoEntity comprobantesVentasEstadoEntity;
    @Column(name = "estado_creacion")
    private String estadoCreacion;

    @OneToMany(mappedBy = "comprobanteCabeceraEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComprobantesVentasDetEntity> comprobantesVentasDetEntity = new ArrayList<>();
    @OneToMany(mappedBy = "comprobanteCabeceraEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComprobantesVentasCuotasEntity> comprobantesVentasCuotas = new ArrayList<>();
    private String observacion;
    @Column(name = "id_moneda", nullable = false)
    private String codigoMoneda;
    @Column(name = "tipo_cambio", nullable = false)
    private Double tipoCambio;
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
