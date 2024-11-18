package com.microservice.empresas.persistence.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "entidades_tipos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"empresa_id", "tipo_codigo"})
})
public class EntidadesTiposEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id; // Nuevo campo para el ID único

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaEntity empresa;

    @Column(name = "tipo_codigo", nullable = false, length = 3)
    private String tipoCodigo;

    private String descripcion;

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

    @ManyToMany(mappedBy = "entidadesTiposList")
    private List<EntidadEntity> entidadesEntityList;
}

