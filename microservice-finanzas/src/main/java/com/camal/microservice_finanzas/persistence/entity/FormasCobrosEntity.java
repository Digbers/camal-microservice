package com.camal.microservice_finanzas.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "formas_cobros")
public class FormasCobrosEntity {
    @Id
    @Column(unique = true, nullable = false, length = 3)
    private String codigo;
    @Column(nullable = false, length = 100)
    private String descripcion;
    @Column(name = "id_empresa", nullable = false)
    private Long idEmpresa;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_moneda", nullable = false)
    private MonedasEntity moneda;
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
