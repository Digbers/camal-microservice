package com.camal.microservice_finanzas.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "monedas")
public class MonedasEntity {
    @Id
    @Column(unique = true, nullable = false, length = 3)
    private String codigo;
    @Column(name = "id_empresa", nullable = false)
    private Long idEmpresa;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(nullable = false, length = 3)
    private String simbolo;
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
