package com.microservice.ventas.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comprobantes_ventas_cuotas")
public class ComprobantesVentasCuotasEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_empresa", nullable = false)
    private Long idEmpresa;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comprobante_venta_cab", nullable = false, referencedColumnName = "id")
    private ComprobantesVentasCabEntity comprobanteCabeceraEntity;
    @Column(nullable = false, name = "nro_cuota")
    private Integer nroCuota;
    @Column(nullable = false, name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
    @Column(precision = 10, scale = 2)
    private BigDecimal importe;
    @Column(name = "id_moneda", nullable = false)
    private String codigoMoneda;
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
