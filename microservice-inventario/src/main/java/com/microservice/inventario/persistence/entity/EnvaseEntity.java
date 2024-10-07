package com.microservice.inventario.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "envase")
public class EnvaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_envase")
    private Long idEnvase;
    @Column(name = "id_empresa", nullable = false)
    private Long idEmpresa;
    @Column (name = "tipo_envase")
    private String tipoEnvase;
    private String descripcion;
    private Integer capacidad; // Cantidad de pollos por envase
    @Column(precision = 10, scale = 2)
    private BigDecimal pesoReferencia;
    private String estado; // Ej: "Vacío", "Lleno", "En Uso"
    @OneToMany(mappedBy = "envase", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private List<StockAlmacen> stockAlmacenList;
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
